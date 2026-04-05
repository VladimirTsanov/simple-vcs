function toggleTheme(event) {
    event.preventDefault();
    document.body.classList.toggle('purple-theme');
}

const themeBtn = document.getElementById('theme-switcher');
const body = document.body;

themeBtn.addEventListener('click', () => {
    // 1. Toggle the class
    body.classList.toggle('dark-theme');

    // 2. Save the current state to LocalStorage
    if (body.classList.contains('dark-theme')) {
        localStorage.setItem('theme', 'dark');
    } else {
        localStorage.setItem('theme', 'light');
    }
});

// Run this immediately on page load
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