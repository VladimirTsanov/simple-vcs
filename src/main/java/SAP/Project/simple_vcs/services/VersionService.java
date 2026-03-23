package SAP.Project.simple_vcs.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SAP.Project.simple_vcs.dto.VersionRequest;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.entity.VersionStatus;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionService {
    private final VersionRepository versionRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Version createNewVersion(VersionRequest request) {
        Document document = documentRepository.findById(request.documentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Automatically calculate the next version number
        int nextVersionNumber = document.getVersions().size() + 1;

        Version newVersion = Version.builder()
                .document(document)
                .content(request.content())
                .versionNumber(nextVersionNumber)
                .status(VersionStatus.DRAFT) // New versions always start as drafts
                .author(author)
                .build();

        return versionRepository.save(newVersion);
    }

    public List<Version> getVersionsForDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return document.getVersions();
    }
}