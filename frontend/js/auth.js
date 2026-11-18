/**
 * FoodHub – Authentication & Session Management
 */

const FoodHubAuth = {
    SESSION_KEY: 'foodhub_user',

    getCurrentUser() {
        const stored = sessionStorage.getItem(this.SESSION_KEY) || localStorage.getItem(this.SESSION_KEY);
        try {
            return stored ? JSON.parse(stored) : null;
        } catch (e) {
            return null;
        }
    },

    setCurrentUser(user, remember = false) {
        const str = JSON.stringify(user);
        sessionStorage.setItem(this.SESSION_KEY, str);
        if (remember) {
            localStorage.setItem(this.SESSION_KEY, str);
        }
    },

    isLoggedIn() {
        return this.getCurrentUser() !== null;
    },

    isAdmin() {
        const user = this.getCurrentUser();
        return user && user.role === 'ADMIN';
    },

    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.pathname);
        }
    },

    requireAdmin() {
        if (!this.isAdmin()) {
            alert('Access Denied: Administrator privileges required.');
            window.location.href = 'login.html';
        }
    },

    logout() {
        sessionStorage.removeItem(this.SESSION_KEY);
        localStorage.removeItem(this.SESSION_KEY);
        window.location.href = 'login.html';
    },

    initNavbar() {
        const user = this.getCurrentUser();
        const navActions = document.getElementById('navActions');
        const adminLink = document.getElementById('adminNavLink');
        const ordersLink = document.getElementById('ordersNavLink');

        if (adminLink) {
            adminLink.style.display = this.isAdmin() ? 'inline-block' : 'none';
        }
        if (ordersLink) {
            ordersLink.style.display = this.isLoggedIn() ? 'inline-block' : 'none';
        }

        if (navActions) {
            if (user) {
                navActions.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <span style="font-size: 0.9rem; font-weight: 600; color: var(--dark);">
                            👋 ${user.name.split(' ')[0]} ${user.role === 'ADMIN' ? '<span class="status-badge status-PLACED" style="font-size:0.7rem; padding: 2px 6px;">ADMIN</span>' : ''}
                        </span>
                        <a href="cart.html" class="cart-btn" id="cartNavBtn">
                            🛒 Cart <span class="cart-count" id="cartBadge">0</span>
                        </a>
                        <button class="btn btn-secondary btn-sm" onclick="FoodHubAuth.logout()">Logout</button>
                    </div>
                `;
            } else {
                navActions.innerHTML = `
                    <a href="cart.html" class="cart-btn" id="cartNavBtn">
                        🛒 Cart <span class="cart-count" id="cartBadge">0</span>
                    </a>
                    <a href="login.html" class="btn btn-secondary btn-sm">Login</a>
                    <a href="register.html" class="btn btn-primary btn-sm">Register</a>
                `;
            }
        }
    }
};

window.FoodHubAuth = FoodHubAuth;

document.addEventListener('DOMContentLoaded', () => {
    FoodHubAuth.initNavbar();
});
