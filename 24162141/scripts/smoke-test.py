"""HTTP tests against a running project. Temporary records are deleted after tests.
For --otp, start Tomcat using local SMTP fixture settings documented in README.
Uses only Python standard library; no outgoing email.
"""
import argparse, http.cookiejar, urllib.request, urllib.parse, urllib.error
import re, uuid, socketserver, threading, email, email.policy
parser=argparse.ArgumentParser()
parser.add_argument('--base',default='http://localhost:8086/de06_24162141')
parser.add_argument('--otp',action='store_true')
args=parser.parse_args()
checks=[]
def check(condition,label):
    if not condition: raise AssertionError(label)
    checks.append(label); print('PASS',label)
class Client:
    def __init__(self):
        self.cookies=http.cookiejar.CookieJar()
        self.opener=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(self.cookies))
    def request(self,path,data=None):
        req=urllib.request.Request(args.base+path,None if data is None else urllib.parse.urlencode(data).encode())
        try:r=self.opener.open(req,timeout=25)
        except urllib.error.HTTPError as e:r=e
        with r:return r.status,r.read().decode('utf-8'),r.url
    def token(self,path):
        code,body,url=self.request(path)
        check(code==200,'GET '+path)
        m=re.search(r'name="csrf" value="([^"]+)"',body)
        check(bool(m),'CSRF present '+path)
        return m.group(1)
    def post(self,path,data,form=None):
        token=self.token(form or path)
        return self.request(path,dict(data,csrf=token))
    def login(self,user,pw):return self.post('/login',{'username':user,'password':pw})

messages=[]
class SMTP(socketserver.StreamRequestHandler):
    def handle(self):
        self.wfile.write(b'220 localhost test SMTP\r\n')
        while True:
            line=self.rfile.readline()
            if not line:return
            cmd=line.split(b' ',1)[0].strip().upper()
            if cmd in [b'EHLO',b'HELO']: self.wfile.write(b'250-localhost\r\n250 8BITMIME\r\n')
            elif cmd==b'DATA':
                self.wfile.write(b'354 End with dot\r\n'); data=b''
                while True:
                    row=self.rfile.readline()
                    if row in [b'.\r\n',b'']:break
                    data+=row
                msg=email.message_from_bytes(data,policy=email.policy.default)
                messages.append(msg.get_content()); self.wfile.write(b'250 Queued\r\n')
            elif cmd==b'QUIT':self.wfile.write(b'221 Bye\r\n');return
            else:self.wfile.write(b'250 OK\r\n')

server=None
if args.otp:
    server=socketserver.ThreadingTCPServer(('127.0.0.1',2526),SMTP)
    threading.Thread(target=server.serve_forever,daemon=True).start()
