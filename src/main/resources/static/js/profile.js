walletManager.onWalletReady(() => {
    // TODO: temporary code, should be reworked in future
    let walletOwnerAddress = walletManager.getWalletString();
    let walletPageAddress = document.getElementById("wallet-address").getAttribute('data-wallet');
    let twitterInfo = document.getElementById("profile-info-twitter");
    if (walletOwnerAddress === walletPageAddress) {
        twitterInfo.style.display = "flex";
    } else {
        twitterInfo.style.display = "none";
    }
});

document.getElementById("profile-info-wallet").onclick = function () {
    let walletAddress = document.getElementById("wallet-address");
    let fullWallet = walletAddress.getAttribute('data-wallet');
    navigator.clipboard.writeText(fullWallet);

    walletAddress.innerText = "Copied!";
    setTimeout(() => walletAddress.innerText = walletManager.shortenWalletAddress(fullWallet),
        3000);
};
document.getElementById("profile-info-twitter").onclick = function () {
    createToast("info", "Not available yet.")
};

function setWallet() {
    let walletAddress = document.getElementById("wallet-address");
    let wallet = walletAddress.getAttribute('data-wallet');
    walletAddress.classList.remove("loader-spinner");

    walletAddress.innerText = walletManager.shortenWalletAddress(wallet);
}

setWallet();

function loadTransactions() {
    const walletId = document.getElementById("wallet-address").getAttribute('data-wallet');
    const loaderSpan = document.getElementById("loader-span");
    fetch('/api/transactions?walletId=' + walletId)
        .then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                loaderSpan.classList.remove("loader-spinner");
                loaderSpan.innerText = "No transactions found";
            } else {
                loaderSpan.style.display = "none";
                fillTransactionTable(data);
            }
        })
        .catch(error => {
            console.error(error);
            createToast("error", "Failed to load transactions.");
        });
}

function fillTransactionTable(transactions) {
    const tbody = document.querySelector('#transactions-table tbody');

    for (const transaction of transactions) {
        const row = document.createElement('tr');

        // Short product description
        const productCell = document.createElement('td');
        let product = transaction.productDto;
        const productDescriptionSpan = document.createElement('span');
        const productImage = generatePreviewImageForProduct(product);
        const productNameSpan = document.createElement('span');
        productNameSpan.innerText = product.name;
        productDescriptionSpan.appendChild(productImage);
        productDescriptionSpan.appendChild(productNameSpan);
        productDescriptionSpan.className = "transaction-product-info";
        productDescriptionSpan.onclick = async function () {
            let currentProduct = await getProductByProductId(product.productId);
            const overlay = document.getElementById("overlay");
            const productPopup = document.getElementById("popup");

            document.getElementById("popup-product-id").innerText = currentProduct.name;
            document.getElementById("popup-product-description").innerText = currentProduct.description;
            if (!currentProduct.resourceLink) {
                document.getElementById("popup-source-link-container").style.display = "none";
            } else {
                document.getElementById("source-link").innerText = currentProduct.resourceLink;
                document.getElementById("source-link").href = currentProduct.resourceLink;
            }

            overlay.style.display = "block";
            productPopup.style.display = "block";
        }

        productCell.appendChild(productDescriptionSpan);
        row.appendChild(productCell);

        // Transaction type
        const typeCell = document.createElement('td');
        const typeSpan = document.createElement('span');
        let walletPageAddress = document.getElementById("wallet-address").getAttribute('data-wallet');
        const type = transaction.buyerWalletId === walletPageAddress ? 'BUY' : 'SELL';
        typeSpan.innerText = type;
        typeSpan.classList.add(type.toLowerCase());
        typeCell.appendChild(typeSpan);
        row.appendChild(typeCell);

        // Buyer
        const buyerCell = document.createElement('td');
        buyerCell.textContent = walletManager.shortenWalletAddress(transaction.buyerWalletId);
        row.appendChild(buyerCell);

        // Seller
        const sellerCell = document.createElement('td');
        sellerCell.textContent = walletManager.shortenWalletAddress(transaction.sellerWalletId);
        row.appendChild(sellerCell);

        // TxId
        const txIdCell = document.createElement('td');
        const txIdLink = document.createElement('a');
        txIdLink.href = `https://solscan.io/tx/${transaction.txId}`;
        txIdLink.textContent = walletManager.shortenTransactionSignature(transaction.txId);
        txIdCell.className = "tx-id-url";
        txIdCell.appendChild(txIdLink);
        row.appendChild(txIdCell);

        tbody.appendChild(row);
    }
}
document.getElementById("close-popup").addEventListener('click', function() {
    document.getElementById("overlay").style.display = 'none';
    document.getElementById("popup").style.display = 'none';
});
document.addEventListener('DOMContentLoaded', loadTransactions);