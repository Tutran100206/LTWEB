"""Integration checks on local SQL Server/Tomcat; isolated fixtures removed in finally."""
import http.cookiejar
import urllib.request
import urllib.parse
import urllib.error
import subprocess
import re
import uuid
from concurrent.futures import ThreadPoolExecutor

BASE = 'http://localhost:8086/de06_24162141'
tag = 'cart_test_' + uuid.uuid4().hex[:10]
checks = 0

def sql(query):
    result = subprocess.run(['sqlcmd', '-S', 'localhost', '-E', '-C', '-b', '-d',
                             'DE06_24162141', '-I', '-h', '-1', '-W', '-Q', 'SET NOCOUNT ON; ' + query],
                            capture_output=True, check=True)
    return result.stdout.decode('utf-8', errors='replace').strip()

def check(condition, label):
    global checks
    assert condition, label
    checks += 1
    print('PASS', label)

class Client:
    def __init__(self):
        self.opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))

    def request(self, path, data=None):
        req = urllib.request.Request(BASE + path, None if data is None else urllib.parse.urlencode(data).encode())
        try:
            response = self.opener.open(req, timeout=25)
        except urllib.error.HTTPError as e:
            response = e
        with response:
            return response.status, response.read().decode('utf-8'), response.url

    def token(self, path='/cart'):
        code, body, _ = self.request(path)
        assert code == 200, (path, code)
        return re.search(r'name="csrf" value="([^"]+)"', body).group(1)

    def post(self, path, data, form='/cart'):
        return self.request(path, dict(data, csrf=self.token(form)))

    def login(self, name):
        return self.post('/login', {'username': name, 'password': 'User@123'}, '/login')

