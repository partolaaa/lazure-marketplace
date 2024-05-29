document.addEventListener("DOMContentLoaded", async function() {
    try {
        let solanaPriceSpan = document.getElementById("solana-price");
        const price = await getCurrentSolanaPrice();
        solanaPriceSpan.classList.remove("loader-spinner");
        document.getElementById("solana-price").innerText = `$${price.toFixed(2)}`;
    } catch (error) {
        console.error(error);
        createToast("warning", error.message);
    }
});