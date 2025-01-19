document.addEventListener("DOMContentLoaded", loadUsers);

async function loadUsers() {
    try {
        let response = await fetch("http://localhost:8080/api/users/list");
        let users = await response.json();

        let userList = document.getElementById("userList");
        userList.innerHTML = ""; // Vyčistí tabulku před načtením nových dat

        users.forEach(user => {
            let row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.username}</td>
                <td><input type="text" id="username-${user.username}" placeholder="Nové jméno"></td>
                <td><input type="password" id="password-${user.username}" placeholder="Nové heslo"></td>
                <td>
                    <button onclick="updateUserAdmin('${user.username}')">Upravit</button>
                    <button onclick="deleteUser('${user.username}')">Smazat</button>
                </td>
            `;

            userList.appendChild(row);
        });
    } catch (error) {
        console.error("Chyba při načítání uživatelů:", error);
    }
}

async function updateUserAdmin(username) {
    let newUsername = document.getElementById(`username-${username}`).value.trim();
    let newPassword = document.getElementById(`password-${username}`).value.trim();

    if (!newUsername && !newPassword) {
        alert("Zadejte alespoň jednu hodnotu pro úpravu.");
        return;
    }

    try {
        let response = await fetch("http://localhost:8080/api/users/admin/update", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                oldUsername: username,
                newUsername: newUsername || null,
                newPassword: newPassword || null
            })
        });

        let result = await response.json();

        if (result.success) {
            alert("Uživatel byl úspěšně upraven.");
            loadUsers(); // Aktualizace tabulky
        } else {
            alert("Chyba při úpravě uživatele: " + result.message);
        }
    } catch (error) {
        console.error("Chyba při úpravě uživatele:", error);
        alert("Chyba serveru při úpravě uživatele.");
    }
}


async function deleteUser(username) {
    try {
        let response = await fetch(`http://localhost:8080/api/users/delete/${username}`, {
            method: "DELETE"
        });

        if (response.ok) {
            alert("Uživatel smazán!");
            loadUsers(); // Obnoví seznam uživatelů
        } else {
            alert("Nepodařilo se smazat uživatele.");
        }
    } catch (error) {
        console.error("Chyba při mazání uživatele:", error);
    }
}

function logout() {
    localStorage.removeItem("authToken");
    window.location.href = "index.html";
}
