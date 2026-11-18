/**
 * FoodHub – Administrator Dashboard Logic
 */

let adminMenu = [];
let adminCategories = [];
let adminOrders = [];

async function initAdminDashboard() {
    FoodHubAuth.requireAdmin();

    try {
        await Promise.all([
            loadSummaryMetrics(),
            loadAdminMenu(),
            loadAdminOrders(),
            loadAdminCategories()
        ]);
    } catch (e) {
        console.error('Error initializing admin dashboard:', e);
    }
}

// 1. Load Metric Summary Cards
async function loadSummaryMetrics() {
    try {
        const summary = await FoodHubApi.getReportSummary();
        document.getElementById('metricOrders').innerText = summary.totalOrders || 0;
        document.getElementById('metricCustomers').innerText = summary.totalCustomers || 0;
        document.getElementById('metricRevenue').innerText = '₹' + parseFloat(summary.totalRevenue || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });
        document.getElementById('metricMenuItems').innerText = summary.availableFoodItems || 0;
    } catch (e) {
        console.error('Failed to load summary stats:', e);
    }
}

// 2. Tab Navigation
function showAdminTab(tabName) {
    document.querySelectorAll('.admin-tab-content').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.admin-nav a').forEach(el => el.classList.remove('active'));

    const targetTab = document.getElementById(`tab-${tabName}`);
    const targetLink = document.getElementById(`link-${tabName}`);

    if (targetTab) targetTab.style.display = 'block';
    if (targetLink) targetLink.classList.add('active');
}

// 3. Menu Management
async function loadAdminMenu() {
    const tbody = document.getElementById('adminMenuTableBody');
    if (!tbody) return;

    try {
        adminMenu = await FoodHubApi.getMenu(true);
        renderAdminMenuTable();
    } catch (e) {
        console.error('Failed to load menu items:', e);
    }
}

