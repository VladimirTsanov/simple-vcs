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

async function handleComparison() {
    const oldV = document.querySelector('input[name="oldVersion"]:checked');
    const newV = document.querySelector('input[name="newVersion"]:checked');

    if (!oldV || !newV) {
        alert("Please select two versions to compare!");
        return;
    }

    try {
        const response = await fetch(`/api/versions/compare/${oldV.value}/${newV.value}`);
        const data = await response.json();

        const display = document.getElementById('diff-display');
        const content = document.getElementById('diff-content');

        content.innerHTML = '';
        display.style.display = 'block';

        data.forEach(line => {
            const p = document.createElement('p');
            p.textContent = line.text;
            p.style.margin = "0";
            p.style.padding = "2px 5px";

            if (line.type === 'INSERT') {
                p.style.backgroundColor = '#e6ffec';
                p.textContent = '+ ' + line.text;
                p.style.color = '#24292e';
            } else if (line.type === 'DELETE') {
                p.style.backgroundColor = '#ffeef0';
                p.style.textDecoration = 'line-through';
                p.textContent = '- ' + line.text;
                p.style.color = '#24292e';
            }
            content.appendChild(p);
        });
    } catch (error) {
        console.error("Comparison failed:", error);
    }
}