package ru.sbp.objects.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String secretKey;

    public User(String username){
        this.username = username;
    }

    public User(String username, String secretKey){
        this.username = username;
        this.secretKey = secretKey;
    }
}
