package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.TaskToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskToUserRepository extends JpaRepository<TaskToUser, UUID> {

}
