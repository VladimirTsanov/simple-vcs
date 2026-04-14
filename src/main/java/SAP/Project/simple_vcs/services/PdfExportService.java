package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final DocumentService documentService;

    public byte[] exportDocumentToPdf(Long id) throws DocumentNotFoundException, IOException {
        Document document = documentService.getDocumentById(id);
        Version activeVersion = document.getActiveVersion();

        if (activeVersion == null || activeVersion.getStatus() != SAP.Project.simple_vcs.entity.VersionStatus.APPROVED) {
            throw new IllegalStateException("Document has no approved version to export.");
        }

        String content = activeVersion.getContent();
        String title = document.getTitle();

        // Create a simple HTML structure for the PDF (XHTML compliant)
        String rawHtml = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>" + title + "</title>" +
                "<style>" +
                "body { font-family: 'Arial', sans-serif; line-height: 1.6; padding: 20px; }" +
                "h1 { color: #2c3e50; text-align: center; border-bottom: 2px solid #34495e; padding-bottom: 10px; }" +
                ".metadata { color: #7f8c8d; font-size: 0.9em; margin-bottom: 30px; }" +
                ".content { white-space: pre-wrap; font-size: 1.1em; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>" + title + "</h1>" +
                "<div class='metadata'>" +
                "<p>Version: " + activeVersion.getVersionNumber() + "</p>" +
                "<p>Author: " + (activeVersion.getAuthor() != null ? activeVersion.getAuthor().getUsername() : "Unknown") + "</p>" +
                "<p>Approved by: " + (activeVersion.getReviewer() != null ? activeVersion.getReviewer().getUsername() : "N/A") + "</p>" +
                "<p>Created: " + activeVersion.getCreatedAt() + "</p>" +
                "</div>" +
                "<div class='content'>" + content + "</div>" +
                "</body>" +
                "</html>";

        // Clean HTML to make it strictly XHTML compliant for OpenHTMLtoPDF
        org.jsoup.nodes.Document doc = Jsoup.parse(rawHtml);
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        String xhtml = doc.html();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(xhtml, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new IOException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
