const TOKEN_KEY = "rentalFleetJwt";

document.addEventListener("DOMContentLoaded", () => {
    const page = location.pathname.split("/").pop() || "index.html";

    if (page === "index.html") {
        setupLoginPage();
    }

    if (page === "product.html") {
        setupProductPage();
    }

    if (page === "login-failure.html") {
        setupFailurePage();
    }
});

function setupLoginPage() {
    const form = document.getElementById("loginForm");
    const button = document.getElementById("loginButton");
    const status = document.getElementById("loginStatus");

    form.addEventListener("submit", async event => {
        event.preventDefault();

        const username = document.getElementById("usernameInput").value.trim();
        const password = document.getElementById("passwordInput").value;

        button.disabled = true;
        status.textContent = "Checking login...";

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                const message = await response.text();
                goToFailure(message || "Invalid username or password.");
                return;
            }

            const data = await response.json();
            localStorage.setItem(TOKEN_KEY, data.token);
            location.href = "product.html";
        } catch (error) {
            goToFailure("Login request failed. Please try again.");
        } finally {
            button.disabled = false;
        }
    });
}

async function setupProductPage() {
    const token = localStorage.getItem(TOKEN_KEY);
    const userInfo = document.getElementById("userInfo");
    const status = document.getElementById("productStatus");
    const productList = document.getElementById("productList");
    const logoutButton = document.getElementById("logoutButton");

    logoutButton.addEventListener("click", () => {
        clearSession();
        location.href = "index.html";
    });

    if (!token) {
        goToFailure("Please login before viewing products.");
        return;
    }

    userInfo.textContent = "Logged in with a JWT.";

    try {
        const response = await fetch("/api/products", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            const message = await response.text();
            clearSession();
            goToFailure(message || "Your login is no longer valid.");
            return;
        }

        const products = await response.json();
        renderProducts(products, productList, status);
    } catch (error) {
        status.textContent = "Could not load products.";
    }
}

function setupFailurePage() {
    const failureMessage = document.getElementById("failureMessage");
    const params = new URLSearchParams(location.search);
    const message = params.get("message");

    if (message) {
        failureMessage.textContent = message;
    }
}

function renderProducts(products, productList, status) {
    productList.innerHTML = "";

    if (products.length === 0) {
        status.textContent = "No products available.";
        return;
    }

    status.textContent = products.length + (products.length === 1 ? " product" : " products") + " found.";

    products.forEach(product => {
        const item = document.createElement("article");
        item.innerHTML = `
            <hr>
            <h3>${escapeHtml(product.name)}</h3>
            <p><strong>Category:</strong> ${escapeHtml(product.category)}</p>
            <p><strong>Owner:</strong> ${escapeHtml(product.owner)}</p>
            <p>${escapeHtml(product.description)}</p>
        `;
        productList.appendChild(item);
    });
}

function goToFailure(message) {
    location.href = "login-failure.html?message=" + encodeURIComponent(message);
}

function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
