document.getElementById("add-product").addEventListener('click', function() {
    document.getElementById("overlay").style.display = 'block';
    document.getElementById("popup").style.display = 'block';

    document.getElementById("add-product-form").reset();

    if (walletManager.wallet) {
        document.getElementById("submit-product-button").disabled = false;
        document.getElementById("submit-product-button").classList.remove("button-disabled");
        document.getElementById("submit-product-tooltip").style.display = "none";
    } else {
        document.getElementById("submit-product-button").disabled = true;
        document.getElementById("submit-product-button").classList.add("button-disabled");
        document.getElementById("submit-product-tooltip").style.display = "block";
    }
});

document.getElementById('add-product-form').addEventListener('submit', function(event) {
    event.preventDefault();

    let categoryId = document.querySelector('input[name="categoryId"]');
    if (!categoryId.value) {
        createToast("warning", "Please, select product type.");
        return;
    }

    let form = event.target;
    let data = new FormData(form);

    fetch(form.action, {
        method: 'POST',
        body: data
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => Promise.reject(text));
            }
            return response.text();
        })
        .then(() => {
            createToast("success", "Product added successfully!");
            getAllListingsByWallet();
        })
        .catch(error => {
            createToast("warning", error);
        });
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
            const container = document.querySelector('.custom-options');
            container.innerHTML = '';

            data.forEach(category => {
                const option = document.createElement('span');
                option.className = 'custom-option';
                option.setAttribute('data-value', category.categoryId);
                option.textContent = category.name;

                option.appendChild(prepareCategoryToolTip(category));

                option.addEventListener('click', function() {
                    let selectedValue = this.getAttribute('data-value');
                    let hiddenInput = document.querySelector('input[name="categoryId"]');
                    hiddenInput.value = selectedValue;
                    document.querySelectorAll('.custom-option').forEach(opt => opt.classList.remove('selected'));
                    this.classList.add('selected');
                    document.querySelector('.custom-select__trigger span').textContent = this.firstChild.textContent.trim();
                });


                container.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Failed to fetch categories:', error);
        });
});



function toggleDropdown() {
    const dropdown = document.querySelector('.custom-select');
    dropdown.classList.toggle('open');
}

document.querySelectorAll('.custom-option').forEach(option => {
    option.addEventListener('click', function(event) {
        event.stopPropagation();

        let selectedValue = this.getAttribute('data-value');
        let selectedText = this.textContent;
        let selectTrigger = document.querySelector('.custom-select__trigger span');
        let hiddenInput = document.querySelector('input[name="categoryId"]');

        selectTrigger.textContent = selectedText;
        hiddenInput.value = selectedValue;
        console.log(selectedValue);
        document.querySelector('.custom-select').classList.remove('open');
    });
});


window.onclick = function(e) {
    if (!e.target.matches('.custom-select, .custom-select *')) {
        document.querySelector('.custom-select').classList.remove('open');
    }
}

document.getElementById("overlay").addEventListener('click', function() {
    document.getElementById("overlay").style.display = 'none';
    document.getElementById("popup").style.display = 'none';
});

document.getElementById("close-popup").addEventListener('click', function() {
    document.getElementById("overlay").style.display = 'none';
    document.getElementById("popup").style.display = 'none';
});

document.addEventListener('DOMContentLoaded', loadListings);

function loadListings() {
    walletManager.onWalletReady(() => {
        getAllListingsByWallet();
    });
}

function getAllListingsByWallet() {
    const loader = document.querySelector('.loader');
    const container = document.querySelector('.main-products-container');
    fetch('/api/products/wallet/' + walletManager.getWalletString())
        .then(response => response.json())
        .then(async data => {
            loader.style.display = 'none';
            container.innerHTML = '';
            if (data.length === 0) {
                const img = document.createElement('img');
                img.src = "/img/broken-image.png";
                img.alt = "no image lol";
                const productDiv = document.createElement('div');
                productDiv.className = 'product';
                const infoDiv = document.createElement('div');
                infoDiv.className = 'short-product-info';
                const nameP = document.createElement('p');
                nameP.textContent = "No listings found";
                infoDiv.appendChild(nameP);
                productDiv.appendChild(img);
                productDiv.appendChild(infoDiv);

                container.appendChild(productDiv);
            } else {
                for (const product of data) {
                    container.appendChild(await createProductElement(product));
                }
            }
        })
        .catch(() => {
            loader.style.display = 'none';
            createToast("error", "Failed to load products.");
        });
}
