const BASE_URL = "http://localhost:8080";

function signup() {
    fetch(BASE_URL + "/api/auth/signup", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            role: document.getElementById("role").value
        })
    })
    .then(res => res.text())
    .then(data => alert(data))
    .catch(() => alert("Signup failed"));
}

function login() {
    fetch(BASE_URL + "/api/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            email: document.getElementById("loginEmail").value,
            password: document.getElementById("loginPassword").value
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Invalid credentials");
        return res.json();
    })
    .then(user => {
        localStorage.setItem("userId", user.id);
        localStorage.setItem("userName", user.name);
        localStorage.setItem("userRole", user.role);
        alert("Login Successful");
        window.location.href = "dashboard.html";
    })
    .catch(err => alert(err.message));
}

function showUser() {
    let name = localStorage.getItem("userName") || "User";
    let role = localStorage.getItem("userRole") || "";
    document.getElementById("username").innerText = `👤 ${name} (${role})`;
}

function createProject() {
    const adminId = document.getElementById("adminId").value;

    fetch(`${BASE_URL}/api/projects?userId=${adminId}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            title: document.getElementById("projectTitle").value,
            description: document.getElementById("projectDesc").value
        })
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(() => {
        alert("Project Created");
        loadProjects();
    })
    .catch(() => alert("Error creating project"));
}

function loadProjects() {
    fetch(BASE_URL + "/api/projects/all")
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(projects => {
        let select = document.getElementById("projectSelect");
        select.innerHTML = "";

        projects.forEach(p => {
            select.innerHTML += `<option value="${p.id}">${p.title}</option>`;
        });

        document.getElementById("projectCount").innerText = projects.length;
    })
    .catch(() => console.log("Error loading projects"));
}

function loadUsers() {
    fetch(BASE_URL + "/api/auth/users")
    .then(res => res.json())
    .then(users => {
        let select = document.getElementById("userSelect");
        select.innerHTML = "";

        users
        .filter(u => u.role === "MEMBER")
        .forEach(u => {
            select.innerHTML += `<option value="${u.id}">${u.name}</option>`;
        });
    })
    .catch(() => console.log("Error loading users"));
}

function createTask() {
    const userId = document.getElementById("userSelect").value;
    const projectId = document.getElementById("projectSelect").value;
    const dueDate = document.getElementById("taskDueDate").value;

    fetch(`${BASE_URL}/api/tasks?userId=${userId}&projectId=${projectId}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            title: document.getElementById("taskTitle").value,
            description: document.getElementById("taskDesc").value,
            status: "TODO",
            dueDate: dueDate
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Failed");

        return res.text();  
    })
    .then(() => {
        alert("Task Assigned");

        document.getElementById("taskTitle").value = "";
        document.getElementById("taskDesc").value = "";
        document.getElementById("taskDueDate").value = "";

        loadTasks();
    })
    .catch(() => alert("Error creating task"));
}
function loadTasks() {
    const role = localStorage.getItem("userRole");
    const userId = localStorage.getItem("userId");

    let url = role === "ADMIN"
        ? `${BASE_URL}/api/tasks/all`
        : `${BASE_URL}/api/tasks/user/${userId}`;

    fetch(url)
    .then(res => res.json())
    .then(tasks => {
        let list = document.getElementById("taskList");
        list.innerHTML = "";

        tasks.forEach(t => {

            let dueText = t.dueDate ? t.dueDate : "No Due Date";

            let color = "black";

            if (t.status === "DONE") color = "green";
            else if (t.status === "IN_PROGRESS") color = "orange";

            if (t.dueDate && new Date(t.dueDate) < new Date() && t.status !== "DONE") {
                color = "red";
                dueText += " (Overdue)";
            }

            list.innerHTML += `
            <li style="color:${color}">
                <b>${t.title}</b> (${t.status}) 
                - User: ${t.assignedTo || "N/A"} 
                - Due: ${dueText}

                <select id="status-${t.id}">
                    <option ${t.status === "TODO" ? "selected" : ""}>TODO</option>
                    <option ${t.status === "IN_PROGRESS" ? "selected" : ""}>IN_PROGRESS</option>
                    <option ${t.status === "DONE" ? "selected" : ""}>DONE</option>
                </select>

                <button onclick="updateStatus(${t.id})">Update</button>
                ${role === "ADMIN" ? `<button onclick="deleteTask(${t.id})">Delete</button>` : ""}
            </li>`;
        });
    })
    .catch(() => console.log("Error loading tasks"));
}
function updateStatus(id) {
    const status = document.getElementById(`status-${id}`).value;

    fetch(`${BASE_URL}/api/tasks/${id}?status=${status}&userId=1`, {
        method: "PUT"
    })
    .then(() => {
        alert("Status Updated");
        loadTasks();
        loadDashboard();
    })
    
    .catch(() => alert("Error updating status"));
    loadDashboard();
}
function handleRoleUI() {
    const role = localStorage.getItem("userRole");

    if (role === "MEMBER") {
        document.getElementById("adminSection").style.display = "none";
    }
}
function deleteTask(id) {
    fetch(`${BASE_URL}/api/tasks/${id}`, {
        method: "DELETE"
    })
    .then(() => loadTasks());
}

function loadDashboard() {
    const userId = localStorage.getItem("userId");
    const role = localStorage.getItem("userRole");

    let url = role === "ADMIN"
        ? BASE_URL + "/api/dashboard"
        : `${BASE_URL}/api/tasks/user/${userId}/stats`;

    fetch(url)
    .then(res => res.json())
    .then(data => {
        document.getElementById("total").innerText = data.totalTasks || 0;
        document.getElementById("done").innerText = data.completed || 0;
        document.getElementById("inProgress").innerText = data.inProgress || 0;
        document.getElementById("todo").innerText = data.toDo || 0;
        document.getElementById("overdue").innerText = data.overdue || 0;
    });
}
function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

window.onload = function () {
    showUser();
    handleRoleUI();
    loadDashboard();
    loadTasks();

    if (localStorage.getItem("userRole") === "ADMIN") {
        loadProjects();
        loadUsers();
    }
};