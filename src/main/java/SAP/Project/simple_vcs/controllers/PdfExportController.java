package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.services.DocumentService;
import SAP.Project.simple_vcs.services.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PdfExportController {

    private final PdfExportService pdfExportService;
    private final DocumentService documentService;

    @GetMapping("/document/{id}/export/pdf")
    public ResponseEntity<byte[]> exportDocumentToPdf(@PathVariable Long id) throws DocumentNotFoundException, IOException {
        byte[] pdfContent = pdfExportService.exportDocumentToPdf(id);

        var document = documentService.getDocumentById(id);
        String filename = "document_" + document.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
