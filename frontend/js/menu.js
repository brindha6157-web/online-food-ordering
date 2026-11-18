/**
 * FoodHub – Menu Page & Customer Dashboard Logic
 */

let allMenuItems = [];
let allCategories = [];
let selectedCategoryId = 'ALL';
let searchKeyword = '';
let currentSort = 'DEFAULT';

async function initMenuPage() {
    try {
        const [categories, menu] = await Promise.all([
            FoodHubApi.getCategories(),
            FoodHubApi.getMenu()
        ]);
        allCategories = categories;
        allMenuItems = menu;

        renderCategories();
        applyFilters();
    } catch (error) {
        console.error('Failed to load menu data:', error);
    }
}

function renderCategories() {
    const container = document.getElementById('categoryFilters');
    if (!container) return;

    let html = `
        <button class="btn btn-sm ${selectedCategoryId === 'ALL' ? 'btn-primary' : 'btn-secondary'}" 
                onclick="filterByCategory('ALL')">
            🍽️ All Items
        </button>
    `;

    allCategories.forEach(cat => {
        const isSelected = selectedCategoryId == cat.id;
        const icon = getCategoryIcon(cat.name);
        html += `
            <button class="btn btn-sm ${isSelected ? 'btn-primary' : 'btn-secondary'}" 
                    onclick="filterByCategory(${cat.id})">
                ${icon} ${cat.name}
            </button>
        `;
    });

    container.innerHTML = html;
}

function getCategoryIcon(catName) {
    switch (catName.toLowerCase()) {
        case 'pizza': return '🍕';
        case 'burger': return '🍔';
        case 'biryani': return '🍚';
        case 'drinks': return '🍹';
        case 'desserts': return '🍰';
        default: return '🍴';
    }
}

function filterByCategory(catId) {
    selectedCategoryId = catId;
    renderCategories();
    applyFilters();
}

function handleSearch(keyword) {
    searchKeyword = keyword.trim().toLowerCase();
    applyFilters();
}

function handleSort(sortType) {
    currentSort = sortType;
    applyFilters();
}

function applyFilters() {
    let filtered = [...allMenuItems];

    // Category filter
    if (selectedCategoryId !== 'ALL') {
        filtered = filtered.filter(item => item.category && item.category.id == selectedCategoryId);
    }

    // Search filter
    if (searchKeyword) {
        filtered = filtered.filter(item =>
            item.name.toLowerCase().includes(searchKeyword) ||
            item.description.toLowerCase().includes(searchKeyword)
        );
    }

    // Sort filter
    if (currentSort === 'PRICE_LOW_HIGH') {
        filtered.sort((a, b) => a.price - b.price);
    } else if (currentSort === 'PRICE_HIGH_LOW') {
        filtered.sort((a, b) => b.price - a.price);
    }

    renderFoodGrid(filtered);
}

function renderFoodGrid(items) {
    const grid = document.getElementById('foodGrid');
    const countDisplay = document.getElementById('itemCount');
    if (!grid) return;

    if (countDisplay) {
        countDisplay.innerText = `Showing ${items.length} food items`;
    }

    if (items.length === 0) {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 4rem 1rem; color: var(--text-secondary);">
                <div style="font-size: 3rem; margin-bottom: 1rem;">🔍</div>
                <h3>No food items found matching your criteria</h3>
                <p>Try searching for a different dish or category.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = items.map(item => {
        const catName = item.category ? item.category.name : 'FoodHub Special';
        // Handle image path correctly
        let imgSrc = item.imageUrl || 'images/menu/default.svg';
        if (imgSrc.startsWith('/')) imgSrc = imgSrc.substring(1);

        return `
            <div class="food-card">
                <div class="food-image-wrapper">
                    <img src="${imgSrc}" alt="${item.name}" onerror="this.src='images/menu/default.svg'">
                    <span class="food-badge">${catName}</span>
                </div>
                <div class="food-body">
                    <div class="food-category">${catName}</div>
                    <h3 class="food-name">${item.name}</h3>
                    <p class="food-desc">${item.description}</p>
                    <div class="food-footer">
                        <span class="food-price">₹${parseFloat(item.price).toFixed(2)}</span>
                        <button class="btn btn-primary btn-sm" onclick='addToCartById(${item.id})'>
                            + Add to Cart
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function addToCartById(itemId) {
    const item = allMenuItems.find(i => i.id == itemId);
    if (item) {
        FoodHubCart.addItem(item, 1);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('foodGrid')) {
        initMenuPage();
    }
});
