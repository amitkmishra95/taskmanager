package com.app.controller;

import com.app.entity.Project;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.repository.ProjectRepository;
import com.app.service.ProjectService;
import com.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectrepo;
    @Autowired
    private UserService userService;
    @PostMapping
    public Project createProject(@RequestBody Project project,
                                 @RequestParam Long userId) {

        User user = userService.getById(userId);

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only Admin can create project");
        }

        return projectService.createProject(project, userId);
    }
    @GetMapping("/all")
    public List<Map<String, Object>> func(){
        return projectrepo.findAll().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            return map;
        }).toList();
    }
    @GetMapping("{id}")
    public Optional<Project> func(Long id){
    	return projectrepo.findById(id);//.stream().toList();
    }
    
}