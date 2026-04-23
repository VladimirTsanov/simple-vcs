package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.entity.VersionStatus;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private VersionRepository versionRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private DocumentService documentService;

    // -------------------------------------------------------------------------
    // createDocument
    // -------------------------------------------------------------------------

    @Test
    void createDocument_success_createsDraftVersionOneAsActive() throws Exception {
        User author = new User();
        author.setId(1L);

        // First save returns a document with an ID; second save returns the final state
        Document docWithId = Document.builder().id(10L).title("New Doc").build();
        Document finalDoc = Document.builder().id(10L).title("New Doc").build();
        Version v1 = Version.builder().id(1L).versionNumber(1).status(VersionStatus.DRAFT).build();
        finalDoc.setActiveVersion(v1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(documentRepository.save(any())).thenReturn(docWithId, finalDoc);

        Document result = documentService.createDocument(new DocumentRequest("New Doc", "content"), 1L);

        assertThat(result.getId()).isEqualTo(10L);
        verify(documentRepository, times(2)).save(any());
        verify(auditLogService).logActionWithActor(eq(author), eq("CREATE"), eq("Document"), eq(10L), any());
    }

    @Test
    void createDocument_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> documentService.createDocument(new DocumentRequest("Doc", "content"), 99L));
        verify(documentRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // getDocumentById
    // -------------------------------------------------------------------------

    @Test
    void getDocumentById_found_returnsDocument() throws Exception {
        Document doc = Document.builder().id(5L).title("Doc").build();
        when(documentRepository.findById(5L)).thenReturn(Optional.of(doc));

        assertThat(documentService.getDocumentById(5L)).isSameAs(doc);
    }

    @Test
    void getDocumentById_notFound_throwsDocumentNotFoundException() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.getDocumentById(99L));
    }

    // -------------------------------------------------------------------------
    // deleteDocument
    // -------------------------------------------------------------------------

    @Test
    void deleteDocument_success() throws Exception {
        Document doc = Document.builder().id(5L).title("Doc").build();
        when(documentRepository.findById(5L)).thenReturn(Optional.of(doc));

        documentService.deleteDocument(5L);

        verify(documentRepository).delete(doc);
        verify(auditLogService).logAction(eq("DELETE"), eq("Document"), eq(5L), any());
    }

    @Test
    void deleteDocument_notFound_throwsDocumentNotFoundException() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> documentService.deleteDocument(99L));
        verify(documentRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // shareDocument
    // -------------------------------------------------------------------------

    @Test
    void shareDocument_byUsername_success() throws Exception {
        Document doc = Document.builder().id(5L).title("Doc").build();
        User user = new User();
        user.setId(2L);
        user.setUsername("bob");

        when(documentRepository.findById(5L)).thenReturn(Optional.of(doc));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(documentRepository.save(doc)).thenReturn(doc);

        Document result = documentService.shareDocument(5L, "bob");

        assertThat(result.getSharedWith()).contains(user);
        verify(auditLogService).logAction(eq("SHARE"), eq("Document"), eq(5L), any());
    }

    @Test
    void shareDocument_byEmail_fallback_success() throws Exception {
        Document doc = Document.builder().id(5L).title("Doc").build();
        User user = new User();
        user.setId(3L);
        user.setEmail("bob@example.com");

        when(documentRepository.findById(5L)).thenReturn(Optional.of(doc));
        when(userRepository.findByUsername("bob@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("bob@example.com")).thenReturn(user);
        when(documentRepository.save(doc)).thenReturn(doc);

        Document result = documentService.shareDocument(5L, "bob@example.com");

        assertThat(result.getSharedWith()).contains(user);
    }

    @Test
    void shareDocument_userNotFound_throwsUserNotFoundException() {
        Document doc = Document.builder().id(5L).title("Doc").build();

        when(documentRepository.findById(5L)).thenReturn(Optional.of(doc));
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ghost")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> documentService.shareDocument(5L, "ghost"));
    }

    // -------------------------------------------------------------------------
    // getDocumentsSharedWithUser
    // -------------------------------------------------------------------------

    @Test
    void getDocumentsSharedWithUser_userFound_returnsList() {
        User user = new User();
        user.setId(1L);
        List<Document> docs = List.of(Document.builder().id(1L).title("A").build());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findBySharedWithContaining(user)).thenReturn(docs);

        assertThat(documentService.getDocumentsSharedWithUser(1L)).hasSize(1);
    }

    @Test
    void getDocumentsSharedWithUser_userNotFound_returnsEmptyList() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(documentService.getDocumentsSharedWithUser(99L)).isEmpty();
        verify(documentRepository, never()).findBySharedWithContaining(any());
    }
}
