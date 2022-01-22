package com.revature.dao;

import com.revature.console.Driver;
import com.revature.model.Employee;

import java.sql.*;

public class EmployeeDAO {

    public static Employee addEmployee(Employee employee){

        int employeeId;
        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO employee(username, password, isAdmin) VALUES (?,?,?) ",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setBoolean(3, employee.isAdmin());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employeeId = generatedKeys.getInt(1);
                    Driver.logger.trace("New employee added id: " + employeeId);
                    stmt.close();
                    employee.setId(employeeId);

                    return employee;
                }
                else {
                    c.rollback();
                    System.out.println("No id returned");
                    throw new SQLException("Account could not be created! No ID returned.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setEmployee(Employee employee){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE employee SET " +
                    " password = ? , isAdmin = ?" +
                    " WHERE username = ?");

            preparedStatement.setString(3, employee.getUsername());
            preparedStatement.setString(1, employee.getPassword());
            preparedStatement.setBoolean(2, employee.isAdmin());

            return preparedStatement.executeUpdate()!=0; //to convert int to boolean

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Employee findEmployeeByUsername(String username){


        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM employee WHERE username=? ;");

            preparedStatement.setString(1, username);


            ResultSet results =  preparedStatement.executeQuery(); //to convert int to boolean
            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                return null;
            }
            else {
                int id = results.getInt("id");
                String password = results.getString("password");
                boolean isAdmin = results.getBoolean("isAdmin");

                return new Employee(username, password, id, isAdmin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteEmployee(int employeeId){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("DELETE FROM employee WHERE id = ?;");

            preparedStatement.setInt(1, employeeId);

            return preparedStatement.executeUpdate()!= 0; //to convert int to boolean

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
