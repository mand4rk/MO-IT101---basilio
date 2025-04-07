
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class EmployeeFileHandlingGUI extends JFrame {
    private static final String FILE_NAME = "employees.txt";

    private JTextField idField, nameField, salaryField;
    private JTextArea displayArea;

    public EmployeeFileHandlingGUI() {
        setTitle("Employee Record System");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Employee ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Employee Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        inputPanel.add(salaryField);

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> addEmployee());
        inputPanel.add(addButton);

        JButton viewButton = new JButton("View All");
        viewButton.addActionListener(e -> viewEmployees());
        inputPanel.add(viewButton);

        add(inputPanel, BorderLayout.NORTH);

        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Bottom panel for update/delete/search
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));

        JButton updateButton = new JButton("Update Salary");
        updateButton.addActionListener(e -> updateSalary());
        bottomPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(e -> deleteEmployee());
        bottomPanel.add(deleteButton);

        JButton searchButton = new JButton("Search Employee");
        searchButton.addActionListener(e -> searchEmployee());
        bottomPanel.add(searchButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addEmployee() {
        String id = idField.getText();
        String name = nameField.getText();
        String salary = salaryField.getText();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + name + "," + salary);
            writer.newLine();
            displayArea.setText("Employee added successfully.");
        } catch (IOException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void viewEmployees() {
        displayArea.setText("");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                displayArea.append(line + "\n");
            }
        } catch (IOException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void updateSalary() {
        String id = idField.getText();
        String newSalary = salaryField.getText();

        File inputFile = new File(FILE_NAME);
        File tempFile = new File("temp.txt");

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(id)) {
                    writer.write(data[0] + "," + data[1] + "," + newSalary);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            displayArea.setText("Error updating salary: " + e.getMessage());
            return;
        }

        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            displayArea.setText(updated ? "Salary updated successfully." : "Employee not found.");
        } else {
            displayArea.setText("Error replacing the file.");
        }
    }

    private void deleteEmployee() {
        String id = idField.getText();

        File inputFile = new File(FILE_NAME);
        File tempFile = new File("temp.txt");

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[0].equals(id)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            displayArea.setText("Error deleting employee: " + e.getMessage());
            return;
        }

        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            displayArea.setText(deleted ? "Employee deleted successfully." : "Employee not found.");
        } else {
            displayArea.setText("Error replacing the file.");
        }
    }

    private void searchEmployee() {
        String id = idField.getText();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(id)) {
                    displayArea.setText("Found: " + line);
                    found = true;
                    break;
                }
            }
            if (!found) {
                displayArea.setText("Employee not found.");
            }
        } catch (IOException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeFileHandlingGUI::new);
    }
}
