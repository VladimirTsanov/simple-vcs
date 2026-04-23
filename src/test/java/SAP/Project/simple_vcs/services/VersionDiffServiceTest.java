package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DiffResponse;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionDiffServiceTest {

    @Mock private VersionRepository versionRepository;

    @InjectMocks private VersionDiffService versionDiffService;

    @Test
    void compareVersions_identicalContent_allEqual() throws Exception {
        mockVersions("line1\nline2\nline3", "line1\nline2\nline3");

        List<DiffResponse> result = versionDiffService.compareVersions(1L, 2L);

        assertThat(result).hasSize(3);
        assertThat(result.stream().map(DiffResponse::getType).toList()).containsOnly("EQUAL");
    }

    @Test
    void compareVersions_addedLines_producesInserts() throws Exception {
        mockVersions("line1", "line1\nline2");

        List<DiffResponse> result = versionDiffService.compareVersions(1L, 2L);

        assertThat(result.stream().map(DiffResponse::getType).toList())
                .containsExactly("EQUAL", "INSERT");
        assertThat(result.stream().map(DiffResponse::getText).toList())
                .containsExactly("line1", "line2");
    }

    @Test
    void compareVersions_removedLines_producesDeletes() throws Exception {
        mockVersions("line1\nline2", "line1");

        List<DiffResponse> result = versionDiffService.compareVersions(1L, 2L);

        assertThat(result.stream().map(DiffResponse::getType).toList())
                .containsExactly("EQUAL", "DELETE");
        assertThat(result.stream().map(DiffResponse::getText).toList())
                .containsExactly("line1", "line2");
    }

    @Test
    void compareVersions_changedLine_producesDeleteThenInsert() throws Exception {
        mockVersions("hello", "world");

        List<DiffResponse> result = versionDiffService.compareVersions(1L, 2L);

        assertThat(result.stream().map(DiffResponse::getType).toList())
                .containsExactly("DELETE", "INSERT");
        assertThat(result.stream().map(DiffResponse::getText).toList())
                .containsExactly("hello", "world");
    }

    @Test
    void compareVersions_crlfLineEndings_splitCorrectly() throws Exception {
        mockVersions("line1\r\nline2", "line1\r\nline2");

        List<DiffResponse> result = versionDiffService.compareVersions(1L, 2L);

        assertThat(result.stream().map(DiffResponse::getType).toList()).containsOnly("EQUAL");
        assertThat(result).hasSize(2);
    }

    @Test
    void compareVersions_oldVersionNotFound_throwsVersionNotFoundException() {
        when(versionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(VersionNotFoundException.class,
                () -> versionDiffService.compareVersions(1L, 2L));
    }

    @Test
    void compareVersions_newVersionNotFound_throwsVersionNotFoundException() {
        Version old = Version.builder().id(1L).content("old content").build();
        when(versionRepository.findById(1L)).thenReturn(Optional.of(old));
        when(versionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(VersionNotFoundException.class,
                () -> versionDiffService.compareVersions(1L, 2L));
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private void mockVersions(String oldContent, String newContent) {
        Version oldV = Version.builder().id(1L).content(oldContent).build();
        Version newV = Version.builder().id(2L).content(newContent).build();
        when(versionRepository.findById(1L)).thenReturn(Optional.of(oldV));
        when(versionRepository.findById(2L)).thenReturn(Optional.of(newV));
    }
}
