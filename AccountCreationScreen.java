import javax.swing.*;
import java.awt.*;

public class AccountCreationScreen extends JFrame {
	private DatabaseManager dbManager;
	
	public AccountCreationScreen(DatabaseManager dbManager) {
		this.dbManager = dbManager;
		
		setTitle("Create Account");
		setSize(350, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		JTextField emailField = new JTextField(15);
		JTextField usernameField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		JTextField firstnameField = new JTextField(15);
		JTextField lastnameField = new JTextField(15);
		
		gbc.gridx = 0; gbc.gridy = 0;
		add(new JLabel("First Name:"), gbc);
		gbc.gridx = 1;
		add(firstnameField, gbc);
		
		gbc.gridx = 0; gbc.gridy = 1;
		add(new JLabel("Last Name:"), gbc);
		gbc.gridx = 1;
		add(lastnameField, gbc);
		
		gbc.gridx = 0; gbc.gridy = 2;
		add(new JLabel("Email:"), gbc);
		gbc.gridx = 1;
		add(emailField, gbc);
		
		gbc.gridx = 0; gbc.gridy = 3;
		add(new JLabel("Username:"), gbc);
		gbc.gridx = 1;
		add(usernameField, gbc);
		
		gbc.gridx = 0; gbc.gridy = 4;
		add(new JLabel("Password:"), gbc);
		gbc.gridx = 1;
		add(passwordField, gbc);
		
		JButton createAccountButton = new JButton("Create Account");
		
		gbc.gridx = 1; gbc.gridy = 5;
		add(createAccountButton, gbc);
		
		createAccountButton.addActionListener(e -> {
			String firstname = firstnameField.getText();
			String lastname = lastnameField.getText();
			String email = emailField.getText();
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			
			// Input validation
            if (!isValidName(firstname) || !isValidName(lastname)) {
                JOptionPane.showMessageDialog(this, "First and last names must contain only letters.");
                return;
            }
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format.");
                return;
            }
            if (!isValidUsername(username)) {
                JOptionPane.showMessageDialog(this, "Username must be alphanumeric and at least 4 characters.");
                return;
            }
            if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters, include a letter, and a number.");
                return;
            }
            if (dbManager.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Try a different one.");
                return;
            }
            
			if (dbManager.isUsernameTaken(username)) {
				JOptionPane.showMessageDialog(this, "Username already exists. Try a different one.");
			} else {
				User newUser = new User(
					    0,           // Placeholder for userId; it will be auto-generated in the database
					    username,    // username
					    password,    // plaintext password (hashed in addUser)
					    "member",    // role (default for new accounts)
					    firstname,   // firstname
					    lastname,    // lastname
					    email,       // email
					    null         // memberId (not assigned yet)
				);
					

				dbManager.addUser(newUser);
				JOptionPane.showMessageDialog(this, "Account created successfully!. You can now login.");
				dispose();
				new LoginScreen(dbManager);
			}
		});
				
				setLocationRelativeTo(null);
				setVisible(true);
	}
	
	// Validation methods
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[A-Za-z0-9]{4,}$");  // Alphanumeric, at least 4 characters
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*");
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+$");
    }
}
