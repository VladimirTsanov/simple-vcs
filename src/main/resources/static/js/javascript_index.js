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

(function() {
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
