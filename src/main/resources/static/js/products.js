function generatePreviewImageForProduct(product) {
    const imageMap = {
        1: 'code.png',
        2: 'asset.png',
        3: 'intellectual-property.png',
        4: 'coupon.png'
    };
    const img = document.createElement('img');
    img.src = `/img/${imageMap[product.categoryId] || 'broken-image.png'}`;
    img.alt = product.name;

    return img;
}

async function createProductElement(product) {
    const productDiv = document.createElement('div');
    productDiv.className = 'product';

    const infoDiv = document.createElement('div');
    infoDiv.className = 'short-product-info';

    const nameP = document.createElement('p');
    nameP.id = 'name';
    nameP.textContent = product.name;

    const priceP = document.createElement('p');
    priceP.className = 'price-container';

    // SOL price and ticker
    const priceSpan1 = document.createElement('span');
    const tickerSpan1 = document.createElement('span');
    priceSpan1.textContent = product.price.toFixed(2);
    priceSpan1.className = 'price';
    tickerSpan1.className = 'price-ticker';
    tickerSpan1.innerText = 'SOL';

    // USD price and ticker
    const priceSpan2 = document.createElement('span');
    const tickerSpan2 = document.createElement('span');
    priceSpan2.textContent = (parseFloat(product.price) * parseFloat(await getCurrentSolanaPrice())).toFixed(2);
    priceSpan2.className = 'price';
    priceSpan2.style.marginLeft = "10px";
    priceSpan2.style.fontSize = "12px";
    tickerSpan2.className = 'price-ticker';
    tickerSpan2.innerText = 'USD';

    // Append both sets of price and ticker
    priceP.appendChild(priceSpan1);
    priceP.appendChild(tickerSpan1);
    priceP.appendChild(priceSpan2);
    priceP.appendChild(tickerSpan2);

    infoDiv.appendChild(nameP);
    infoDiv.appendChild(priceP);
    productDiv.appendChild(generatePreviewImageForProduct(product));
    productDiv.appendChild(infoDiv);

    if (isProductOwner(product)) {
        productDiv.classList.add('my-listed-product');
    }

    return productDiv;
}


function isProductOwner(product) {
    return walletManager.wallet !== null && walletManager.getWalletString() === product.walletId;
}

async function getProductOwnerWalletByProductId(productId) {
    let walletID;

    try {
        const response = await fetch("/api/users/get-product-owner-wallet-by-product-id/" + productId, {
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
async function getProductByProductId(productId) {
    let product;
    try {
        const response = await fetch("/api/products/" + productId, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error("Couldn't get product info: " + response.statusText);
        }

        product = await response.json();
    } catch (error) {
        createToast("warning", error);
    }

    return product;
}

async function createProductInfoPopup(product) {
    let productDiv = await createProductElement(product);
    if (isProductOwner(product)) {
        return productDiv;
    }
    productDiv.addEventListener("click", function () {
        document.getElementById("popup-product-id").textContent = product.name;
        document.getElementById("overlay").style.display = 'block';
        document.getElementById("popup").style.display = 'block';

        document.getElementById("popup-product-image").src = productDiv.querySelector('img').src;
        document.getElementById("popup-product-description").textContent = product.description;
        document.getElementById("popup-product-price").textContent = product.price;
        document.getElementById("product-status-text").innerText = "This product is on sale now";
        if (product.youtubeLink) {
            const videoId = getYouTubeVideoId(product.youtubeLink);
            if (videoId) {
                document.getElementById('video-container').replaceChildren();
                const iframe = document.createElement('iframe');
                iframe.width = "560";
                iframe.height = "315";
                iframe.src = `https://www.youtube.com/embed/${videoId}`;
                iframe.frameBorder = "0";
                iframe.allow = "accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture";
                iframe.allowFullscreen = true;
                document.getElementById('video-container').appendChild(iframe);
                document.getElementById('video-container').style.display = "flex"
            } else {
                console.log("YouTube URL is not valid");
                document.getElementById('video-container').style.display = "none";
            }
        } else {
            document.getElementById('video-container').style.display = "none";
        }

        let button = document.getElementById("popup-buy-button");
        if (walletManager.wallet) {
            document.getElementById("popup-buy-button-tooltiptext").style.display = "none";
            button.classList.remove("button-disabled");
            button.removeAttribute('title');
            document.getElementById("popup-buy-button").onclick = function () {
                let buyLoader = document.getElementById("buy-loader");
                buyLoader.style.display = "block";
                handleTransferSol(product, buyLoader).then(() => buyLoader.style.display = "none");
            };
        } else {
            button.classList.add("button-disabled");
            document.getElementById("popup-buy-button-tooltiptext").style.display = "block";
        }
    });

    return productDiv;
}

function getYouTubeVideoId(url) {
    const urlObj = new URL(url);
    return urlObj.searchParams.get("v");
}