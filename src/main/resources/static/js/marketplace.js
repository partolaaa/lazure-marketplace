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
    fetch('api/categories')
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

    return fetch(`api/products/get-products?${parameters}`)
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