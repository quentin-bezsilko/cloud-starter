package com.qbe.cloudstarter.controller;

import com.qbe.cloudstarter.entity.TaskEntity;
import com.qbe.cloudstarter.repository.TaskRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskRepository taskRepository;

  public TaskController(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @PostMapping
  public TaskEntity createTask(@RequestBody TaskEntity task) {
    return taskRepository.save(task);
  }

  @GetMapping
  public List<TaskEntity> getAllTasks() {
    return taskRepository.findAll();
  }

  @GetMapping("/{id}")
  public TaskEntity getTask(@PathVariable Long id) {
    return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
  }

  @PutMapping("/{id}")
  public TaskEntity updateTask(@PathVariable Long id, @RequestBody TaskEntity updatedTask) {
    TaskEntity task =
        taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    task.setTitle(updatedTask.getTitle());
    task.setCompleted(updatedTask.isCompleted());
    return taskRepository.save(task);
  }

  @DeleteMapping("/{id}")
  public void deleteTask(@PathVariable Long id) {
    taskRepository.deleteById(id);
  }
}
