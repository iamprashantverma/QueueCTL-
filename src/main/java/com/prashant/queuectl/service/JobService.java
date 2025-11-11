package com.prashant.queuectl.service;

import com.prashant.queuectl.dto.JobRequestDTO;
import com.prashant.queuectl.dto.JobResponseDTO;
import com.prashant.queuectl.entity.Job;
import com.prashant.queuectl.entity.enums.State;
import com.prashant.queuectl.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public JobResponseDTO enqueueJob(JobRequestDTO jobRequestDTO) {

        Job job = mapToEntity(jobRequestDTO);

        Job savedJob = jobRepository.save(job);
        log.info("Enqueued new job with ID: {}", savedJob.getId());

        return mapToResponseDTO(savedJob);
    }

    public List<JobResponseDTO> jobsByState(State state) {
        List<Job> jobs = jobRepository.findByState(state);

        return jobs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JobResponseDTO> allJobs() {
        List<Job> jobList = jobRepository.findAll();

        return jobList.stream()
                .map(job -> modelMapper.map(job, JobResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveJob(Job job) {
        jobRepository.save(job);
    }

    private Job mapToEntity(JobRequestDTO jobRequestDTO) {
        return modelMapper.map(jobRequestDTO, Job.class);
    }

    private JobResponseDTO mapToResponseDTO(Job job) {
        return modelMapper.map(job, JobResponseDTO.class);
    }


    @Transactional
    public JobResponseDTO retryFromDlq(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getState() != State.DEAD) {
            throw new RuntimeException("Job is not in DLQ");
        }

        job.setState(State.PENDING);
        job.setAttempts(0);
        Job savedJob = jobRepository.save(job);
        return mapToResponseDTO(savedJob);
    }

}
