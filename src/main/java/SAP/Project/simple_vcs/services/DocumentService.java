package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.entity.*;
import SAP.Project.simple_vcs.repository.*;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
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

    @Transactional
    public Document createDocument(DocumentRequest request) {
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
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
}
