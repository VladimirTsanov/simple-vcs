function toggleTheme(event) {
    event.preventDefault();
    document.body.classList.toggle('purple-theme');
}

const themeBtn = document.getElementById('theme-switcher');
const body = document.body;

if (themeBtn) {
    themeBtn.addEventListener('click', () => {
        body.classList.toggle('dark-theme');
        if (body.classList.contains('dark-theme')) {
            localStorage.setItem('theme', 'dark');
        } else {
            localStorage.setItem('theme', 'light');
        }
    });
}

(function () {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
    } else {
        document.body.classList.remove('dark-theme');
    }
})();

function toggleForms() {
    const login = document.getElementById('login-side');
    const register = document.getElementById('register-side');

    if (login.classList.contains('hidden')) {
        login.classList.remove('hidden');
        login.classList.add('fade-in');
        register.classList.add('hidden');
    } else {
        register.classList.remove('hidden');
        register.classList.add('fade-in');
        login.classList.add('hidden');
    }
}

function goToPage() {
    const url = document.getElementById('loginBtn').getAttribute('data-url');
    window.location.href = url;
}

async function compareSelectedVersions() {
    const oldId = document.getElementById('oldVersionId').value;
    const newId = document.getElementById('newVersionId').value;

    if (!oldId || !newId || oldId === newId) {
        alert("Please select two different versions.");
        return;
    }

    try {

        const response = await fetch('/versions/compare', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                oldVersionId: oldId,
                newVersionId: newId
            })
        });

        if (!response.ok) throw new Error("Failed to fetch comparison");

        const data = await response.json();
        renderDiff(data);
    } catch (error) {
        console.error("Diff failed:", error);
    }
}

function renderDiff(data) {
    const resultsArea = document.getElementById('diff-results');
    const container = document.getElementById('diff-container');

    resultsArea.innerHTML = '';
    container.classList.remove('hidden');

    let oldLineNum = 1;
    let newLineNum = 1;

    data.forEach(line => {
        const row = document.createElement('div');
        row.className = 'diff-row';

        // Създаваме клетките за номерата
        const oldNumCell = document.createElement('div');
        oldNumCell.className = 'diff-num';

        const newNumCell = document.createElement('div');
        newNumCell.className = 'diff-num';

        const textCell = document.createElement('div');
        textCell.className = 'diff-text';

        if (line.type === 'INSERT') {
            row.classList.add('row-insert');
            oldNumCell.textContent = ''; // Празно в старата колона
            newNumCell.textContent = newLineNum++;
            textCell.textContent = '+ ' + line.text;
        }
        else if (line.type === 'DELETE') {
            row.classList.add('row-delete');
            oldNumCell.textContent = oldLineNum++;
            newNumCell.textContent = ''; // Празно в новата колона
            textCell.textContent = '- ' + line.text;
            textCell.style.textDecoration = 'line-through';
        }
        else {
            row.classList.add('row-equal');
            oldNumCell.textContent = oldLineNum++;
            newNumCell.textContent = newLineNum++;
            textCell.textContent = '  ' + line.text;
        }

        row.appendChild(oldNumCell);
        row.appendChild(newNumCell);
        row.appendChild(textCell);
        resultsArea.appendChild(row);
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const container = document.getElementById("commentsContainer");
    if (!container) return;

    const docId = container.getAttribute("data-doc-id");
    const versionId = container.getAttribute("data-version-id");

    // Store globally for submission
    window.currentDocId = docId;
    window.currentVersionId = versionId;

    if (docId && docId !== "0" && versionId && versionId !== "0") {
        loadComments();
    }
});

async function loadComments() {
    try {
        const response = await fetch(`/api/documents/${window.currentDocId}/versions/${window.currentVersionId}/comment/all`);
        if (!response.ok) throw new Error("Failed to fetch comments");

        const comments = await response.json();
        const container = document.getElementById("commentsContainer");
        container.innerHTML = "";

        if (comments.length === 0) {
            container.innerHTML = `<p style="opacity: 0.5; font-style: italic;">No discussion yet. Be the first to comment!</p>`;
            return;
        }

        comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).forEach(comment => {
            const el = document.createElement("div");
            el.className = "comment-card fade-in";

            const dateObj = new Date(comment.createdAt);
            const formattedDate = isNaN(dateObj) ? '' : dateObj.toLocaleString('en-US', {
                year: 'numeric', month: 'short', day: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });

            const rolesStr = (comment.authorRoles || []).map(r => r.replace(/^ROLE_/i, '')).join(', ');

            el.innerHTML = `
                <div class="comment-header">
                    <span class="comment-author">@${comment.authorUsername} (${rolesStr})</span>
                    <span class="comment-meta" style="opacity: 0.7;">${formattedDate}</span>
                    <span class="comment-meta" style="opacity: 0.7;">v${comment.versionNum}</span>
                    <span class="comment-meta" style="opacity: 0.7;">Written at version status: ${comment.versionStatus}</span>
                </div>
                <div class="comment-body">${comment.content}</div>
            `;
            container.appendChild(el);
        });
    } catch (e) {
        console.error(e);
        document.getElementById("commentsContainer").innerHTML = `<p style="color: red;">Error loading comments.</p>`;
    }
}

async function submitNewComment() {
    const contentBox = document.getElementById("newCommentContent");
    const content = contentBox.value.trim();
    if (!content) return;

    try {
        const response = await fetch(`/api/documents/${window.currentDocId}/versions/${window.currentVersionId}/comment/new`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                documentId: window.currentDocId,
                content: content
            })
        });

        if (!response.ok) throw new Error("Failed to submit comment");

        contentBox.value = "";
        await loadComments();
    } catch (e) {
        console.error(e);
        alert("There was an error posting your comment.");
    }
}
























function toggleChat() {
            const window = document.getElementById('chat-window');
            window.style.display = window.style.display === 'flex' ? 'none' : 'flex';
        }

        // Send message on 'Enter' key
        function handleKey(e) {
            if (e.key === 'Enter') sendMessage();
        }

        async function sendMessage() {
            const input = document.getElementById('userPrompt');
            const box = document.getElementById('chat-box');
            const msg = input.value.trim();

            if (!msg) return;

            // Add user message to UI
            box.innerHTML += `<div class="msg user">${msg}</div>`;
            input.value = '';
            box.scrollTop = box.scrollHeight;

            try {
                const response = await fetch('/api/chat', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({message: msg})
                });
                const data = await response.json();

                // Add AI response to UI
                box.innerHTML += `<div class="msg bot">${data.reply}</div>`;
                box.scrollTop = box.scrollHeight;
            } catch (error) {
                box.innerHTML += `<div class="msg bot" style="color:red">Error connecting to server.</div>`;
            }
        }