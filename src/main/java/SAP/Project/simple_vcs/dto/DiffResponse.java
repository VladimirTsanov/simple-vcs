package SAP.Project.simple_vcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffResponse {
    private String type; // "INSERT", "DELETE", "EQUAL"
    private String text;
}