function renderAdminMenuTable() {
    const tbody = document.getElementById('adminMenuTableBody');
    if (!tbody) return;

    if (adminMenu.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No menu items found</td></tr>';
        return;
    }

    tbody.innerHTML = adminMenu.map(item => `
        <tr>
            <td><strong>#${item.id}</strong></td>
            <td>
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <img src="${(item.imageUrl || 'images/menu/default.svg').replace(/^\//, '')}" 
                         alt="" style="width: 40px; height: 40px; object-fit: cover; border-radius: 6px;">
                    <div>
                        <div style="font-weight: 700;">${item.name}</div>
                        <small style="color: var(--text-muted);">${item.description.slice(0, 35)}...</small>
                    </div>
                </div>
            </td>
            <td><span class="status-badge status-PLACED">${item.category ? item.category.name : 'Uncategorized'}</span></td>
            <td><strong>₹${parseFloat(item.price).toFixed(2)}</strong></td>
            <td>
                <span class="status-badge ${item.isAvailable ? 'status-COMPLETED' : 'status-CANCELLED'}">
                    ${item.isAvailable ? 'Available' : 'Out of Stock'}
                </span>
            </td>
            <td>
                <div style="display: flex; gap: 0.5rem;">
                    <button class="btn btn-secondary btn-sm" onclick="openEditItemModal(${item.id})">✏️ Edit</button>
                    <button class="btn btn-danger btn-sm" style="background:#dc2626; color:#fff;" onclick="deleteItemPrompt(${item.id})">🗑️</button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 4. Orders Management
async function loadAdminOrders() {
    const tbody = document.getElementById('adminOrdersTableBody');
    if (!tbody) return;

    try {
        adminOrders = await FoodHubApi.getAllOrders();
        renderAdminOrdersTable(adminOrders);
    } catch (e) {
        console.error('Failed to load orders:', e);
    }
}

function renderAdminOrdersTable(orders) {
    const tbody = document.getElementById('adminOrdersTableBody');
    if (!tbody) return;

    if (orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No orders recorded yet</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(ord => {
        const custName = ord.user ? (ord.user.name || ord.user.email) : 'Customer';
        return `
            <tr>
                <td><strong>${ord.orderNumber}</strong></td>
                <td>
                    <div style="font-weight: 600;">${custName}</div>
                    <small style="color: var(--text-muted);">${ord.deliveryAddress || ''}</small>
                </td>
                <td><small>${ord.orderDate}</small></td>
                <td><strong>₹${parseFloat(ord.totalAmount).toFixed(2)}</strong></td>
                <td>
                    <select class="form-control" style="padding: 4px 8px; font-size: 0.85rem; width: auto;" 
                            onchange="handleOrderStatusChange(${ord.id}, this.value)">
                        <option value="PLACED" ${ord.status === 'PLACED' ? 'selected' : ''}>PLACED</option>
                        <option value="PREPARING" ${ord.status === 'PREPARING' ? 'selected' : ''}>PREPARING</option>
                        <option value="COMPLETED" ${ord.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                        <option value="CANCELLED" ${ord.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="viewOrderDetailsModal(${ord.id})">View Items</button>
                </td>
            </tr>
        `;
    }).join('');
}

async function handleOrderStatusChange(orderId, newStatus) {
    try {
        await FoodHubApi.updateOrderStatus(orderId, newStatus);
        FoodHubCart.showToast(`Order status updated to ${newStatus}`);
        loadSummaryMetrics();
    } catch (e) {
        alert('Failed to update order status: ' + e.message);
    }
}

// 5. Category Management
async function loadAdminCategories() {
    try {
        adminCategories = await FoodHubApi.getCategories();
        const select = document.getElementById('itemCategoryId');
        if (select) {
            select.innerHTML = adminCategories.map(c => `
                <option value="${c.id}">${c.name}</option>
            `).join('');
        }
    } catch (e) {
        console.error('Failed to load categories:', e);
    }
}

// 6. Modal Functions (Add / Edit Item)
function openAddItemModal() {
    document.getElementById('modalTitle').innerText = 'Add New Menu Item';
    document.getElementById('itemId').value = '';
    document.getElementById('itemName').value = '';
    document.getElementById('itemDescription').value = '';
    document.getElementById('itemPrice').value = '';
    document.getElementById('itemImageUrl').value = '';
    document.getElementById('itemAvailable').checked = true;
    document.getElementById('itemModal').classList.add('active');
}

function openEditItemModal(id) {
    const item = adminMenu.find(i => i.id == id);
    if (!item) return;

    document.getElementById('modalTitle').innerText = 'Edit Menu Item';
    document.getElementById('itemId').value = item.id;
    document.getElementById('itemName').value = item.name;
    document.getElementById('itemDescription').value = item.description;
    document.getElementById('itemPrice').value = item.price;
    document.getElementById('itemCategoryId').value = item.category ? item.category.id : 1;
    document.getElementById('itemImageUrl').value = item.imageUrl || '';
    document.getElementById('itemAvailable').checked = item.isAvailable;
    document.getElementById('itemModal').classList.add('active');
}

function closeModal() {
    document.getElementById('itemModal').classList.remove('active');
}

async function handleSaveItem(e) {
    e.preventDefault();
    const id = document.getElementById('itemId').value;
    const name = document.getElementById('itemName').value;
    const description = document.getElementById('itemDescription').value;
    const price = parseFloat(document.getElementById('itemPrice').value);
    const categoryId = parseInt(document.getElementById('itemCategoryId').value);
    const imageUrl = document.getElementById('itemImageUrl').value;
    const isAvailable = document.getElementById('itemAvailable').checked;

    const data = { name, description, price, categoryId, imageUrl, isAvailable };

    try {
        if (id) {
            await FoodHubApi.updateMenuItem(id, data);
            FoodHubCart.showToast('Item updated successfully!');
        } else {
            await FoodHubApi.createMenuItem(data);
            FoodHubCart.showToast('Item created successfully!');
        }
        closeModal();
        await loadAdminMenu();
        await loadSummaryMetrics();
    } catch (err) {
        alert('Error saving menu item: ' + err.message);
    }
}

async function deleteItemPrompt(id) {
    if (confirm('Are you sure you want to delete this food item?')) {
        try {
            await FoodHubApi.deleteMenuItem(id);
            FoodHubCart.showToast('Item deleted successfully.');
            await loadAdminMenu();
            await loadSummaryMetrics();
        } catch (e) {
            alert('Error deleting item: ' + e.message);
        }
    }
}

function viewOrderDetailsModal(orderId) {
    const ord = adminOrders.find(o => o.id == orderId);
    if (!ord) return;
    let details = `Order Number: ${ord.orderNumber}\n`;
    details += `Customer: ${ord.user ? (ord.user.name || ord.user.email) : 'N/A'}\n`;
    details += `Address: ${ord.deliveryAddress}\n\nItems:\n`;
    (ord.items || []).forEach(it => {
        details += `- ${it.quantity}x ${it.menuItem ? it.menuItem.name : 'Dish'} (₹${it.subtotal})\n`;
    });
    details += `\nTotal: ₹${ord.totalAmount}`;
    alert(details);
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('adminDashboardContainer')) {
        initAdminDashboard();
    }
});
