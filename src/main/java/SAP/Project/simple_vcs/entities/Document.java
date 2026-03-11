package SAP.Project.simple_vcs.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @OneToOne
    @JoinColumn(name = "active_version_id")
    private Version activeVersion;
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<Version> versions = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    public Document()
    {

    }
    public Document(String title)
    {
        setTitle(title);
        setVersions(new ArrayList<>());
        setCreatedAt(LocalDateTime.now());
    }
    public Long getId()
    {
        return this.id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public Version getActiveVersion()
    {
        return this.activeVersion;
    }
    public void setActiveVersion(Version activeVersion)
    {
        this.activeVersion = activeVersion;
    }
    public List<Version>getVersions()
    {
        return this.versions;
    }
    public void setVersions(List<Version>versions)
    {
        this.versions = versions;
    }
    public LocalDateTime getCreatedAt()
    {
        return this.createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }
}
