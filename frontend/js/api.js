/**
 * FoodHub – API Communication Layer
 * Connects to Spring Boot REST endpoints with graceful synchronized fallback.
 */

const API_BASE_URL = window.location.origin.includes('8080')
    ? '/api'
    : 'http://localhost:8080/api';

// Synchronized local fallback memory (matching database.sql exactly)
const SEED_CATEGORIES = [
    { id: 1, name: "Pizza", description: "Authentic wood-fired and pan-crust pizzas", imageUrl: "images/categories/pizza.svg" },
    { id: 2, name: "Burger", description: "Juicy grilled gourmet burgers with fresh toppings", imageUrl: "images/categories/burger.svg" },
    { id: 3, name: "Biryani", description: "Traditional aromatic dum biryani with saffron", imageUrl: "images/categories/biryani.svg" },
    { id: 4, name: "Drinks", description: "Refreshing chilled beverages and mocktails", imageUrl: "images/categories/drinks.svg" },
    { id: 5, name: "Desserts", description: "Gourmet pastries, brownies, and ice creams", imageUrl: "images/categories/desserts.svg" }
];

const SEED_MENU = [
    { id: 1, name: "Margherita Supreme", description: "Fresh mozzarella, vine-ripened tomatoes, sweet basil", price: 299.00, category: SEED_CATEGORIES[0], imageUrl: "images/menu/margherita.svg", isAvailable: true },
    { id: 2, name: "Paneer Tikka Pizza", description: "Spiced tandoori paneer cubes, crunchy bell peppers, onions", price: 349.00, category: SEED_CATEGORIES[0], imageUrl: "images/menu/paneer-pizza.svg", isAvailable: true },
    { id: 3, name: "Chicken Feast Pizza", description: "Smoked BBQ chicken chunks, chicken sausage, jalapeños", price: 399.00, category: SEED_CATEGORIES[0], imageUrl: "images/menu/chicken-pizza.svg", isAvailable: true },
    { id: 4, name: "Veggie Paradise", description: "Golden corn, black olives, crisp bell peppers, mushrooms", price: 329.00, category: SEED_CATEGORIES[0], imageUrl: "images/menu/veggie-pizza.svg", isAvailable: true },
    { id: 5, name: "Classic Veg Crispy Burger", description: "Crunchy spiced potato patty with creamy mayo and lettuce", price: 149.00, category: SEED_CATEGORIES[1], imageUrl: "images/menu/veg-burger.svg", isAvailable: true },
    { id: 6, name: "Grilled Chicken Burger", description: "Tender flame-grilled chicken breast with chipotle sauce", price: 219.00, category: SEED_CATEGORIES[1], imageUrl: "images/menu/chicken-burger.svg", isAvailable: true },
    { id: 7, name: "Double Cheese Delight Burger", description: "Loaded double cheese slices melted over crispy vegetable patty", price: 189.00, category: SEED_CATEGORIES[1], imageUrl: "images/menu/cheese-burger.svg", isAvailable: true },
    { id: 8, name: "Spicy Paneer Crunch Burger", description: "Crispy cottage cheese patty in hot peri-peri glaze", price: 199.00, category: SEED_CATEGORIES[1], imageUrl: "images/menu/paneer-burger.svg", isAvailable: true },
    { id: 9, name: "Hyderabadi Chicken Dum Biryani", description: "Slow-cooked aromatic basmati rice layered with chicken", price: 280.00, category: SEED_CATEGORIES[2], imageUrl: "images/menu/chicken-biryani.svg", isAvailable: true },
    { id: 10, name: "Mutton Royal Dum Biryani", description: "Tender bone-in mutton pieces cooked in Awadhi dum style", price: 360.00, category: SEED_CATEGORIES[2], imageUrl: "images/menu/mutton-biryani.svg", isAvailable: true },
    { id: 11, name: "Special Veg Biryani", description: "Garden-fresh vegetables and paneer cubes with herbs", price: 220.00, category: SEED_CATEGORIES[2], imageUrl: "images/menu/veg-biryani.svg", isAvailable: true },
    { id: 12, name: "Egg Biryani Feast", description: "Fragrant biryani topped with roasted golden boiled eggs", price: 210.00, category: SEED_CATEGORIES[2], imageUrl: "images/menu/egg-biryani.svg", isAvailable: true },
    { id: 13, name: "Fresh Mint Mojito", description: "Zesty fresh lime muddled with mint leaves and soda", price: 99.00, category: SEED_CATEGORIES[3], imageUrl: "images/menu/mojito.svg", isAvailable: true },
    { id: 14, name: "Mango Lassi", description: "Traditional chilled yogurt shake blended with Alphonso mango", price: 89.00, category: SEED_CATEGORIES[3], imageUrl: "images/menu/mango-lassi.svg", isAvailable: true },
    { id: 15, name: "Iced Cold Coffee", description: "Rich espresso blended with chilled milk and vanilla ice cream", price: 119.00, category: SEED_CATEGORIES[3], imageUrl: "images/menu/cold-coffee.svg", isAvailable: true },
    { id: 16, name: "Sizzling Chocolate Brownie", description: "Warm walnut brownie with hot Belgian fudge and ice cream", price: 159.00, category: SEED_CATEGORIES[4], imageUrl: "images/menu/brownie.svg", isAvailable: true },
    { id: 17, name: "Gulab Jamun with Ice Cream", description: "Warm khoya dumplings soaked in rose syrup with vanilla gelato", price: 109.00, category: SEED_CATEGORIES[4], imageUrl: "images/menu/gulab-jamun.svg", isAvailable: true },
    { id: 18, name: "Royal Kulfi Falooda", description: "Creamy malai kulfi topped with basil seeds and rose vermicelli", price: 139.00, category: SEED_CATEGORIES[4], imageUrl: "images/menu/kulfi.svg", isAvailable: true }
];

