package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.DiffResponse;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
import SAP.Project.simple_vcs.repository.VersionRepository;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VersionDiffService {
    private final VersionRepository versionRepository;

    public List<DiffResponse> compareVersions(Long oldId, Long newId) throws VersionNotFoundException {
        Version oldV = versionRepository.findById(oldId)
                .orElseThrow(() -> new VersionNotFoundException("Old version not found"));
        Version newV = versionRepository.findById(newId)
                .orElseThrow(() -> new VersionNotFoundException("New version not found"));

        List<String> oldLines = Arrays.asList(oldV.getContent().split("\\r?\\n"));
        List<String> newLines = Arrays.asList(newV.getContent().split("\\r?\\n"));

        Patch<String> patch = DiffUtils.diff(oldLines, newLines);
        List<DiffResponse> diffResults = new ArrayList<>();

        int lastLine = 0;
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            // Добавяме непроменените редове преди делтата
            while (lastLine < delta.getSource().getPosition()) {
                diffResults.add(new DiffResponse("EQUAL", oldLines.get(lastLine)));
                lastLine++;
            }

            // Обработваме самата промяна
            if (delta.getType() == DeltaType.DELETE || delta.getType() == DeltaType.CHANGE) {
                for (String line : delta.getSource().getLines()) {
                    diffResults.add(new DiffResponse("DELETE", line));
                }
            }
            if (delta.getType() == DeltaType.INSERT || delta.getType() == DeltaType.CHANGE) {
                for (String line : delta.getTarget().getLines()) {
                    diffResults.add(new DiffResponse("INSERT", line));
                }
            }
            lastLine += delta.getSource().getLines().size();
        }

        // Добавяме останалите непроменени редове до края
        while (lastLine < oldLines.size()) {
            diffResults.add(new DiffResponse("EQUAL", oldLines.get(lastLine)));
            lastLine++;
        }

        return diffResults;
    }
}