admin=Client(); guest=Client(); user=Client(); seller=Client()
tag='smoke_'+uuid.uuid4().hex[:8]
try:
    code,body,_=guest.request('/home')
    check(code==200 and 'MSSV: 24162141' in body and 'Mã đề: 06' in body and body.count('<header>')==1,'SiteMesh header/footer rendered once')
    check('Trang quản trị' not in body,'Guest admin link hidden')
    code,body,_=guest.request('/products')
    check(code==200 and body.count('class="product panel"')==12 and body.count('class="seller"')==3,'12 database products grouped into 3 sellers')
    check('Laptop Văn Phòng A14' in body and '100001' in body,'Product name and code from SQL Server')
    code,body,_=guest.request('/product-detail?id=1')
    check(code==200 and 'Description' in body and 'Bảo hành 12 tháng' in body,'Product detail JOIN fields and description')
    check(guest.request('/product-detail?id=nope')[0]==400,'Invalid product ID returns 400')
    check(guest.request('/product-detail?id=999999')[0]==404,'Missing product returns 404')
    check(guest.request('/admin')[2].endswith('/login'),'Guest redirected from /admin')
    check(guest.request('/admin/users')[2].endswith('/login'),'Guest redirected from admin users')
    code,body,_=guest.login('admin','wrong')
    check('không đúng' in body,'Wrong password rejected')
    code,body,_=guest.login('locked','User@123')
    check('đã bị khóa' in body,'Inactive account rejected')
    check(user.login('user1','User@123')[2].endswith('/home'),'User home redirect')
    check(user.request('/admin')[0]==403 and user.request('/admin/categories')[0]==403,'User blocked from admin routes')
    code,body,url=seller.login('seller1','User@123')
    check(url.endswith('/seller/home') and 'TRANG CHỦ SELLER' in body and 'Laptop Văn Phòng A14' in body,'Seller home and own products')
    check(admin.login('admin','Admin@123')[2].endswith('/admin'),'Admin login redirect')
    code,body,_=admin.request('/admin/users')
    check(code==200 and 'Trang quản trị' in body and body.count('<tr>')==6 and 'Next' in body,'User pagination page one (5 rows)')
    code,body,_=admin.request('/admin/users?page=2')
    check(code==200 and 'Previous' in body and 'locked' in body,'User pagination page two')
    code,body,_=admin.request('/admin/categories?page=2')
    check(code==200 and 'Danh mục thử xóa' in body and 'Previous' in body,'Category pagination page two')
    check(admin.request('/admin/categories/delete?id=6')[0]==405,'Delete via GET rejected')
    check(admin.request('/admin/categories/delete',{'id':'6'})[0]==403,'Missing CSRF rejected')
    code,body,_=admin.post('/admin/categories/add',{'categoryName':'','images':'','status':'1'})
    check('Tên danh mục phải' in body,'Blank category validation')
    code,body,_=admin.post('/admin/categories/delete',{'id':'1'},'/admin/categories')
    check('đang được tham chiếu' in body,'Referenced category deletion handled')
    code,body,_=admin.post('/admin/categories/add',{'categoryName':tag,'images':'assets/product.svg','status':'1'})
    code,body,_=admin.request('/admin/categories?page=999')
    match=re.search(r'<tr><td>(\d+)</td><td>'+tag+r'</td>',body);check(bool(match),'Create category');cid=match.group(1)
    code,body,_=admin.post('/admin/categories/edit?id='+cid,{'id':cid,'categoryName':tag+'_edit','images':'','status':'0'})
    check(tag+'_edit' in admin.request('/admin/categories?page=999')[1],'Edit category')
    admin.post('/admin/categories/delete',{'id':cid},'/admin/categories')
    check(admin.request('/admin/categories/edit?id='+cid)[0]==404,'Delete category')
    data={'username':tag,'email':tag+'@example.com','fullname':'Kiểm thử Người dùng','phone':'0901234567','images':'','status':'1','roleId':'1','sellerId':'','password':'Smoke@123'}
    admin.post('/admin/users/add',data)
    code,body,_=admin.request('/admin/users?page=999')
    match=re.search(r'<tr><td>\d+</td><td>(\d+)</td><td>'+tag+r'</td>',body);check(bool(match),'Create user');uid=match.group(1)
    code,body,_=admin.post('/admin/users/add',data)
    check('đã được sử dụng' in body,'Duplicate username/email rejected')
    data.update(id=uid,fullname='Đã cập nhật',password='')
    admin.post('/admin/users/edit?id='+uid,data)
    check('Đã cập nhật' in admin.request('/admin/users?page=999')[1],'Edit user')
    check(Client().login(tag,'Smoke@123')[2].endswith('/home'),'Blank edit password preserves hash')
    admin.post('/admin/users/delete',{'id':uid},'/admin/users')
    check(admin.request('/admin/users/edit?id='+uid)[0]==404,'Delete user')
    code,body,_=admin.post('/admin/users/delete',{'id':'1'},'/admin/users')
    check('Không thể tự xóa' in body,'Admin cannot delete own account')
    if args.otp:
        reg=Client(); data={'username':tag+'_otp','email':tag+'@example.com','fullname':'OTP Test','phone':'','password':'Smoke@123'}
        code,body,url=reg.post('/register',data)
        check(url.endswith('/verify-otp') and len(messages)==1,'JavaMail delivers OTP to local SMTP fixture')
        code,body,_=reg.post('/verify-otp',{'otp':'wrong'})
        check('OTP không đúng' in body,'Wrong OTP rejected')
        otp=re.search(r'\b\d{6}\b',messages[0]).group(0)
        code,body,url=reg.post('/verify-otp',{'otp':otp})
        check(url.endswith('/login') and 'Kích hoạt thành công' in body,'OTP activates account')
        check(reg.login(data['username'],'Smoke@123')[2].endswith('/home'),'Activated account can log in')
        body=admin.request('/admin/users?page=999')[1]
        uid=re.search(r'<tr><td>\d+</td><td>(\d+)</td><td>'+data['username']+r'</td>',body).group(1)
        admin.post('/admin/users/delete',{'id':uid},'/admin/users')
    admin.post('/logout',{},'/admin')
    check(admin.request('/admin')[2].endswith('/login'),'Logout invalidates session')
    print('\nSUCCESS:',len(checks),'checks')
finally:
    if server:server.shutdown();server.server_close()
