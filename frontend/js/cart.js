/**
 * FoodHub – Cart Management Module
 */

const FoodHubCart = {
    CART_KEY: 'foodhub_cart',

    getCart() {
        const stored = localStorage.getItem(this.CART_KEY);
        try {
            return stored ? JSON.parse(stored) : [];
        } catch (e) {
            return [];
        }
    },

    saveCart(cart) {
        localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
        this.updateBadge();
    },

    addItem(menuItem, quantity = 1) {
        const cart = this.getCart();
        const existing = cart.find(item => item.id === menuItem.id);
        if (existing) {
            existing.quantity += quantity;
        } else {
            cart.push({
                id: menuItem.id,
                name: menuItem.name,
                price: parseFloat(menuItem.price),
                imageUrl: menuItem.imageUrl,
                categoryName: menuItem.category ? menuItem.category.name : '',
                quantity: quantity
            });
        }
        this.saveCart(cart);
        this.showToast(`Added "${menuItem.name}" to cart! 🛒`);
    },

    updateQuantity(itemId, delta) {
        let cart = this.getCart();
        const item = cart.find(i => i.id === itemId);
        if (item) {
            item.quantity += delta;
            if (item.quantity <= 0) {
                cart = cart.filter(i => i.id !== itemId);
            }
            this.saveCart(cart);
        }
        return cart;
    },

    removeItem(itemId) {
        let cart = this.getCart();
        cart = cart.filter(i => i.id !== itemId);
        this.saveCart(cart);
        return cart;
    },

    clearCart() {
        localStorage.removeItem(this.CART_KEY);
        this.updateBadge();
    },

    getCount() {
        return this.getCart().reduce((sum, item) => sum + item.quantity, 0);
    },

    getSubtotal() {
        return this.getCart().reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },

    getCalculations() {
        const subtotal = this.getSubtotal();
        const deliveryFee = subtotal > 500 || subtotal === 0 ? 0 : 40;
        const gstTax = Math.round(subtotal * 0.05); // 5% GST
        const total = subtotal + deliveryFee + gstTax;
        return { subtotal, deliveryFee, gstTax, total };
    },

    updateBadge() {
        const badge = document.getElementById('cartBadge');
        if (badge) {
            badge.innerText = this.getCount();
        }
    },

    showToast(message) {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.style.cssText = `
                position: fixed;
                bottom: 24px;
                right: 24px;
                z-index: 9999;
                display: flex;
                flex-direction: column;
                gap: 8px;
            `;
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = 'alert alert-success';
        toast.style.cssText = `
            box-shadow: var(--shadow-lg);
            animation: slideIn 0.3s ease;
            margin-bottom: 0;
        `;
        toast.innerText = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.4s ease';
            setTimeout(() => toast.remove(), 400);
        }, 2500);
    }
};

window.FoodHubCart = FoodHubCart;

document.addEventListener('DOMContentLoaded', () => {
    FoodHubCart.updateBadge();
});
