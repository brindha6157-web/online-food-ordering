/**
 * FoodHub – Order Management & Live Tracking
 */

async function initOrdersPage() {
    FoodHubAuth.requireAuth();
    const user = FoodHubAuth.getCurrentUser();
    const container = document.getElementById('ordersContainer');
    if (!container) return;

    try {
        const orders = await FoodHubApi.getMyOrders(user.userId || user.id);
        renderOrdersList(orders);
    } catch (error) {
        console.error('Failed to load orders:', error);
        container.innerHTML = `
            <div class="alert alert-danger">
                Failed to load your orders. Please try again.
            </div>
        `;
    }
}

function renderOrdersList(orders) {
    const container = document.getElementById('ordersContainer');
    if (!container) return;

    if (!orders || orders.length === 0) {
        container.innerHTML = `
            <div style="text-align: center; padding: 4rem 1rem; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--border-color);">
                <div style="font-size: 3.5rem; margin-bottom: 1rem;">📦</div>
                <h2>No Orders Yet</h2>
                <p style="color: var(--text-secondary); margin: 0.5rem 0 1.5rem;">You haven't placed any food orders yet. Explore our delicious menu!</p>
                <a href="menu.html" class="btn btn-primary">Browse Menu</a>
            </div>
        `;
        return;
    }

    container.innerHTML = orders.map(order => {
        const itemsList = order.items && order.items.length > 0
            ? order.items.map(it => `
                <div style="display: flex; justify-content: space-between; font-size: 0.9rem; padding: 4px 0;">
                    <span>${it.quantity}x ${it.menuItem ? it.menuItem.name : 'Food Item'}</span>
                    <span style="font-weight: 600;">₹${parseFloat(it.subtotal || 0).toFixed(2)}</span>
                </div>
            `).join('')
            : '<div style="font-size: 0.9rem; color: var(--text-muted);">Items recorded in order</div>';

        return `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <div style="font-size: 1.15rem; font-weight: 800; color: var(--dark);">
                            ${order.orderNumber}
                        </div>
                        <div style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 2px;">
                            Placed on ${order.orderDate}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <span class="status-badge status-${order.status}">${order.status}</span>
                        <div style="font-size: 1.25rem; font-weight: 800; color: var(--primary); margin-top: 4px;">
                            ₹${parseFloat(order.totalAmount).toFixed(2)}
                        </div>
                    </div>
                </div>

                <!-- Visual Stepper Progress Bar -->
                ${renderOrderStepper(order.status)}

                <div style="background-color: var(--light-bg); border-radius: var(--radius-md); padding: 1rem; margin-top: 1rem;">
                    <div style="font-size: 0.85rem; font-weight: 700; color: var(--dark); margin-bottom: 0.5rem;">
                        ORDER ITEMS
                    </div>
                    ${itemsList}
                    <div style="margin-top: 0.75rem; padding-top: 0.5rem; border-top: 1px dashed var(--border-color); font-size: 0.85rem; color: var(--text-secondary);">
                        📍 <strong>Delivery Address:</strong> ${order.deliveryAddress || 'Registered Address'}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function renderOrderStepper(status) {
    if (status === 'CANCELLED') {
        return `
            <div class="alert alert-danger" style="margin: 1rem 0 0.5rem;">
                ⚠️ This order was cancelled.
            </div>
        `;
    }

    const steps = ['PLACED', 'PREPARING', 'COMPLETED'];
    const currentIndex = steps.indexOf(status);

    return `
        <div class="order-stepper">
            <div class="step-item ${currentIndex >= 0 ? (currentIndex > 0 ? 'completed' : 'active') : ''}">
                <div class="step-circle">${currentIndex > 0 ? '✓' : '1'}</div>
                <div class="step-label">Order Placed</div>
            </div>
            <div class="step-item ${currentIndex >= 1 ? (currentIndex > 1 ? 'completed' : 'active') : ''}">
                <div class="step-circle">${currentIndex > 1 ? '✓' : '2'}</div>
                <div class="step-label">Kitchen Preparing</div>
            </div>
            <div class="step-item ${currentIndex >= 2 ? 'completed' : ''}">
                <div class="step-circle">${currentIndex >= 2 ? '✓' : '3'}</div>
                <div class="step-label">Delivered / Ready</div>
            </div>
        </div>
    `;
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('ordersContainer')) {
        initOrdersPage();
    }
});
