package com.sathish.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sathish.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Serializable> {

}
