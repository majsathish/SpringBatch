package com.sathish.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.sathish.entity.Customer;
import com.sathish.repo.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {


	private CustomerRepository customerRepository;


	@Bean
	public FlatFileItemReader<Customer> customerReader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	public CustomerProcessor customerProcessor() {
		return new CustomerProcessor();
	}

	@Bean
	public RepositoryItemWriter<Customer> customerWriter() {

		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");

		return writer;
	}

	@Bean
	@Autowired
	public Job importUserJob(JobRepository jobRepository, Step step1) {
		return new JobBuilder("importUserJob", jobRepository)
				.flow(step1)
				.end()
				.build();


	}


	@Bean
	@Autowired
	public Step step1(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
		return new StepBuilder("step1", jobRepository)
				.<Customer, Customer> chunk(10,transactionManager)
				.reader(customerReader())
				.processor(customerProcessor())
				.writer(customerWriter())
				.build();
	}

//	@Bean
//	public Step step() {
//		return stepBuilderFactory.get("step-1").<Customer, Customer>chunk(10)
//						  .reader(customerReader())
//						  .processor(customerProcessor())
//						  .writer(customerWriter())
//						  .taskExecutor(taskExecutor())
//						  .build();
//	}
//
//	@Bean
//	public Job job() {
//		return jobBuilderFactory.get("customers-import")
//								.flow(step())
//								.end()
//								.build();
//	}
	
//	@Bean
//	public TaskExecutor taskExecutor() {
//		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
//		taskExecutor.setConcurrencyLimit(10);
//		return taskExecutor;
//	}
	
	
}







