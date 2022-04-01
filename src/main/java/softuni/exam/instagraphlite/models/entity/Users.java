package softuni.exam.instagraphlite.models.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class Users extends BaseEntity{

    private String username;
    private String password;
    private Pictures profilePictures;
    private Set<Posts> posts;

    public Users() {
    }

    @Column(nullable = false,unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false,unique = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToOne(optional = false)
    public Pictures getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(Pictures profilePictures) {
        this.profilePictures = profilePictures;
    }

    @OneToMany(mappedBy = "user")
    public Set<Posts> getPosts() {
        return posts;
    }

    public void setPosts(Set<Posts> posts) {
        this.posts = posts;
    }
}
