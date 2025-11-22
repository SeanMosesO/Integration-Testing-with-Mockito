import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    // 1. Mocking the Service layer: This is the dependency of the Controller.
    @Mock
    private UserService userService;

    // 2. Injecting the mock(s) into the Controller (Class Under Test).
    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Initializes @Mock and @InjectMocks
        MockitoAnnotations.openMocks(this);
        // Test data setup for reuse
        mockUser = new User(1L, "control_test", "control@example.com");
    }

    // --- Test Case 1: GET - Successful retrieval (HTTP 200 OK) ---
    @Test
    void getUser_Exists_ReturnsOkAndUser() {
        Long userId = 1L;
        
        // Mock Behavior Setup: Tell the mock service what to return.
        // We ensure that when the Controller calls the Service, the mock returns the mockUser.
        when(userService.getUserById(userId)).thenReturn(mockUser);

        // Execution: Call the controller method.
        ResponseEntity<User> response = userController.getUser(userId);

        // Verification (Assertions): Check the HTTP status and body content.
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status must be 200 OK");
        assertEquals(userId, response.getBody().getId(), "The returned user ID should match the request");

        // Verification (Mock Interaction): Ensure the controller called the service exactly once.
        verify(userService, times(1)).getUserById(userId);
    }

    // --- Test Case 2: GET - User not found (HTTP 404 NOT FOUND) ---
    @Test
    void getUser_DoesNotExist_ReturnsNotFound() {
        Long userId = 99L;

        // Mock Behavior Setup: Simulate the service throwing the 'not found' exception.
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        // Execution: Call the controller method.
        ResponseEntity<User> response = userController.getUser(userId);

        // Verification (Assertions): Check the HTTP status.
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP status must be 404 NOT FOUND");
        assertNull(response.getBody(), "Response body should be empty for 404");

        // Verification (Mock Interaction): Ensure the service was called.
        verify(userService, times(1)).getUserById(userId);
    }

    // --- Test Case 3: POST - Successful creation (HTTP 201 CREATED) ---
    @Test
    void createUser_ValidData_ReturnsCreatedAndUser() {
        User newUserInput = new User(null, "new_control", "control_new@test.com");
        User createdUser = new User(5L, "new_control", "control_new@test.com"); 

        // Mock Behavior Setup: Define what the service returns upon successful creation.
        when(userService.createNewUser(newUserInput)).thenReturn(createdUser);

        // Execution: Call the controller method.
        ResponseEntity<?> response = userController.createUser(newUserInput);

        // Verification (Assertions): Check the HTTP status and returned object.
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status must be 201 CREATED");
        assertEquals(createdUser, response.getBody(), "Response body should contain the created user object");

        // Verification (Mock Interaction): Ensure the service's create method was called once.
        verify(userService, times(1)).createNewUser(newUserInput);
    }

    // --- Test Case 4: POST - Invalid data (HTTP 400 BAD REQUEST) ---
    @Test
    void createUser_DuplicateEmail_ReturnsBadRequest() {
        User duplicateUser = new User(null, "dup", "existing@test.com");
        String errorMessage = "Email address is already in use.";

        // Mock Behavior Setup: Simulate the service throwing the validation exception.
        when(userService.createNewUser(duplicateUser)).thenThrow(new IllegalArgumentException(errorMessage));

        // Execution: Call the controller method.
        ResponseEntity<?> response = userController.createUser(duplicateUser);

        // Verification (Assertions): Check the HTTP status and error message.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "HTTP status must be 400 BAD REQUEST");
        assertEquals(errorMessage, response.getBody(), "Response body should contain the validation error message");

        // Verification (Mock Interaction): Ensure the service was called.
        verify(userService, times(1)).createNewUser(duplicateUser);
    }
}