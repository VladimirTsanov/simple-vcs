async function createDocument(event) {
    event.preventDefault();
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    const requestBody = {
        title: title,
        content: content
    };

    try {
        const response = await fetch("/api/documents/new", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody),
            credentials: "same-origin"
        });

        if (response.ok) {
            const data = await response.json();
            console.log(data);
            alert("Document created successfully!");
            window.location.href = "/my_documents.html";
        } else {
            const errorData = await response.json();
            alert("Failed to create document: " + (errorData.message || "Try again"));
        }
    } catch (error) {
        console.error("Error creating document: ", error);
        alert("An error occurred while creating document. Please try again!");
    }

}