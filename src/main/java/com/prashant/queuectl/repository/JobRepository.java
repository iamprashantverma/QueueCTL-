package com.prashant.queuectl.repository;

import com.prashant.queuectl.entity.Job;
import com.prashant.queuectl.entity.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM jobs j WHERE j.state = :state ORDER BY j.createdAt ASC")
    List<Job> findPendingJobsForUpdate(@Param("state") State state);

    List<Job> findByState(State state);

    long countByState(State state);

    @Query("SELECT j FROM jobs j WHERE j.state = :state ORDER BY j.updatedAt DESC")
    List<Job> findByStateOrderByUpdatedAtDesc(@Param("state") State state);
}
