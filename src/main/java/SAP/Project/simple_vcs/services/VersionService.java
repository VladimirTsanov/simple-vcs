package SAP.Project.simple_vcs.services;

import java.util.List;

import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
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
    public Version createNewVersion(VersionRequest request, Long authorId) throws UserNotFoundException, DocumentNotFoundException {
        Document document = documentRepository.findById(request.documentId())
                .orElseThrow(() -> new DocumentNotFoundException("Document with id" + request.documentId() + " not found"));
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + authorId +" not found"));

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

    public List<Version> getVersionsForDocument(Long documentId) throws DocumentNotFoundException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id" + documentId + " not found"));
        return document.getVersions();
    }
}