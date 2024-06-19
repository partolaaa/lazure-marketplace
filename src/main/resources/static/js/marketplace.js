document.getElementById("configure-search-button").addEventListener("click", function () {
    document.getElementById('search-config').classList.add('open-search-config');
});

document.getElementById("close-search-config").addEventListener("click", function () {
    document.getElementById('search-config').classList.remove('open-search-config');
});

document.getElementById('search-input').addEventListener('input', function(event) {
    if (!(event.target.value === "")) {
        loadListings(true, event.target.value);
    } else {
        loadListings(true);
    }
});

document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/categories')
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
                checkbox.value = category.categoryId;
                checkbox.onclick = function () {
                    loadListings(true, document.getElementById('search-input').value);
                }
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



    fetch("/api/products/max-cost")
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(maxCost => {
            let maxCostPrice = parseInt(maxCost);
            const maxInput = document.getElementById('maxInput');
            maxInput.value = maxCostPrice;

            const slider = document.getElementById('slider');

            noUiSlider.create(slider, {
                start: [0, maxCostPrice],
                connect: true,
                range: {
                    'min': 0,
                    'max': maxCostPrice
                },
                tooltips: [true, true],
                format: {
                    to: function (value) {
                        return parseInt(value);
                    },
                    from: function (value) {
                        return parseInt(value);
                    }
                }
            });

            slider.noUiSlider.on('update', function (values, handle) {
                if (handle === 0) {
                    minInput.value = values[0];
                } else {
                    maxInput.value = values[1];
                }
            });
            minInput.addEventListener('change', function () {
                slider.noUiSlider.set([this.value, null]);
            });

            maxInput.addEventListener('change', function () {
                slider.noUiSlider.set([null, this.value]);
            });
        })
        .catch(error => {
            console.error('Failed to fetch categories:', error);
        });
});

document.addEventListener('DOMContentLoaded', function () {
    loadListings(true);

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

    function loadMoreProducts() {
        loadListings(false).finally(() => {
            isLoading = false;
        });
    }

    document.getElementById("save-price-button").onclick = function () {
        loadListings(true);
    }
});

function loadListings(isClearContainer,
                      title) {

    let limit = 40;
    const loader = document.querySelector('.loader');
    const container = document.querySelector('.main-products-container');

    let parameters = `limit=${limit}`;

    if (title) {
        parameters += `&title=${title}`;
    }

    let categoryCheckboxes= document.querySelectorAll('#search-form-checkboxes .search-config-property');
    let categoryIds = Array.from(categoryCheckboxes)
        .filter(checkbox => checkbox.checked)
        .map(checkbox => checkbox.value);

    if (categoryIds) {
        for (let i = 0; i < categoryIds.length; i++) {
            parameters += `&categoryId=${categoryIds[i]}`;
        }
    }

    let allDisplayedProducts = container.querySelectorAll('.product').length;
    if (!isClearContainer && allDisplayedProducts > 0) {
        parameters += `&offset=${container.querySelectorAll('.product').length}`;
    }

    let maxPrice = document.getElementById("maxInput").value;
    let minPrice = document.getElementById("minInput").value;
    if (maxPrice && minPrice) {
        parameters += `&maxPrice=${maxPrice}&minPrice=${minPrice}`
    }

    return fetch(`/api/products?${parameters}`)
        .then(response => response.json())
        .then(data => {
            if (isClearContainer) {
                container.innerHTML = '';
            }
            const fragment = document.createDocumentFragment();
            data.forEach(product => {
                fragment.appendChild(createProductInfoPopup(product));
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