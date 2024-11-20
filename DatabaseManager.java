import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection conn;

    // Method to connect to the SQLite database
    public void connect() {
        try {
            String url = "jdbc:sqlite:library.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite successfully.");
            initializeTables();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    // Initialize tables
    private void initializeTables() {
    	
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
               "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "firstname TEXT NOT NULL, " +
                "lastname TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "memberId TEXT UNIQUE DEFAULT NULL)";

        String createBooksTable = "CREATE TABLE IF NOT EXISTS Books (" +
                "isbn TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "isAvailable INTEGER NOT NULL DEFAULT 1)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createBooksTable);
            System.out.println("Tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing tables.");
            e.printStackTrace();
        }
    }
    	// Check for user name when making an account
    	public boolean isUsernameTaken(String username) {
    		String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
    	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, username);
    		ResultSet rs = pstmt.executeQuery();
    		return rs.next() && rs.getInt(1) > 0;
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    	
    // Add a user to the database
    public void addUser(User newUser) {
    	String hashedPassword = PasswordUtil.hashPassword(newUser.getPassword());
        String sql = "INSERT INTO Users(username, password, role, firstname, lastname, email, memberId) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, newUser.getUsername());                       // username
            pstmt.setString(2, PasswordUtil.hashPassword(newUser.getPassword()));  // hashed password
            pstmt.setString(3, newUser.getRole());                          // role
            pstmt.setString(4, newUser.getFirstName());                     // firstname
            pstmt.setString(5, newUser.getLastName());                      // lastname
            pstmt.setString(6, newUser.getEmail());                         // email
            pstmt.setString(7, newUser.getMemberId()); 
            pstmt.executeUpdate();
            System.out.println("User added: " + newUser.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Authenticate a user by username and password
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");  // Retrieve the hashed password from the database
                String enteredHashedPassword = PasswordUtil.hashPassword(password);  // Hash the entered password
                System.out.println("Stored hashed password: " + storedHashedPassword);
                System.out.println("Entered hashed password: " + enteredHashedPassword);
                if (storedHashedPassword.equals(enteredHashedPassword)) {  // Compare hashes
                    return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        storedHashedPassword,  // Store hashed password in the User object
                        rs.getString("role"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("email"),
                        rs.getString("memberId")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Return null if authentication fails
    }


    // Add a book to the database
    public void addBook(book newBook) {
        String sql = "INSERT INTO Books(isbn, title, author, isAvailable) VALUES(?, ?, ?, 1)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newBook.getIsbn());
            pstmt.setString(2, newBook.getTitle());
            pstmt.setString(3, newBook.getAuthor());
            pstmt.executeUpdate();
            System.out.println("Book added: " + newBook.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // Grab users borrowed books
    public List<book> getBorrowedBooks(String memberId) {
        List<book> books = new ArrayList<>();
        String sql = "SELECT Books.isbn, Books.title, Books.author FROM BorrowRecords " +
                     "JOIN Books ON BorrowRecords.isbn = Books.isbn WHERE BorrowRecords.memberId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new book(rs.getString("isbn"), rs.getString("title"), rs.getString("author")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
 // remove book for admins
    public void removeBook(String isbn) {
        String sql = "DELETE FROM Books WHERE isbn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
            System.out.println("Book removed: " + isbn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Retrieve all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("userId"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("email"),
                    rs.getString("memberId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Retrieve all books
    public List<book> getAllBooks() {
        List<book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
