async function registerUser(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const requestBody = {
        username: username,
        email: email,
        password: password
    };

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            const data = await response.json();
            alert("Registration successful!");
            window.location.href = data.redirectUrl;
        } else {
            const errorData = await response.json();
            alert("Registration error: " + (errorData.message || "Try again"));
        }
    }
    catch (error) {
        console.error("Registration error: ", error);
        alert("An error occurred while connecting to the server. Please try again!");
    }

}


