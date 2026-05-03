package com.app.dto;

import lombok.*;

@Getter @Setter
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private Long createdBy;
}