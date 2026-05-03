//package com.app.controller;
//
//import com.app.entity.Task;
//import com.app.repository.TaskRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping("/api/dashboard")
//public class DashboardController {
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//    @GetMapping
//    public Map<String, Object> getStats() {
//
//        List<Task> tasks = taskRepository.findAll();
//
//        long total = tasks.size();
//        long completed = tasks.stream().filter(t -> t.getStatus().name().equals("DONE")).count();
//        long inProgress = tasks.stream().filter(t -> t.getStatus().name().equals("IN_PROGRESS")).count();
//        long overdue = tasks.stream()
//                .filter(t -> t.getDueDate() != null &&
//                        t.getDueDate().isBefore(LocalDate.now()) &&
//                        !t.getStatus().name().equals("DONE"))
//                .count();
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("totalTasks", total);
//        map.put("completed", completed);
//        map.put("inProgress", inProgress);
//        map.put("overdue", overdue);
//
//        return map;
//    }
//}


package com.app.controller;

import com.app.entity.Status;
import com.app.entity.Task;
import com.app.repository.ProjectRepository;
import com.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private  ProjectRepository prepo;

    @GetMapping
    public Map<String, Object> getDashboardStats() {

        List<Task> tasks = taskRepository.findAll();

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
                        t.getStatus() != Status.DONE
                )
                .count();
        long projects = prepo.findAll().stream().count();
        Map<String, Object> map = new HashMap<>();
        map.put("totalTasks", total);
        map.put("completed", completed);
        map.put("inProgress", inProgress);
        map.put("toDo", todo);
        map.put("overdue", overdue);
        map.put("projects", projects);

        return map;
    }
}