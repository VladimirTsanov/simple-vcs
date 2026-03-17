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
        Document document = new Document();
        document.setTitle(request.title());
        document = documentRepository.save(document);
        Version v1 = new Version();
        v1.setDocument(document);
        v1.setContent(request.content());
        v1.setVersionNumber(1);
        v1.setStatus(VersionStatus.DRAFT);
        v1.setAuthor(author);
        versionRepository.save(v1);
        document.setActiveVersion(v1);
        return documentRepository.save(document);
    }
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}
