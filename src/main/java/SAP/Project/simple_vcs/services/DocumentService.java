package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.entity.*;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.repository.*;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SAP.Project.simple_vcs.exception.DocumentNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final VersionRepository versionRepository;
    private final UserRepository userRepository;

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
        return documentRepository.save(document);
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
    public void deleteDocument(Long documentId) throws DocumentNotFoundException {
        Document document = getDocumentById(documentId);
        documentRepository.delete(document);
    }

    @Transactional
    public Document shareDocument(Long documentId, String emailOrUsername)
            throws DocumentNotFoundException, UserNotFoundException {
        Document document = getDocumentById(documentId);
        User user = userRepository.findByUsername(emailOrUsername)
                .or(() -> java.util.Optional.ofNullable(userRepository.findByEmail(emailOrUsername)))
                .orElseThrow(() -> new UserNotFoundException("No user found with username or email: " + emailOrUsername));
        document.getSharedWith().add(user);
        return documentRepository.save(document);
    }

    public List<Document> getDocumentsSharedWithUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ArrayList<>();
        return documentRepository.findBySharedWithContaining(user);
    }

}
