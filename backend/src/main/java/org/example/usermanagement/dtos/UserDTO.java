package org.example.usermanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import org.example.usermanagement.entity.User;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
    private Set<String> permissions;

    public UserDTO() {}
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isAdmin = user.isAdmin();
        this.permissions = user.getPermissions().stream()
                               .map(p -> p.getName()) // assuming Permission has `getName()`
                               .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                ", permissions=" + permissions +
                '}';
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
