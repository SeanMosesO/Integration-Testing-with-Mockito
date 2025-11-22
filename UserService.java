import java.util.Optional;

// The Service layer contains the core business logic.
// It depends on the UserRepository to fetch/save data.
public class UserService {

    private final UserRepository userRepository;

    // Dependency Injection via constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by ID.
     * @param id The user ID.
     * @return The User object if found.
     * @throws RuntimeException if the user is not found.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * Saves a new user, ensuring the email is unique before saving.
     * @param user The user object to save.
     * @return The saved User object.
     * @throws IllegalArgumentException if the email already exists.
     */
    public User createNewUser(User user) {
        // 1. Check for existing email (Mocked resource call)
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email address is already in use.");
        }

        // 2. Save the new user (Mocked resource call)
        User savedUser = userRepository.save(user);

        // Simulate a small bit of business logic by setting a default ID if needed (though usually done by repo)
        if (savedUser.getId() == null) {
            savedUser.setId(99L); // Mocked behavior often returns the same object or a modified one
        }

        return savedUser;
    }
}