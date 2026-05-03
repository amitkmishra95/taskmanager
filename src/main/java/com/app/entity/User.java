package com.app.entity;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

@Entity
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference
    @JsonIgnore
    private List<Project> projects;
    
    @OneToMany(mappedBy = "assignedTo")
    @JsonIgnore  
    private List<Task> tasks;
//    
}