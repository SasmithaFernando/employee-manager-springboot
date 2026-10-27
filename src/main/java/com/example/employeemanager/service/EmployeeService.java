package com.example.employeemanager.service;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public boolean isEmailTaken(String email, Long currentEmployeeId) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(email);
        if (existingEmployee.isEmpty()) {
            return false;
        }
        if (currentEmployeeId == null) {
            return true;
        }
        return !existingEmployee.get().getId().equals(currentEmployeeId);
    }

    public boolean isEmailTaken(String email) {
        return isEmailTaken(email, null);
    }

    public void deleteEmployeeById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
