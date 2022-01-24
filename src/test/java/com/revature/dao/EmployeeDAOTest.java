package com.revature.dao;

import com.revature.model.Employee;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeDAOTest {

    @Test
    public void addAndDeleteTest() {
        Employee added= EmployeeDAO.addEmployee(new Employee("UmitTheCoder", "123", -1, false ));
        assertEquals("UmitTheCoder", added.getUsername());
        assertTrue(EmployeeDAO.deleteEmployee(added.getId()));
    }

    @Test
    public void setEmployeeTest() {
        assertTrue(EmployeeDAO.setEmployee(
                new Employee("employee", "123", 3, false )));
    }

    @Test
    public void findEmployeeByUsernameTest() {

        assertEquals("admin", EmployeeDAO.findEmployeeByUsername("admin").getUsername());
    }

}