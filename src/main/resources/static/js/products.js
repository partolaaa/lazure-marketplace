function createProductElement(product) {
    const productDiv = document.createElement('div');
    productDiv.className = 'product';

    const imageMap = {
        1: 'code.png',
        2: 'asset.png',
        3: 'intellectual-property.png',
        4: 'coupon.png'
    };

    const img = document.createElement('img');
    img.src = `/img/${imageMap[product.category_id] || 'logo.png'}`;
    img.alt = product.name;

    const infoDiv = document.createElement('div');
    infoDiv.className = 'short-product-info';

    const nameP = document.createElement('p');
    nameP.id = 'name';
    nameP.textContent = product.name;

    const priceP = document.createElement('p');
    const priceSpan = document.createElement('span');
    priceSpan.className = 'price';
    priceSpan.textContent = product.price.toFixed(2);
    priceP.appendChild(priceSpan);
    priceP.append(' SOL');

    infoDiv.appendChild(nameP);
    infoDiv.appendChild(priceP);
    productDiv.appendChild(img);
    productDiv.appendChild(infoDiv);

    return productDiv;
}