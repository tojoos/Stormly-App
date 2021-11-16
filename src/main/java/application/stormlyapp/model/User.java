package application.stormlyapp.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Login cannot be empty")
    @Column(name = "login")
    private String login;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 6, message = "Password must contain at least 6 digits")
    @Column(name = "password")
    private String password;

    @Transient
    private String confirmedPassword;

    @Email(message = "Wrong email format")
    @Column(name = "email")
    private String email;
}
