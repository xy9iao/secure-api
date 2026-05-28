const TOKEN_KEY = "rentalFleetJwt";
const USERNAME_KEY = "rentalFleetUsername";
const ROLE_KEY = "rentalFleetRole";

const loginView = document.getElementById("loginView");
const productView = document.getElementById("productView");
const unauthorizedView = document.getElementById("unauthorizedView");
const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const loginStatus = document.getElementById("loginStatus");
const usernameInput = document.getElementById("usernameInput");
const passwordInput = document.getElementById("passwordInput");
const tokenOutput = document.getElementById("tokenOutput");
const productStatus = document.getElementById("productStatus");
const productGrid = document.getElementById("productGrid");
const productCount = document.getElementById("productCount");
const usernameBadge = document.getElementById("usernameBadge");
const roleBadge = document.getElementById("roleBadge");
const topUserArea = document.getElementById("topUserArea");
const topLogoutButton = document.getElementById("topLogoutButton");
const backToLoginButton = document.getElementById("backToLoginButton");
const unauthorizedMessage = document.getElementById("unauthorizedMessage");

loginForm.addEventListener("submit", login);
topLogoutButton.addEventListener("click", logout);
backToLoginButton.addEventListener("click", showLogin);
window.addEventListener("hashchange", route);

route();

async function login(event) {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    if (!username || !password) {
        setStatus(loginStatus, "Enter both username and password.", "error");
        return;
    }

    loginButton.disabled = true;
    setStatus(loginStatus, "Checking profile and requesting JWT...", "");

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const errorText = await response.text();
            setStatus(loginStatus, "Login failed. " + errorText, "error");
            return;
        }

        const data = await response.json();
        saveSession(data.token, data.username, data.role);
        setStatus(loginStatus, data.message, "success");

        if (window.location.hash === "#products") {
            await showProducts();
        } else {
            window.location.hash = "products";
        }
    } catch (error) {
        setStatus(loginStatus, "Login error: " + error.message, "error");
    } finally {
        loginButton.disabled = false;
    }
}

async function route() {
    if (window.location.hash === "#products") {
        if (!getToken()) {
            showUnauthorized("A valid JWT is required before the product page can be opened.");
            return;
        }

        await showProducts();
        return;
    }

    showLogin();
}

function showLogin() {
    loginView.classList.remove("hidden");
    productView.classList.add("hidden");
    unauthorizedView.classList.add("hidden");
    topUserArea.classList.add("hidden");
    window.location.hash = "";
}

async function showProducts() {
    loginView.classList.add("hidden");
    productView.classList.remove("hidden");
    unauthorizedView.classList.add("hidden");
    topUserArea.classList.remove("hidden");

    const session = getSession();
    usernameBadge.textContent = "User: " + session.username;
    roleBadge.textContent = "Role: " + session.role;
    roleBadge.classList.toggle("admin", session.role === "ADMIN");
    tokenOutput.textContent = session.token;

    await loadProducts();
}

async function loadProducts() {
    const token = getToken();

    if (!token) {
        clearSession();
        showUnauthorized("Your JWT is missing. Please log in again.");
        return;
    }

    setStatus(productStatus, "Loading products with JWT...", "");
    productGrid.innerHTML = "";
    productCount.textContent = "0 items";

    try {
        const response = await fetch("/api/products", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            const errorText = await response.text();
            clearSession();
            showUnauthorized(errorText || "Access denied. Please log in again.");
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            setStatus(productStatus, "Product request failed. " + errorText, "error");
            return;
        }

        const products = await response.json();
        renderProducts(products);
    } catch (error) {
        setStatus(productStatus, "Product request error: " + error.message, "error");
    }
}

function renderProducts(products) {
    productGrid.innerHTML = "";
    productCount.textContent = products.length + (products.length === 1 ? " item" : " items");

    if (products.length === 0) {
        setStatus(productStatus, "No products are available for this profile.", "");
        productGrid.innerHTML = '<div class="empty-state">No rental products matched this JWT identity.</div>';
        return;
    }

    setStatus(productStatus, "Products loaded successfully.", "success");

    products.forEach(product => {
        const card = document.createElement("article");
        card.className = "product-card";

        const title = document.createElement("h3");
        title.textContent = product.name;

        const meta = document.createElement("div");
        meta.className = "product-meta";
        meta.append(createBadge(product.category));
        meta.append(createBadge("Owner: " + product.owner));

        const description = document.createElement("p");
        description.textContent = product.description;

        card.append(title, meta, description);
        productGrid.append(card);
    });
}

function createBadge(text) {
    const badge = document.createElement("span");
    badge.className = "badge";
    badge.textContent = text;
    return badge;
}

function showUnauthorized(message) {
    loginView.classList.add("hidden");
    productView.classList.add("hidden");
    unauthorizedView.classList.remove("hidden");
    topUserArea.classList.add("hidden");
    unauthorizedMessage.textContent = message;
}

function logout() {
    clearSession();
    setStatus(loginStatus, "Logged out. The JWT was removed from this browser.", "success");
    showLogin();
}

function saveSession(token, username, role) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USERNAME_KEY, username);
    localStorage.setItem(ROLE_KEY, role);
}

function getSession() {
    const token = getToken();
    const claims = decodeJwtPayload(token);

    return {
        token,
        username: localStorage.getItem(USERNAME_KEY) || claims.sub || "unknown",
        role: localStorage.getItem(ROLE_KEY) || claims.role || "unknown"
    };
}

function getToken() {
    return localStorage.getItem(TOKEN_KEY) || "";
}

function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    localStorage.removeItem(ROLE_KEY);
    tokenOutput.textContent = "No JWT token.";
    productGrid.innerHTML = "";
    productCount.textContent = "0 items";
}

function decodeJwtPayload(token) {
    if (!token) {
        return {};
    }

    try {
        const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        const json = decodeURIComponent(atob(base64).split("").map(char => {
            return "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2);
        }).join(""));

        return JSON.parse(json);
    } catch (error) {
        return {};
    }
}

function setStatus(element, message, type) {
    element.className = "status";

    if (type) {
        element.classList.add(type);
    }

    element.textContent = message;
}
