package com.fnf.cloudtaskapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@EnableTask
@SpringBootApplication
public class CloudTaskAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudTaskAppApplication.class, args);
	}

	@Bean
	public Job job(JobRepository jobRepository) {
		return new JobBuilder("SayHelloJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(helloWorldStep(null, null))
				.build();
	}

	@Bean
	public Step helloWorldStep(
			JobRepository jobRepository,
			@Qualifier("transactionManager") PlatformTransactionManager txManager) {

		return new StepBuilder("SayHelloStep", jobRepository)
				.tasklet(helloWorldTasklet(), txManager)
				.build();
	}

	@Bean
	@StepScope
	public Tasklet helloWorldTasklet() {

		return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            log.info("Hello, {}!", name == null? "Cloud Task" : name);
            return RepeatStatus.FINISHED;
        };
	}
}
