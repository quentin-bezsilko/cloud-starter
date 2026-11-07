package com.qbe.cloudstarter.repository;

import com.qbe.cloudstarter.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {}
