const SOLANA_NET = 'devnet';
const LOCALSTORAGE_USER_REJECTED_ID = 'userRejectedWalletConnection';

let subscribers = [];

class WalletManager {
    constructor() {
        this.wallet = null;
        this.connection = null;
        this.init();
    }

    async init() {
        try {
            if (!this.getUserRejectedRequest()) {
                this.wallet = await this.connectWallet();
                this.connection = this.getSolanaConnection();
                await this.updateDisplayedUserInfo();
            }
        } catch (error) {
            console.error("Initialization failed:", error);
        }
    }

    onWalletReady(callback) {
        if (this.wallet) {
            callback();
        } else {
            subscribers.push(callback);
        }
    }

    notifySubscribers() {
        if (!this.wallet) {
            createToast("warning", "Wallet not ready yet.");
            return;
        }
        subscribers.forEach(callback => callback());
    }

    getSolanaConnection() {
        return new solanaWeb3.Connection(solanaWeb3.clusterApiUrl(SOLANA_NET), 'confirmed');
    }

    sendLoginRequest() {
        fetch('/api/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({walletId: this.getWalletString()})
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
            })
            .catch(error => {
                throw new Error("Error while getting login data.");
            });
    }

    async connectWallet() {
        if (window.solana && window.solana.isPhantom) {
            try {
                this.wallet = await window.solana.connect({onlyIfTrusted: false});
                this.setUserRejected(false);
                await this.updateDisplayedUserInfo();
                this.sendLoginRequest();

                this.notifySubscribers();

                return this.wallet;
            } catch (error) {
                this.handleWalletConnectionError(error);
                throw error;
            }
        } else {
            createToast("info", "You should install Phantom wallet first.");
            throw new Error("Phantom wallet not found");
        }
    }

    async updateDisplayedUserInfo() {
        let wallet = this.wallet.publicKey;
        let profileURL = document.getElementById("profile");
        let menuProfileURL = document.getElementById("menu-profile");
        const balance = await this.getAccountBalance(wallet);

        document.getElementById("balance").innerText = `${parseFloat(balance).toFixed(2)} SOL`;

        profileURL.innerText = this.shortenWalletAddress(wallet.toString());
        profileURL.href = "/profile/" + wallet.toString();
        menuProfileURL.href = "/profile/" + wallet.toString();
    }

    async getAccountBalance(publicKey) {
        if (!this.connection) {
            this.connection = this.getSolanaConnection();
        }
        try {
            const balance = await this.connection.getBalance(publicKey);
            return balance / solanaWeb3.LAMPORTS_PER_SOL;
        } catch (error) {
            createToast("error", `Error getting balance: ${error}`);
            throw new Error("Failed to get account balance");
        }
    }

    getWalletString() {
        return this.wallet.publicKey.toString();
    }

    async showWalletInfo() {
        if (!this.wallet) {
            await this.connectWallet();
        }
        let walletShortInfo = document.getElementById("wallet-short-info");
        document.getElementById("wallet-info").classList.add("open-wallet-info");
        walletShortInfo.classList.add("loader-spinner");

        try {
            walletShortInfo.classList.remove("loader-spinner");
            const balance = await this.getAccountBalance(this.wallet.publicKey);
            const shortWalletAddress = this.shortenWalletAddress(this.getWalletString());
            walletShortInfo.innerText = `${shortWalletAddress} ${parseFloat(balance).toFixed(2)} SOL`;
        } catch (error) {
            walletShortInfo.innerText = "Failed to load data";
            console.error("Failed to load wallet info:", error);
        }
    }

    async transferSol(recipientWalletAddress, amount) {
        try {
            const {solana} = window;
            if (solana && solana.isPhantom) {
                const provider = solana;
                await provider.connect({onlyIfTrusted: false});
                const connection = walletManager.getSolanaConnection();
                const transaction = new solanaWeb3.Transaction().add(
                    solanaWeb3.SystemProgram.transfer({
                        fromPubkey: provider.publicKey,
                        toPubkey: recipientWalletAddress,
                        lamports: amount * 1000000000
                    })
                );

                let {blockhash} = await connection.getRecentBlockhash();
                transaction.recentBlockhash = blockhash;
                transaction.feePayer = provider.publicKey;

                let signed = await provider.signTransaction(transaction);
                let signature = await connection.sendRawTransaction(signed.serialize());
                await connection.confirmTransaction(signature);

                createToast("success", `You successfully sent ${amount} to ${this.shortenWalletAddress(recipientWalletAddress)}!`)
                let balance = await this.getAccountBalance(this.wallet.publicKey);
                document.getElementById("balance").innerText = `${parseFloat(balance).toFixed(2)} SOL`;

                return signature;
            } else {
                createToast("warning", 'Phantom wallet not found!');
            }
        } catch (error) {
            createToast("error", 'Error while sending your sol.');
        }
    }

    shortenWalletAddress(fullAddress) {
        return fullAddress.length > 8 ? `${fullAddress.slice(0, 5)}...${fullAddress.slice(-3)}` : fullAddress;
    }

    shortenTransactionSignature(signature) {
        return signature.length > 15 ? `${signature.slice(0, 7)}...${signature.slice(-8)}` : signature;
    }

    async disconnectWallet() {
        if (window.solana && window.solana.isPhantom) {
            try {
                await window.solana.disconnect();
                document.getElementById("balance").innerText = "Connect Wallet";
                document.getElementById('wallet-info').classList.remove("open-wallet-info");
                let profileURL = document.getElementById("profile");
                profileURL.innerText = "";
                profileURL.href = "";
                this.wallet = null;
                this.connection = null;
                this.setUserRejected(true);
                fetch("/api/users/logout", {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then(response => response.text())
                    .then(() => createToast("info", "Wallet was successfully disconnected!"))
                    .catch(error => createToast("error", `Error disconnecting wallet: ${error}`));
            } catch (error) {
                createToast("error", `Error disconnecting wallet: ${error}`);
            }
        }
    }

    setUserRejected(isRejected) {
        localStorage.setItem(LOCALSTORAGE_USER_REJECTED_ID, isRejected.toString());
    }

    getUserRejectedRequest() {
        return localStorage.getItem(LOCALSTORAGE_USER_REJECTED_ID) === "true";
    }

    handleWalletConnectionError(error) {
        createToast("warning", `Failed to connect the Phantom wallet: ${error}`);
        this.setUserRejected(true);
    }
}

const walletManager = new WalletManager();
document.getElementById('profile-balance').addEventListener('click', function (event) {
    walletManager.showWalletInfo();
    event.stopPropagation();
});

document.getElementById('disconnect-wallet').addEventListener('click', function () {
    walletManager.disconnectWallet();
});

async function handleTransferSol(product, buyLoader) {
    try {
        const walletID = await getProductOwnerWalletByProductId(product.product_id);
        let signature = await walletManager.transferSol(walletID, product.price);
        buyLoader.style.display = "none";

        await addTransaction(walletID, signature, product);
    } catch (error) {
        console.error(error);
    }
}

async function addTransaction(sellerWallet, signature, product) {
    let seller = await getUserByWalletId(sellerWallet);
    let buyer = await getUserByWalletId(walletManager.getWalletString());

    let transaction = {
        "sellerId": seller.userId,
        "buyerId": buyer.userId,
        "productId": product.product_id,
        "txId": signature
    }

    fetch("/api/transactions", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(transaction)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => Promise.reject(text));
            }
            return response.text();
        })
        .catch(error => {
            createToast("error", error);
        });
}

async function getUserByWalletId(walletId) {
    let user;
    try {
        const response = await fetch("/api/users/wallet/" + walletId, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error("Couldn't get user by wallet address: " + response.statusText);
        }

        user = await response.json();
    } catch (error) {
        createToast("warning", error);
    }

    return user;
}