// Seed storage initialization if needed
if (!localStorage.getItem('foodhub_menu')) {
    localStorage.setItem('foodhub_menu', JSON.stringify(SEED_MENU));
}
if (!localStorage.getItem('foodhub_categories')) {
    localStorage.setItem('foodhub_categories', JSON.stringify(SEED_CATEGORIES));
}
if (!localStorage.getItem('foodhub_orders')) {
    const defaultOrders = [
        { id: 1, orderNumber: "ORD-20261101-001", user: { id: 2, name: "Rahul Sharma", email: "rahul@gmail.com" }, totalAmount: 648.00, status: "COMPLETED", deliveryAddress: "No. 12, Anna Nagar West, Chennai", orderDate: "2026-11-01 12:45:00", items: [{ menuItem: SEED_MENU[0], quantity: 1, subtotal: 299 }, { menuItem: SEED_MENU[1], quantity: 1, subtotal: 349 }] },
        { id: 2, orderNumber: "ORD-20261102-002", user: { id: 3, name: "Priya Sundaram", email: "priya@gmail.com" }, totalAmount: 479.00, status: "COMPLETED", deliveryAddress: "Flat 4B, Green Acres, OMR, Chennai", orderDate: "2026-11-02 19:30:00", items: [{ menuItem: SEED_MENU[8], quantity: 1, subtotal: 280 }, { menuItem: SEED_MENU[7], quantity: 1, subtotal: 199 }] },
        { id: 3, orderNumber: "ORD-20261115-008", user: { id: 3, name: "Priya Sundaram", email: "priya@gmail.com" }, totalAmount: 739.00, status: "PREPARING", deliveryAddress: "Flat 4B, Green Acres, OMR, Chennai", orderDate: "2026-11-15 19:15:00", items: [{ menuItem: SEED_MENU[2], quantity: 1, subtotal: 399 }, { menuItem: SEED_MENU[8], quantity: 1, subtotal: 280 }, { menuItem: SEED_MENU[13], quantity: 1, subtotal: 60 }] },
        { id: 4, orderNumber: "ORD-20261118-010", user: { id: 2, name: "Rahul Sharma", email: "rahul@gmail.com" }, totalAmount: 248.00, status: "PLACED", deliveryAddress: "No. 12, Anna Nagar West, Chennai", orderDate: "2026-11-18 08:30:00", items: [{ menuItem: SEED_MENU[4], quantity: 1, subtotal: 149 }, { menuItem: SEED_MENU[12], quantity: 1, subtotal: 99 }] }
    ];
    localStorage.setItem('foodhub_orders', JSON.stringify(defaultOrders));
}

