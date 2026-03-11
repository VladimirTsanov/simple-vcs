package SAP.Project.simple_vcs.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false,unique = true)
    private String username;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean isActive;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
            (name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    public User()
    {

    }
    public User(String email, String username, String passwordHash)
    {
        setEmail(email);
        setUsername(username);
        setPasswordHash(passwordHash);
        setIsActive(true);
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
    public String getEmail()
    {
        return this.email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPasswordHash()
    {
        return this.passwordHash;
    }
    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }
    public LocalDateTime getCreatedAt()
    {
        return this.createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }
    public Boolean getIsActive()
    {
        return this.isActive;
    }
    public void setIsActive(Boolean isActive)
    {
        this.isActive = isActive;
    }
    public Set<Role> getRoles()
    {
        return this.roles;
    }
    public void setRoles(Set<Role>roles)
    {
        this.roles = roles;
    }
}
