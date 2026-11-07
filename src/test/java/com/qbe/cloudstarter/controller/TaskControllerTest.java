package com.qbe.cloudstarter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.qbe.cloudstarter.entity.TaskEntity;
import com.qbe.cloudstarter.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TaskRepository taskRepository;

  // CREATE
  @Test
  void shouldCreateTask() throws Exception {
    TaskEntity task = new TaskEntity(1L, "Learn Kubernetes", false);

    when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

    mockMvc
        .perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {
                                    "title": "Learn Kubernetes",
                                    "completed": false
                                }
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Learn Kubernetes"))
        .andExpect(jsonPath("$.completed").value(false));

    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  // READ ALL
  @Test
  void shouldGetAllTasks() throws Exception {
    List<TaskEntity> tasks =
        List.of(
            new TaskEntity(1L, "Learn Docker", false),
            new TaskEntity(2L, "Learn Kubernetes", true));

    when(taskRepository.findAll()).thenReturn(tasks);

    mockMvc
        .perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].title").value("Learn Docker"))
        .andExpect(jsonPath("$[1].completed").value(true));

    verify(taskRepository).findAll();
  }

  // READ ONE
  @Test
  void shouldGetTaskById() throws Exception {

    TaskEntity task = new TaskEntity(1L, "Learn Spring Boot", false);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    mockMvc
        .perform(get("/api/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Learn Spring Boot"));

    verify(taskRepository).findById(1L);
  }

  // UPDATE
  @Test
  void shouldUpdateTask() throws Exception {

    TaskEntity existingTask = new TaskEntity(1L, "Learn Docker", false);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

    when(taskRepository.save(any(TaskEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc
        .perform(
            put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                {
                                    "title": "Learn Kubernetes",
                                    "completed": true
                                }
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Learn Kubernetes"))
        .andExpect(jsonPath("$.completed").value(true));

    verify(taskRepository).findById(1L);
    verify(taskRepository).save(any(TaskEntity.class));
  }

  // DELETE
  @Test
  void shouldDeleteTask() throws Exception {
    mockMvc.perform(delete("/api/tasks/1")).andExpect(status().isOk());
    verify(taskRepository, times(1)).deleteById(1L);
  }
}
