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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Random;

@Slf4j
@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;

    public JobConfiguration(JobRepository jobRepository, JobCompletionNotificationListener jobCompletionNotificationListener) {
        this.jobRepository = jobRepository;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
    }

    @Bean
    public Job longTransactionJob() {
        return new JobBuilder("LongTransactionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(longTransactionStep(null))
                .build();
    }

    @Bean
    public Step longTransactionStep(@Qualifier("transactionManager") PlatformTransactionManager txManager) {
        return new StepBuilder("LongTransactionStep", jobRepository)
                .tasklet(longTransactionTasklet(), txManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet longTransactionTasklet() {
        return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            Thread.sleep(20_000);
            log.info("Hello, {}!", name == null ? "Cloud Task" : name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job fragileJob() {
        return new JobBuilder("fragileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fragileStep(null))
                .listener(jobCompletionNotificationListener)
                .build();
    }

    @Bean
    public Step fragileStep(@Qualifier("transactionManager") PlatformTransactionManager txManager) {
        return new StepBuilder("fragileStep", jobRepository)
                .tasklet(fragileTasklet(), txManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet fragileTasklet() {
        return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            log.info("fragileTasklet: launched by {}", name==null? "DataFlow" : name);
            if (new Random().nextBoolean()) {
                return RepeatStatus.FINISHED;
            }
            throw new RuntimeException("This is an intended Error");
        };
    }

    @Bean
    public Job helloJob() {
        return new JobBuilder("helloJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(helloStep(null))
                .listener(jobCompletionNotificationListener)
                .build();
    }

    @Bean
    public Step helloStep(@Qualifier("transactionManager") PlatformTransactionManager txManager) {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet(helloTasklet(), txManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet helloTasklet() {
        return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            log.info("Hello, {}!", name == null ? "Cloud Task" : name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job goodbyeJob() {
        return new JobBuilder("goodbyeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(goodbyeStep(null))
                .listener(jobCompletionNotificationListener)
                .build();
    }

    @Bean
    public Step goodbyeStep(@Qualifier("transactionManager") PlatformTransactionManager txManager) {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet(goodbyeTasklet(), txManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet goodbyeTasklet() {
        return (contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            log.info("Goodbye, {}!", name == null ? "Cloud Task" : name);
            return RepeatStatus.FINISHED;
        };
    }

}