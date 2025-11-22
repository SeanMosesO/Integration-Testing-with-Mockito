import java.util.Optional;

// This interface simulates interaction with a database (the external resource)
public interface UserRepository {
    // Finds a user by ID
    Optional<User> findById(Long id);

    // Saves a user and returns the saved instance (often with an updated ID)
    User save(User user);

    // Finds a user by email
    Optional<User> findByEmail(String email);
}