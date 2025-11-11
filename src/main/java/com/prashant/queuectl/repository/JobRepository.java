package com.prashant.queuectl.repository;

import com.prashant.queuectl.entity.Job;
import com.prashant.queuectl.entity.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByState(State state);
}
