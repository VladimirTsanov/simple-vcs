package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.entity.*;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final VersionRepository versionRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService; 

    @Transactional
    public Document createDocument(DocumentRequest request, Long authorId) throws UserNotFoundException {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User with id" + authorId + " not found"));
        
        Document document = Document.builder()
                .title(request.title())
                .build();
        document = documentRepository.save(document);
        
        Version v1 = Version.builder()
                .document(document)
                .content(request.content())
                .versionNumber(1)
                .status(VersionStatus.DRAFT)
                .author(author)
                .build();
                
        document.setActiveVersion(v1);
        document.getVersions().add(v1);
        
        Document savedDocument = documentRepository.save(document);

        auditLogService.logActionWithActor(author, "CREATE", "Document", savedDocument.getId(), "Created document: " + request.title());

        return savedDocument;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getPersonalDocuments(Long authorId) {
        return documentRepository.findByActiveVersionAuthorId(authorId);
    }

    public Document getDocumentById(Long documentId) throws SAP.Project.simple_vcs.exception.DocumentNotFoundException {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new SAP.Project.simple_vcs.exception.DocumentNotFoundException(
                        "Document with id " + documentId + " not found"));
    }

    @Transactional
    public void deleteDocument(Long documentId) throws SAP.Project.simple_vcs.exception.DocumentNotFoundException {
        Document document = getDocumentById(documentId);
        documentRepository.delete(document);

        
        auditLogService.logAction("DELETE", "Document", documentId, "Deleted document");
    }
}