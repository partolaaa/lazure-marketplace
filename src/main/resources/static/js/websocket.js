let socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.debug = null;
stompClient.connect({}, function () {
    stompClient.subscribe('/topic/login', function () {
        walletManager.onWalletReady(() => loadListings(true));
    });
    stompClient.subscribe('/topic/logout', function () {
        walletManager.onWalletDisconnected(() => loadListings(true));
    });
});