package com.app.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.*;

@Getter @Setter
public class TaskDTO {

    private Long id;

    @NotBlank
    private String title;

    private String description;

    private String status;

    @NotNull
    private Long assignedTo;

    @NotNull
    private Long projectId;

    private LocalDate dueDate;
}