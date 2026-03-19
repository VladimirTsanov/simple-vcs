package SAP.Project.simple_vcs.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter @Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "active_version_id")
    private Version activeVersion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Version> versions;
}
