async function loadDocuments() {
    try {
        const response = await fetch("/api/documents/all", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }

        })

        if (response.ok) {
            const data = await response.json();
            displayDocuments(data);
        } else {
            const errorData = await response.json();
            alert("Failed to load documents: " + (errorData.message || "Try again"));
        }

    }
    catch (error) {
        console.error("Error loading documents: ", error);
        alert("An error occurred while loading documents. Please try again!");
    }
}

function displayDocuments(documents) {
    const container = document.getElementById("document-container")
    container.innerHTML = '';

    if (documents.length === 0) {
        container.innerHTML = `
        <p>No documents found</p>
        `
        return;
    }

    documents.forEach(doc => {
        const docElement = document.createElement("div");
        docElement.className = "document-item";
        docElement.innerHTML = `
        <h3>${doc.title}</h3>
        <p>Owner: ${doc.authorUsername}</p>
        <p>Active version: ${doc.version}</p>
        <p>Status: ${doc.status}</p>
        <p>Created: ${new Date(doc.createdAt).toLocaleDateString()}</p>
        <a href="/document_details.html?id=${doc.id}">View</a>
        <hr>
        `
        container.appendChild(docElement);
    })
}

document.addEventListener("DOMContentLoaded", function () {
    loadDocuments();
});