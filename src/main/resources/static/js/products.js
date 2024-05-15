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

async function getProductOwnerWalletByProductId(productId) {
    let walletID;

    try {
        const response = await fetch("api/products/get-product-owner-wallet-by-product-id/" + productId, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error("Couldn't get owner's wallet address: " + response.statusText);
        }

        const user = await response.json();
        walletID = user.walletId;
    } catch (error) {
        createToast("warning", error);
    }

    return walletID;
}

function createProductInfoPopup(product) {
    let productDiv = createProductElement(product);
    productDiv.addEventListener("click", function () {
        document.getElementById("popup-product-id").textContent = product.name;
        document.getElementById("overlay").style.display = 'block';
        document.getElementById("popup").style.display = 'block';

        document.getElementById("popup-product-image").src = productDiv.querySelector('img').src;
        document.getElementById("popup-product-description").textContent = product.description;
        document.getElementById("popup-product-price").textContent = product.price;
        document.getElementById("product-status-text").innerText = "This product is on sale now";

        let button = document.getElementById("popup-buy-button");
        if (walletManager.wallet) {
            document.getElementById("popup-buy-button-tooltiptext").style.display = "none";
            button.classList.remove("button-disabled");
            button.removeAttribute('title');
            document.getElementById("popup-buy-button").onclick = function () {
                let buyLoader = document.getElementById("buy-loader");
                buyLoader.style.display = "block";
                handleTransferSol(product, buyLoader).then(r => buyLoader.style.display = "none");
            };
        } else {
            button.classList.add("button-disabled");
            document.getElementById("popup-buy-button-tooltiptext").style.display = "block";
        }
    });

    return productDiv;
}