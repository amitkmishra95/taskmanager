package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    @JsonIgnoreProperties({"tasks", "projects"})
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"tasks"})
    private Project project;

    private LocalDate dueDate;
}