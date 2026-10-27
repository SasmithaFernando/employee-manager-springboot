package com.example.employeemanager.controller;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("employee", new Employee());
        return "employees";
    }

    @PostMapping("/employees")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (employee.getEmail() != null && !employee.getEmail().isBlank()
                && employeeService.isEmailTaken(employee.getEmail())) {
            bindingResult.rejectValue("email", "duplicate.email", "An employee with this email already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "employees";
        }

        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found with ID: " + id);
            return "redirect:/employees";
        }

        model.addAttribute("employee", employee.get());
        return "edit-employee";
    }

    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (employee.getEmail() != null && !employee.getEmail().isBlank()
                && employeeService.isEmailTaken(employee.getEmail(), id)) {
            bindingResult.rejectValue("email", "duplicate.email", "An employee with this email already exists");
        }

        if (bindingResult.hasErrors()) {
            employee.setId(id);
            return "edit-employee";
        }

        employee.setId(id);
        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployeeById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/employees";
    }
}
