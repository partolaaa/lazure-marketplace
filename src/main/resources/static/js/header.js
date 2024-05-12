document.getElementById('logo').onclick = function () {
    location.href='/'
};

document.querySelectorAll('.side-menu-element').forEach(function(element) {
    element.addEventListener('click', function() {
        let link = this.querySelector('a');
        window.location.href = link.getAttribute('href');
    });
});

document.getElementById('menu').addEventListener('click', function() {
    document.getElementById('side-menu').classList.add('open-menu');
});

document.getElementById('close-menu').addEventListener('click', function() {
    document.getElementById('side-menu').classList.remove('open-menu');
});

document.getElementById('close-info').addEventListener('click', function() {
    document.getElementById('wallet-info').classList.remove('open-wallet-info');
});

document.addEventListener('click', function(event) {
    const sideMenu = document.getElementById('side-menu');
    const walletInfo = document.getElementById('wallet-info');

    if (!sideMenu.contains(event.target) && sideMenu.classList.contains('open-menu')) {
        sideMenu.classList.remove('open-menu');
    }

    if (!walletInfo.contains(event.target) && walletInfo.classList.contains('open-wallet-info')) {
        walletInfo.classList.remove('open-wallet-info');
    }
});

document.getElementById('menu').addEventListener('click', function(event) {
    document.getElementById('side-menu').classList.add('open-menu');
    event.stopPropagation();
});

document.addEventListener("DOMContentLoaded", function() {
    const background = document.getElementById('background');
    let colorArr = ["rgba(255, 97, 179, 0.5)", "rgba(255, 97, 133, 0.5)"];

    function randomRange(min, max) {
        return Math.random() * (max - min) + min;
    }

    for (let i = 0; i < 5; i++) {
        let circle = document.createElement('div');
        circle.classList.add('circle');
        circle.style.width = circle.style.height = `${randomRange(50, 150)}px`;
        circle.style.position = 'absolute';
        circle.style.left = `${randomRange(0, 100)}vw`;
        circle.style.top = `${randomRange(0, 100)}vh`;
        circle.style.backgroundColor = colorArr[Math.floor(Math.random() * colorArr.length)];
        background.appendChild(circle);

        function moveCircle() {
            const newX = randomRange(0, 100);
            const newY = randomRange(0, 100);
            circle.style.transition = `all ${randomRange(20, 40)}s ease`;
            circle.style.left = `${newX}vw`;
            circle.style.top = `${newY}vh`;
        }

        setInterval(moveCircle, randomRange(20000, 40000));
    }
});




