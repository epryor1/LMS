import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
	private DatabaseManager dbManager;
	
	public LoginScreen(DatabaseManager dbManager) {
		this.dbManager = dbManager;
		
		setTitle("Login");
		setSize(300,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		JTextField usernameField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		
		gbc.gridx = 0; gbc.gridy = 0;
		add(new JLabel("Username:"), gbc);
		gbc.gridx = 1;
		add(usernameField, gbc);
		
		gbc.gridx = 0; gbc.gridy = 1;
		add(new JLabel("Password:"), gbc);
		gbc.gridx = 1;
		add(passwordField, gbc);
		
		JButton loginButton = new JButton("Login");
		JButton createAccountButton = new JButton("Create Account");
		
		gbc.gridx = 0; gbc.gridy = 2;
		add(loginButton, gbc);
		gbc.gridx = 1;
		add(createAccountButton, gbc);
		
		
	loginButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			User user = dbManager.authenticateUser(username, password);
			
			if (user != null) {
				JOptionPane.showMessageDialog(null, "Welcome, " + user.getFirstName() + "!");
				dispose();
				new LibraryApp(dbManager, user);
			} else {
				JOptionPane.showMessageDialog(null, "Invalid username or password. Try Again.");
			}
		}
	});
	
	createAccountButton.addActionListener(e -> {
		dispose(); // close login screen
		new AccountCreationScreen(dbManager); // Open account creation screen
	});
	setLocationRelativeTo(null);
	setVisible(true);
	}
	
	public static void main(String[] args) {
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.connect();
		new LoginScreen(dbManager);
	}
}
