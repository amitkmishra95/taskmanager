package com.app.controller;

import com.app.entity.*;
import com.app.repository.TaskRepository;
import com.app.service.TaskService;
import com.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    @PostMapping
    public Task createTask(@RequestBody Task task,
                           @RequestParam Long userId,
                           @RequestParam Long projectId ) {

        User user = userService.getById(userId);

//        if (user.getRole() != Role.ADMIN) {
//            throw new RuntimeException("Only Admin can assign tasks");
//        }

        return taskService.createTask(task, userId, projectId);
    }

//    @GetMapping
//    public List<Task> getTasks(@RequestParam Long projectId) {
//        return taskService.getTasksByProject(projectId);
//    }
    
    @GetMapping("/all")
    public List<Map<String, Object>> getTasks() {
        return taskRepository.findAll().stream().map(t -> {
            Map<String, Object> map = new HashMap<>();

            map.put("id", t.getId());
            map.put("title", t.getTitle());
            map.put("status", t.getStatus());
            map.put("dueDate", t.getDueDate());

            map.put("assignedTo", t.getAssignedTo() != null ?
                    t.getAssignedTo().getName() : null);

            map.put("project", t.getProject() != null ?
                    t.getProject().getTitle() : null);

            return map;
        }).toList();
    }
    @PutMapping("/{id}")
    public Task updateStatus(@PathVariable Long id,
                             @RequestParam String status,
                             @RequestParam Long userId) {

//        Long uid = taskService.
        return taskService.updateStatus(id, Status.valueOf(status));
    }
    @GetMapping("/user/{userId}")
    public List<Task> getUserTasks(@PathVariable Long userId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null &&
                        t.getAssignedTo().getId().equals(userId))
                .toList();
    }
    @GetMapping("/user/{userId}/stats")
    public Map<String, Object> getUserStats(@PathVariable Long userId) {

        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null &&
                        t.getAssignedTo().getId().equals(userId))
                .toList();

        long total = tasks.size();

        long completed = tasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .count();

        long inProgress = tasks.stream()
                .filter(t -> t.getStatus() == Status.IN_PROGRESS)
                .count();
        long todo = tasks.stream()
                .filter(t -> t.getStatus() == Status.TODO)
                .count();

        long overdue = tasks.stream()
                .filter(t ->
                        t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDate.now()) &&
                        t.getStatus() != Status.DONE)
                .count();

        Map<String, Object> map = new HashMap<>();
        map.put("totalTasks", total);
        map.put("completed", completed);
        map.put("inProgress", inProgress);
        map.put("todo", todo);
        map.put("overdue", overdue);

        return map;
    }
}