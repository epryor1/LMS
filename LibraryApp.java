import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class LibraryApp extends JFrame {
    private DatabaseManager dbManager;
    private User currentUser;
    private DefaultTableModel bookTableModel;
    
    public LibraryApp(DatabaseManager dbManager, User currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;
        
        // Set up the main frame
        setTitle("Library Management System - " + currentUser.getFirstName());
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the table models
        bookTableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN"}, 0);
        
        // Set up JTabbedPane for Books and Borrowing History
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Catalog", createCatalogPanel());

        // Only admins should have access to the Borrowing History
        if ("admin".equals(currentUser.getRole())) {
            tabbedPane.addTab("User Management", createUserManagementPanel());
            tabbedPane.addTab("Admin Controls", createAdminControlsPanel());
        } else {
            tabbedPane.addTab("My Borrowed Books", createBorrowedBooksPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
        loadBooks();
        
        setVisible(true);
    }

    
    // Catalog Panel
    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Library Catalog"));

        // Table for displaying catalog
        DefaultTableModel catalogTableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN"}, 0);
        JTable catalogTable = new JTable(catalogTableModel);
        JScrollPane scrollPane = new JScrollPane(catalogTable);

        // Load books into the table
        List<book> books = dbManager.getAllBooks();
        for (book book : books) {
            catalogTableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), book.getIsbn()});
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    // Borrowed books panel
    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Borrowed Books"));

        // Table for displaying borrowed books
        DefaultTableModel borrowedTableModel = new DefaultTableModel(new String[]{"Title", "Author", "Borrow Date", "Return Date"}, 0);
        JTable borrowedTable = new JTable(borrowedTableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedTable);

        // Load borrowed books for the current user
        List<book> borrowedBooks = dbManager.getBorrowedBooks(currentUser.getMemberId());
        for (book book : borrowedBooks) {
            borrowedTableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), "2024-01-01", "2024-01-15"});  // Example dates
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // User management panel
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("User Management"));

        // Table for displaying user details
        DefaultTableModel userTableModel = new DefaultTableModel(new String[]{"Name", "Username", "Role", "Email"}, 0);
        JTable userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Load all users into the table
        List<User> users = dbManager.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{user.getFirstName(), user.getUsername(), user.getRole(), user.getMemberId()});
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Admin controls panel
    private JPanel createAdminControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Admin Controls"));

        // Input fields for adding a book
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField titleField = new JTextField(15);
        JTextField authorField = new JTextField(15);
        JTextField isbnField = new JTextField(15);
        JButton addBookButton = new JButton("Add Book");
        JButton removeBookButton = new JButton("Remove Book");

        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        // Author field
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(authorField, gbc);

        // ISBN field
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(isbnField, gbc);

        // Buttons
        gbc.gridx = 1; gbc.gridy = 3;
        inputPanel.add(addBookButton, gbc);
        gbc.gridx = 2;
        inputPanel.add(removeBookButton, gbc);

        // Table for displaying all books
        DefaultTableModel adminBookTableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN"}, 0);
        JTable adminBookTable = new JTable(adminBookTableModel);
        JScrollPane scrollPane = new JScrollPane(adminBookTable);

        // Load books into the admin table
        List<book> books = dbManager.getAllBooks();
        for (book book : books) {
            adminBookTableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), book.getIsbn()});
        }

        // Add book action
        addBookButton.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();

            book newBook = new book(isbn, title, author);
            dbManager.addBook(newBook);
            adminBookTableModel.addRow(new Object[]{title, author, isbn});

            // Clear input fields
            titleField.setText("");
            authorField.setText("");
            isbnField.setText("");
            JOptionPane.showMessageDialog(this, "Book added successfully.");
        });

        // Remove book action
        removeBookButton.addActionListener(e -> {
            int selectedRow = adminBookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String isbn = (String) adminBookTableModel.getValueAt(selectedRow, 2);
                dbManager.removeBook(isbn);
                adminBookTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Book removed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to remove.");
            }
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    

    

    // Method to load books into the GUI
    private void loadBooks() {
        bookTableModel.setRowCount(0);  // Clear existing rows
        List<book> books = dbManager.getAllBooks();

        for (book book : books) {
            bookTableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), book.getIsbn()});
        }
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        // Simulate a login with a User (replace with actual login functionality later)
        User testUser = new User(1, "admin", "admin", "admin", "Admin", "User", "libAdmin@libAdmin.com", "M001");  // Sample user
        new LibraryApp(dbManager, testUser);  // Start the app with the test user
    }
}
