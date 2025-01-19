document.addEventListener("DOMContentLoaded", () => {
    let currentUser = localStorage.getItem("loggedInUser");
    if (!currentUser) {
        alert("Nejste přihlášen.");
        window.location.href = "index.html";
        return;
    }

    document.getElementById("currentUsername").textContent = currentUser;
});

async function updateUser() {
    let newUsername = document.getElementById("newUsername").value.trim();
    let newPassword = document.getElementById("newPassword").value.trim();
    let currentUser = localStorage.getItem("loggedInUser");

    if (!currentUser) {
        alert("Nejste přihlášen.");
        return;
    }

    try {
        let response = await fetch("http://localhost:8080/api/users/update", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                oldUsername: currentUser,
                newUsername: newUsername || null,
                newPassword: newPassword || null
            })
        });

        let result = await response.json();

        if (result.success) {
            alert("Údaje byly úspěšně aktualizovány.");
            localStorage.setItem("loggedInUser", newUsername || currentUser);
            location.reload();
        } else {
            alert("Chyba při aktualizaci: " + result.message);
        }
    } catch (error) {
        console.error("Chyba při aktualizaci uživatele:", error);
        alert("Chyba serveru při aktualizaci.");
    }
}


function logout() {
    localStorage.removeItem("loggedInUser");
    window.location.href = "index.html";
}
