document.getElementById("configure-search-button").addEventListener("click", function () {
    document.getElementById('search-config').classList.add('open-search-config');
});

document.getElementById("close-search-config").addEventListener("click", function () {
    document.getElementById('search-config').classList.remove('open-search-config');
});

document.addEventListener('DOMContentLoaded', function() {
    fetch('api/products/category')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('search-form-checkboxes');
            container.innerHTML = '';

            data.forEach(category => {
                const paragraphElement = document.createElement('p');
                paragraphElement.className = 'search-config-form-item';

                const checkbox = document.createElement('input');
                checkbox.type = "checkbox";
                checkbox.className = "search-config-property";
                checkbox.name = "selectedCategory";
                checkbox.value = category.category_id;
                document.getElementById('search-form-checkboxes').appendChild(checkbox);


                const divElement = document.createElement("div");
                divElement.className = "category-info-container"

                const spanElement = document.createElement('span');
                spanElement.textContent = category.name;

                divElement.appendChild(spanElement);
                divElement.appendChild(prepareCategoryToolTip(category));

                paragraphElement.appendChild(checkbox);
                paragraphElement.appendChild(divElement);


                container.appendChild(paragraphElement);
            });
        })
        .catch(error => {
            console.error('Failed to fetch categories:', error);
        });
});

document.getElementById('search-config-form').addEventListener('submit', function(event) {
    event.preventDefault();
    let form = event.target;

    let url = new URL(form.action);
    let params = new URLSearchParams(new FormData(form));

    url.search = params.toString();

    fetch(url, {
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => Promise.reject(text));
            }
            return response.text();
        })
        .then(text => {
            createToast("success", text);
        })
        .catch(error => {
            createToast("warning", error);
        });
});

document.addEventListener('DOMContentLoaded', function () {
    loadListings(40,true);

    const container = document.querySelector('.main-products-container');

    let isLoading = false;
    let lastScrollTop = 0;
    const threshold = 100;

    container.addEventListener('scroll', function() {
        const currentScrollTop = container.scrollTop;

        if (currentScrollTop > lastScrollTop && currentScrollTop + container.clientHeight >= container.scrollHeight - threshold && !isLoading) {
            isLoading = true;
            loadMoreProducts();
        }
        lastScrollTop = currentScrollTop;
    });

    function loadMoreProducts(limit= 20) {
        loadListings(limit, false).finally(() => {
            isLoading = false;
        });
    }
});

function loadListings(limit= 20, isClearContainer) {
    const loader = document.querySelector('.loader');
    const container = document.querySelector('.main-products-container');

    return fetch(`api/products/get-products?limit=${limit}`)
        .then(response => response.json())
        .then(data => {
            if (isClearContainer) {
                container.innerHTML = '';
            }
            const fragment = document.createDocumentFragment();
            data.forEach(product => {
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
                            walletManager.transferSol("HMg6tQYMpigM5656hK4XF5e6aAYDGyBmXqRJfDfYsNhq", product.price).then(r => buyLoader.style.display = "none");
                        };
                    } else {
                        button.classList.add("button-disabled");
                        document.getElementById("popup-buy-button-tooltiptext").style.display = "block";
                    }
                });
                fragment.appendChild(productDiv);
            });
            container.appendChild(fragment);
        })
        .catch(error => {
            console.error("Failed to load products:", error);
            createToast("error", "Failed to load products.");
        })
        .finally(() => {
            loader.style.display = 'none';
        });
}

document.getElementById("close-popup").addEventListener('click', function() {
    document.getElementById("overlay").style.display = 'none';
    document.getElementById("popup").style.display = 'none';
});