try:
    for suffix in ('a', 'b'):
        sql(f"INSERT INTO Users(username,email,fullname,password,phone,status,roleId) SELECT N'{tag+suffix}',N'{tag+suffix}@example.com',N'Test',password,'0901234567',1,1 FROM Users WHERE username='user1'")
    uid = int(sql(f"SELECT userId FROM Users WHERE username='{tag}a'"))
    sql(f"INSERT INTO Product(productName,price,stock,status) VALUES(N'{tag}',100000,5,1),(N'{tag}_second',200000,3,1)")
    pid = int(sql(f"SELECT productId FROM Product WHERE productName='{tag}'"))
    second = int(sql(f"SELECT productId FROM Product WHERE productName='{tag}_second'"))
    a, b, guest = Client(), Client(), Client()
    check(guest.request('/cart')[2].endswith('/login'), 'Guest must log in')
    check(a.login(tag+'a')[2].endswith('/home'), 'Buyer login')
    b.login(tag+'b')
    check(a.request('/cart/add')[0] == 405, 'GET mutation rejected')
    check(a.request('/cart/add', {'productId': pid, 'quantity': 1})[0] == 403, 'Missing CSRF rejected')
    code, body, _ = a.request('/products')
    check(code == 200 and 'Thêm vào giỏ' in body, 'Product list purchase form compiles')
    check(a.request('/product-detail?id='+str(pid))[0] == 200, 'Detail purchase form compiles')
    for qty in ('0', '-1', '1.5', '2147483648', '6'):
        a.post('/cart/add', {'productId': pid, 'quantity': qty})
        check(sql(f'SELECT COUNT(*) FROM CartItem i JOIN Cart c ON i.cartId=c.cartId WHERE c.userId={uid}') == '0', 'Invalid quantity '+qty)
    a.post('/cart/add', {'productId': pid, 'quantity': 2})
    a.post('/cart/add', {'productId': pid, 'quantity': 1})
    check(sql(f'SELECT quantity FROM CartItem i JOIN Cart c ON i.cartId=c.cartId WHERE c.userId={uid}') == '3', 'Repeated add merges quantities')
    a.post('/cart/update', {'productId': pid, 'quantity': 4})
    check(sql(f'SELECT quantity FROM CartItem i JOIN Cart c ON i.cartId=c.cartId WHERE c.userId={uid}') == '4', 'Update quantity')
    a.post('/cart/add', {'productId': second, 'quantity': 1})
    a.post('/cart/remove', {'productId': second})
    check(sql(f'SELECT COUNT(*) FROM CartItem i JOIN Cart c ON i.cartId=c.cartId WHERE c.userId={uid}') == '1', 'Remove one line')
    a.post('/cart/clear', {})
    check(sql(f'SELECT COUNT(*) FROM CartItem i JOIN Cart c ON i.cartId=c.cartId WHERE c.userId={uid}') == '0', 'Clear cart')
    a.post('/cart/add', {'productId': pid, 'quantity': 2})
    a.post('/cart/add', {'productId': second, 'quantity': 2})
    cid = sql(f'SELECT cartId FROM Cart WHERE userId={uid} AND status=0')
    shipping = {'cartId': cid, 'recipientName': 'Test Buyer', 'phone': '0901234567', 'address': 'Test address'}
    check(a.request('/checkout')[0] == 200, 'Checkout JSP compiles')
    a.post('/checkout', dict(shipping, phone='bad'), '/checkout')
    check(sql(f'SELECT status FROM Cart WHERE cartId=\'{cid}\'') == '0', 'Invalid shipping rejected')
    sql(f'UPDATE Product SET stock=1 WHERE productId={second}')
    a.post('/checkout', shipping, '/checkout')
    check(sql(f'SELECT stock FROM Product WHERE productId={pid}') == '5', 'Partial stock deduction rolls back on shortage')
    check(sql(f'SELECT status FROM Cart WHERE cartId=\'{cid}\'') == '0', 'Failed checkout preserves draft')
    sql(f'UPDATE Product SET stock=3 WHERE productId={second}')
    token = a.token('/checkout')
    def place(_):
        return a.request('/checkout', dict(shipping, csrf=token))
    with ThreadPoolExecutor(max_workers=2) as pool:
        results = list(pool.map(place, range(2)))
    check(any(url.endswith('/orders') for _, _, url in results), 'COD checkout succeeds')
    check(sql(f'SELECT stock FROM Product WHERE productId={pid}') == '3', 'Concurrent duplicate checkout deducts stock once')
    check(sql(f"SELECT COUNT(*) FROM Cart WHERE userId={uid} AND status=1 AND paymentMethod='COD'") == '1', 'Exactly one COD order')
    check(cid not in b.request('/orders')[1], 'Other buyer cannot view order')
    sql(f'UPDATE Product SET price=999999 WHERE productId={pid}')
    check(sql(f"SELECT unitPrice FROM CartItem WHERE cartId='{cid}' AND productId={pid}").startswith('100000'), 'Order preserves checkout price')
    for status, label in enumerate(('Đơn hàng mới','Đã xác nhận','Chuẩn bị hàng','Vận chuyển','Giao hàng','Đã giao','Đơn hàng hủy','Đơn hàng hoàn'), 1):
        sql(f"UPDATE Cart SET status={status} WHERE cartId='{cid}'")
        code, body, _ = a.request('/orders?status='+str(status))
        check(code == 200 and f'data-status="{status}"' in body and label in body, 'Database status reflected '+str(status))
        check(cid not in a.request('/orders?status='+str(status % 8 + 1))[1], 'Filter excludes other status '+str(status))
    check(a.request('/orders?status=9')[0] == 400, 'Invalid status rejected')
    print('SUCCESS:', checks, 'checks')
finally:
    sql(f"DELETE i FROM CartItem i JOIN Cart c ON i.cartId=c.cartId JOIN Users u ON c.userId=u.userId WHERE u.username IN ('{tag}a','{tag}b'); DELETE c FROM Cart c JOIN Users u ON c.userId=u.userId WHERE u.username IN ('{tag}a','{tag}b'); DELETE FROM Users WHERE username IN ('{tag}a','{tag}b'); DELETE FROM Product WHERE productName IN ('{tag}','{tag}_second');")
