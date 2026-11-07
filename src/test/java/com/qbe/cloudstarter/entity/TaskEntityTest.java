package com.qbe.cloudstarter.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaskEntityTest {

  @Test
  void shouldCreateTask() {
    TaskEntity task = new TaskEntity(null, "Learn Kubernetes", false);
    assertNull(task.getId());
    assertEquals("Learn Kubernetes", task.getTitle());
    assertFalse(task.isCompleted());
  }

  @Test
  void shouldUpdateTask() {
    TaskEntity task = new TaskEntity(null, "Learn Docker", false);
    task.setTitle("Learn Kubernetes");
    task.setCompleted(true);

    assertEquals("Learn Kubernetes", task.getTitle());
    assertTrue(task.isCompleted());
  }

  @Test
  void shouldCreateEmptyTask() {
    TaskEntity task = new TaskEntity();

    assertNull(task.getId());
    assertNull(task.getTitle());
    assertFalse(task.isCompleted());
  }
}
