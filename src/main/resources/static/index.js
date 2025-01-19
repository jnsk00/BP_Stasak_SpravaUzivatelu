async function loadUsers() {
    try {
        let response = await fetch("http://localhost:8080/api/users/list");
        if (!response.ok) throw new Error("Chyba při načítání uživatelů");

        let users = await response.json();
        let userList = document.getElementById("userList");

        if (!userList) {
            console.error("Element s id 'userList' nebyl nalezen.");
            return;
        }

        userList.innerHTML = "";

        users.forEach(user => {
            let li = document.createElement("li");
            li.innerText = user.username;
            userList.appendChild(li);
        });
    } catch (error) {
        console.error("Chyba při načítání uživatelů:", error);
    }
}

// Zavolání funkce až po načtení DOM
document.addEventListener("DOMContentLoaded", loadUsers);
