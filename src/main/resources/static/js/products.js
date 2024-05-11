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
    img.src = `/img/${imageMap[product.category_id] || 'broken-image.png'}`;
    img.alt = product.name;

    const infoDiv = document.createElement('div');
    infoDiv.className = 'short-product-info';

    const nameP = document.createElement('p');
    nameP.id = 'name';
    nameP.textContent = product.name;

    const priceP = document.createElement('p');
    const priceSpan = document.createElement('span');
    const tickerSpan = document.createElement('span');

    priceP.className = 'price-container';
    priceSpan.textContent = product.price.toFixed(2);
    priceSpan.className = "price"
    tickerSpan.className = "price-ticker";
    tickerSpan.innerText = "SOL"
    priceP.appendChild(priceSpan);
    priceP.appendChild(tickerSpan);

    infoDiv.appendChild(nameP);
    infoDiv.appendChild(priceP);
    productDiv.appendChild(img);
    productDiv.appendChild(infoDiv);

    return productDiv;
}