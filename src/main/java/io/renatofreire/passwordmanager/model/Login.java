package io.renatofreire.passwordmanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "login")
public class Login {

    @Id
    @SequenceGenerator(name = "password_sequence", sequenceName = "password_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_sequence")
    private Long id;
    private String name;
    private String username;
    private String password;
    private String url;
    private String description;
    @ManyToOne
    @JoinColumn(name="login_userId", nullable = false)
    private User user;

    public Login() {
    }

    public Login(String name, String username, String password, String url, String description, User user) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.url = url;
        this.description = description;
        this.user = user;
    }

    public Login(Long id, String name, String username, String password, String url, String description, User user) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.url = url;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}