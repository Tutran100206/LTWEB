"""Download the product illustration sources recorded in docs/product-images.md."""
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor
import urllib.request

UNSPLASH = 'https://images.unsplash.com/'
SOURCES = [
    (100001, 'laptop-office.jpg', UNSPLASH+'photo-1496181133206-80ce9b88a853?w=900&q=85&fit=max&fm=jpg'),
    (100002, 'laptop-pro.jpg', UNSPLASH+'photo-1517336714731-489689fd1ca8?w=900&q=85&fit=max&fm=jpg'),
    (100003, 'phone-nova.jpg', UNSPLASH+'photo-1511707171634-5f897ff02aa9?w=900&q=85&fit=max&fm=jpg'),
    (100004, 'earbuds.jpg', UNSPLASH+'photo-1606220945770-b5b6c2c55bf1?w=900&q=85&fit=max&fm=jpg'),
    (100005, 'keyboard.jpg', UNSPLASH+'photo-1618384887929-16ec33fab9ef?w=900&q=85&fit=max&fm=jpg'),
    (100006, 'mouse.jpg', UNSPLASH+'photo-1527814050087-3793815479db?w=900&q=85&fit=max&fm=jpg'),
    (100007, 'laptop-slim.jpg', UNSPLASH+'photo-1516387938699-a93567ec168e?w=900&q=85&fit=max&fm=jpg'),
    (100008, 'phone-mini.jpg', UNSPLASH+'photo-1592750475338-74b7b21085ab?w=900&q=85&fit=max&fm=jpg'),
    (100009, 'speaker.jpg', UNSPLASH+'photo-1608043152269-423dbba4e7e1?w=900&q=85&fit=max&fm=jpg'),
    (100010, 'charger.jpg', 'https://ueeshop.ly200-cdn.com/u_file/UPAR/UPAR392/2209/products/21/cb37af81b2.jpg.500x500.jpg'),
    (100011, 'headphones.jpg', UNSPLASH+'photo-1546435770-a3e426bf472b?w=900&q=85&fit=max&fm=jpg'),
    (100012, 'laptop-stand.jpg', 'https://www.nillkin.com/cdn/shop/products/ProDeskAdjustableLaptopStand-Sliver.jpg?width=1000'),
]
ROOT=Path(__file__).resolve().parents[1]
def download(item):
    code,name,url=item
    request=urllib.request.Request(url,headers={'User-Agent':'Mozilla/5.0'})
    with urllib.request.urlopen(request,timeout=40) as response:
        if not response.headers.get('Content-Type','').startswith('image/'):
            raise ValueError('Not an image: '+name)
        data=response.read()
    target=ROOT/'src/main/webapp/assets/products'/name
    target.parent.mkdir(parents=True,exist_ok=True)
    target.write_bytes(data)
    print(name,len(data),'bytes')

if __name__=='__main__':
    with ThreadPoolExecutor(max_workers=4) as pool:
        list(pool.map(download,SOURCES))
