package SAP.Project.simple_vcs.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    public Role()
    {

    }
    public Role(String name, String description)
    {
        setName(name);
        setDescription(description);
    }
    public Long getId()
    {
        return this.id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDescription()
    {
        return this.description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

}
