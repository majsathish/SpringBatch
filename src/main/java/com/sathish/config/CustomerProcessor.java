package com.sathish.config;

import org.springframework.batch.item.ItemProcessor;

import com.sathish.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		// logic

		return item;
	}

}
