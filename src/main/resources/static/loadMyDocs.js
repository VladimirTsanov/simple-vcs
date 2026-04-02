async function loadMyDocs() {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch("/api/documents/my", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            }
        });

        const isJson = response.headers.get('content-type')?.includes('application/json');
        const data = isJson ? await response.json() : null;

        if (response.ok) {
            displayMyDocuments(data);
        } else {
            alert("Failed to load documents: " + (data?.message || "Server error " + response.status));
        }
    } catch (error) {
        console.error("Error loading documents: ", error);
        alert("An error occurred while loading documents.");
    }
}

function displayMyDocuments(documents) {
    const container = document.getElementById("my-documents-list");
    if (!container) return;

    container.innerHTML = '';

    if (!documents || documents.length === 0) {
        container.innerHTML = '<p>No documents found</p>';
        return;
    }

    documents.forEach(doc => {
        const docElement = document.createElement("div");
        docElement.className = "document-item";

        docElement.innerHTML = `
            <h3 class="doc-title"></h3>
            <p>Owner: ${doc.authorUsername || 'Unknown'}</p>
            <p>Active version: ${doc.version || '1.0'}</p>
            <p>Status: ${doc.status}</p>
            <p>Created: ${doc.createdAt ? new Date(doc.createdAt).toLocaleDateString() : 'N/A'}</p>
            <a href="/document_details.html?id=${doc.id}">View</a>
            <hr>
        `;

        docElement.querySelector('.doc-title').textContent = doc.title;

        container.appendChild(docElement);
    });
}

document.addEventListener("DOMContentLoaded", loadMyDocs);