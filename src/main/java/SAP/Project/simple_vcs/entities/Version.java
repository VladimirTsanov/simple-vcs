package SAP.Project.simple_vcs.entities;

import SAP.Project.simple_vcs.enums.VersionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "versions")
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
    @Column(nullable = false)
    private Integer versionNumber;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VersionStatus status = VersionStatus.DRAFT;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer = null;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Version() {

    }

    public Version(Document document, Integer versionNumber, String content, User author) {
        setDocument(document);
        setVersionNumber(versionNumber);
        setContent(content);
        setAuthor(author);
        setReviewer(null);
        setStatus(VersionStatus.DRAFT);
        setCreatedAt(LocalDateTime.now());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return this.document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Integer getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public VersionStatus getStatus() {
        return this.status;
    }

    public void setStatus(VersionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
