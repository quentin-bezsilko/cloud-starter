package com.qbe.cloudstarter.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.qbe.cloudstarter.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class TaskRepositoryTest {

  @Autowired private TaskRepository taskRepository;

  @Test
  void shouldSaveAndFindTask() {
    TaskEntity task = new TaskEntity(null, "Learn Kubernetes", false);

    TaskEntity savedTask = taskRepository.save(task);
    assertNotNull(savedTask.getId());

    TaskEntity foundTask = taskRepository.findById(savedTask.getId()).orElseThrow();
    assertEquals("Learn Kubernetes", foundTask.getTitle());
    assertFalse(foundTask.isCompleted());
  }

  @Test
  void shouldDeleteTask() {
    TaskEntity task = taskRepository.save(new TaskEntity(null, "Learn Docker", false));
    taskRepository.deleteById(task.getId());
    assertFalse(taskRepository.findById(task.getId()).isPresent());
  }
}