const FoodHubApi = {
    async request(endpoint, options = {}) {
        try {
            const res = await fetch(`${API_BASE_URL}${endpoint}`, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });
            if (!res.ok) {
                const err = await res.json().catch(() => ({ message: res.statusText }));
                throw new Error(err.message || 'Server request failed');
            }
            return await res.json();
        } catch (error) {
            console.warn(`[API] Live server unavailable at ${endpoint}. Using fallback mode.`, error.message);
            throw error;
        }
    },

    // Authentication
    async login(email, password) {
        try {
            return await this.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
        } catch (e) {
            // Fallback auth
            const cleanEmail = email.toLowerCase().trim();
            if (cleanEmail === 'admin@foodhub.com' && (password === 'Admin@123' || password === 'admin')) {
                return {
                    success: true,
                    message: "Login successful",
                    userId: 1,
                    name: "System Administrator",
                    email: "admin@foodhub.com",
                    role: "ADMIN"
                };
            }
            // Check stored users
            const users = JSON.parse(localStorage.getItem('foodhub_users') || '[]');
            const user = users.find(u => u.email === cleanEmail && u.password === password);
            if (user) {
                return { success: true, message: "Login successful", ...user };
            }
            if (cleanEmail.includes('@') && password.length >= 6) {
                return {
                    success: true,
                    message: "Login successful (Demo)",
                    userId: 2,
                    name: cleanEmail.split('@')[0],
                    email: cleanEmail,
                    role: "CUSTOMER",
                    phone: "9840123456",
                    address: "Chennai, India"
                };
            }
            throw new Error("Invalid email or password.");
        }
    },

    async register(name, email, password, phone, address) {
        try {
            return await this.request('/auth/register', {
                method: 'POST',
                body: JSON.stringify({ name, email, password, phone, address })
            });
        } catch (e) {
            const users = JSON.parse(localStorage.getItem('foodhub_users') || '[]');
            const cleanEmail = email.toLowerCase().trim();
            if (users.find(u => u.email === cleanEmail)) {
                throw new Error("An account with this email already exists.");
            }
            const newUser = {
                userId: Date.now(),
                name,
                email: cleanEmail,
                password,
                phone,
                address,
                role: "CUSTOMER"
            };
            users.push(newUser);
            localStorage.setItem('foodhub_users', JSON.stringify(users));
            return { success: true, message: "Registration successful!", ...newUser };
        }
    },

    // Menu & Categories
    async getCategories() {
        try {
            return await this.request('/categories');
        } catch (e) {
            return JSON.parse(localStorage.getItem('foodhub_categories')) || SEED_CATEGORIES;
        }
    },

    async getMenu(all = false) {
        try {
            return await this.request(`/menu${all ? '?all=true' : ''}`);
        } catch (e) {
            const items = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;
            return all ? items : items.filter(i => i.isAvailable);
        }
    },

    async createMenuItem(data) {
        try {
            return await this.request('/menu', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        } catch (e) {
            const items = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;
            const newItem = {
                id: Date.now(),
                name: data.name,
                description: data.description,
                price: parseFloat(data.price),
                category: SEED_CATEGORIES.find(c => c.id == data.categoryId) || SEED_CATEGORIES[0],
                imageUrl: data.imageUrl || 'images/menu/default.svg',
                isAvailable: data.isAvailable !== undefined ? data.isAvailable : true
            };
            items.push(newItem);
            localStorage.setItem('foodhub_menu', JSON.stringify(items));
            return newItem;
        }
    },

    async updateMenuItem(id, data) {
        try {
            return await this.request(`/menu/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } catch (e) {
            const items = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;
            const idx = items.findIndex(i => i.id == id);
            if (idx !== -1) {
                items[idx] = { ...items[idx], ...data };
                if (data.categoryId) {
                    items[idx].category = SEED_CATEGORIES.find(c => c.id == data.categoryId) || items[idx].category;
                }
                localStorage.setItem('foodhub_menu', JSON.stringify(items));
                return items[idx];
            }
            throw new Error("Item not found");
        }
    },

    async deleteMenuItem(id) {
        try {
            return await this.request(`/menu/${id}`, { method: 'DELETE' });
        } catch (e) {
            let items = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;
            items = items.filter(i => i.id != id);
            localStorage.setItem('foodhub_menu', JSON.stringify(items));
            return { success: true };
        }
    },

    // Orders
    async placeOrder(orderData) {
        try {
            return await this.request('/orders', {
                method: 'POST',
                body: JSON.stringify(orderData)
            });
        } catch (e) {
            const orders = JSON.parse(localStorage.getItem('foodhub_orders')) || [];
            const menu = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;

            let total = 0;
            const items = orderData.items.map(it => {
                const menuItem = menu.find(m => m.id == it.menuItemId) || menu[0];
                const subtotal = menuItem.price * it.quantity;
                total += subtotal;
                return { menuItem, quantity: it.quantity, subtotal };
            });

            const newOrder = {
                id: Date.now(),
                orderNumber: `ORD-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${Math.floor(1000 + Math.random() * 9000)}`,
                user: { id: orderData.userId },
                totalAmount: total,
                status: 'PLACED',
                deliveryAddress: orderData.deliveryAddress,
                orderDate: new Date().toISOString().replace('T', ' ').slice(0, 19),
                paymentMethod: orderData.paymentMethod,
                items
            };

            orders.unshift(newOrder);
            localStorage.setItem('foodhub_orders', JSON.stringify(orders));
            return newOrder;
        }
    },

    async getMyOrders(userId) {
        try {
            return await this.request(`/orders/my-orders?userId=${userId}`);
        } catch (e) {
            const orders = JSON.parse(localStorage.getItem('foodhub_orders')) || [];
            return orders.filter(o => o.user && o.user.id == userId);
        }
    },

    async getAllOrders() {
        try {
            return await this.request('/admin/orders');
        } catch (e) {
            return JSON.parse(localStorage.getItem('foodhub_orders')) || [];
        }
    },

    async updateOrderStatus(orderId, status) {
        try {
            return await this.request(`/admin/orders/${orderId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ status })
            });
        } catch (e) {
            const orders = JSON.parse(localStorage.getItem('foodhub_orders')) || [];
            const o = orders.find(ord => ord.id == orderId);
            if (o) {
                o.status = status;
                localStorage.setItem('foodhub_orders', JSON.stringify(orders));
                return o;
            }
            throw new Error("Order not found");
        }
    },

    // Reports
    async getReportSummary() {
        try {
            return await this.request('/admin/reports/summary');
        } catch (e) {
            const orders = JSON.parse(localStorage.getItem('foodhub_orders')) || [];
            const menu = JSON.parse(localStorage.getItem('foodhub_menu')) || SEED_MENU;
            const valid = orders.filter(o => o.status !== 'CANCELLED');
            const totalRevenue = valid.reduce((acc, o) => acc + (parseFloat(o.totalAmount) || 0), 0);
            return {
                totalOrders: orders.length,
                totalCustomers: 3,
                totalRevenue: totalRevenue,
                availableFoodItems: menu.filter(m => m.isAvailable).length
            };
        }
    }
};

window.FoodHubApi = FoodHubApi;
