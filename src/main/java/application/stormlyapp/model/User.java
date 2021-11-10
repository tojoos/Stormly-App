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

    @NotBlank
    @Column(name = "login")
    private String login;

    @NotBlank
    @Length(min = 6, message = "Password's length must be at least 6 digits long")
    @Column(name = "password")
    private String password;

    @NotBlank
    @Email(message = "Wrong email format")
    @Column(name = "email")
    private String email;
}
