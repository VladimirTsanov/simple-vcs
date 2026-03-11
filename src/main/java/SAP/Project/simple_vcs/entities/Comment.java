package SAP.Project.simple_vcs.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "version_id",nullable = false)
    private Version version;
    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    private User author;
    @Column(nullable = false)
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    public Comment()
    {

    }
    public Comment(Version version, User author, String content)
    {
        setVersion(version);
        setAuthor(author);
        setContent(content);
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
    public Version getVersion()
    {
        return this.version;
    }
    public void setVersion(Version version)
    {
        this.version = version;
    }
    public User getAuthor()
    {
        return this.author;
    }
    public void setAuthor(User author)
    {
        this.author = author;
    }
    public String getContent()
    {
        return this.content;
    }
    public void setContent(String content)
    {
        this.content = content;
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
