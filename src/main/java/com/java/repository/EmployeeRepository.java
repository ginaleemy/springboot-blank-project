package com.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.entity.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
   
}
