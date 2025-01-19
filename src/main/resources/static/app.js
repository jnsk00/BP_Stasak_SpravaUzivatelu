// Registrace uživatele
async function registerUser() {
    // Načti hodnoty z formuláře
    let username = document.getElementById("username").value.trim();
    let password = document.getElementById("password").value.trim();

    // Kontrola, jestli jsou pole vyplněna
    if (!username || !password) {
        alert("Zadejte uživatelské jméno a heslo.");
        return;
    }

    try {
        // Odeslání požadavku na registraci
        let response = await fetch(`http://localhost:8080/api/users/register?username=${username}&password=${password}`, {
            method: "POST"
        });

        // Kontrola, zda odpověď obsahuje JSON
        let result;
        try {
            result = await response.json();
        } catch (jsonError) {
            console.error("Odpověď není platný JSON", jsonError);
            alert("Neplatná odpověď serveru.");
            return;
        }

        // Zpracování odpovědi
        if (response.ok) {
            alert(result.message); // Zobrazí zprávu od serveru
            if (result.success) {
                await loadUsers(); // Aktualizuj seznam uživatelů
            }
        } else {
            alert(`Chyba při registraci: ${result.message || "Neznámá chyba"}`);
        }
    } catch (error) {
        console.error("Chyba při registraci uživatele:", error);
        alert("Nastala chyba při připojení k serveru.");
    }
}

async function loginUser() {
    let username = document.getElementById("loginUsername").value.trim();
    let password = document.getElementById("loginPassword").value.trim();

    if (!username || !password) {
        alert("Zadejte uživatelské jméno a heslo.");
        return;
    }

    try {
        let response = await fetch("http://localhost:8080/api/users/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            alert("Neplatné přihlašovací údaje.");
            return;
        }

        let data = await response.json();

        if (data.success) {
            localStorage.setItem("loggedInUser", username);
            localStorage.setItem("isAdmin", data.admin); // Ukládáme informaci, zda je admin

            if (data.admin) {
                window.location.href = "admin-panel.html"; // Přesměrování pro admina
            } else {
                window.location.href = "user-panel.html"; // Přesměrování pro běžného uživatele
            }
        } else {
            alert("Neplatné přihlašovací údaje.");
        }
    } catch (error) {
        console.error("Chyba při přihlášení:", error);
        alert("Chyba serveru při přihlášení.");
    }
}


window.onload = loadUsers;



