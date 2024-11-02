package cc.maids.test.controllers;
import cc.maids.test.dto.PatronDTO;
import cc.maids.test.dto.ResponseDTO;
import cc.maids.test.dto.SignInDTO;
import cc.maids.test.exceptions.FieldsValidationException;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Patron;
import cc.maids.test.services.PatronsService;
import cc.maids.test.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class PatronsControllerTest {
    @InjectMocks
    private PatronsController patronsController;

    @Mock
    private PatronsService patronsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateSuccess() {
        SignInDTO signInDTO = SignInDTO.builder().email("test@example.com").password("password").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtUtil.generateToken(signInDTO.getEmail())).thenReturn("mockJwtToken");

        ResponseEntity<?> response = patronsController.authenticate(signInDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof SignInDTO);
        assertEquals("mockJwtToken", ((SignInDTO) response.getBody()).getToken());
    }

    @Test
    void testAuthenticateFailure() {
        SignInDTO signInDTO = SignInDTO.builder().email("test@example.com").password("wrongPassword").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = patronsController.authenticate(signInDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDTO);
        assertEquals("Invalid credentials", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetAllPatrons() throws Exception {
        Patron patron = Patron.builder().id(1L).name("John Doe").email("john@example.com").build();
        when(patronsService.getAllPatrons()).thenReturn(Collections.singletonList(patron));

        ResponseEntity<?> response = patronsController.getAllPatrons();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ResponseDTO);
    }

    @Test
    void testGetPatronByIdFound() throws Exception {
        Patron patron = Patron.builder().id(1L).name("John Doe").email("john@example.com").build();
        when(patronsService.getPatronById(1L)).thenReturn(Optional.of(patron));

        ResponseEntity<?> response = patronsController.getPatronById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PatronDTO);
    }

    @Test
    void testGetPatronByIdNotFound() throws Exception {
        when(patronsService.getPatronById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = patronsController.getPatronById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testCreatePatronSuccess() throws Exception {
        PatronDTO patronDTO = PatronDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password")
                .build();
        Patron savedPatron = Patron.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();
        when(patronsService.createPatron(any(PatronDTO.class))).thenReturn(savedPatron);
        ResponseEntity<?> response = patronsController.createPatron(patronDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof PatronDTO);
    }

    @Test
    void testCreatePatronValidationFailure() throws Exception {
        PatronDTO patronDTO = PatronDTO.builder().name("John Doe").email("invalidEmail").password("password").build();
        when(patronsService.createPatron(any(PatronDTO.class))).thenThrow(new FieldsValidationException("Validation Error", null));

        ResponseEntity<?> response = patronsController.createPatron(patronDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDTO);
    }

    @Test
    void testUpdatePatronSuccess() throws Exception {
        PatronDTO patronDTO = PatronDTO.builder().name("John Doe Updated").email("johnupdated@example.com").build();
        Patron updatedPatron = Patron.builder().id(1L).name("John Doe Updated").email("johnupdated@example.com").build();
        when(patronsService.updatePatron(any(PatronDTO.class), eq(1L))).thenReturn(updatedPatron);

        ResponseEntity<?> response = patronsController.updatePatron(patronDTO, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDTO);
    }

    @Test
    void testDeletePatronSuccess() throws Exception {
        doNothing().when(patronsService).deletePatron(1L);

        ResponseEntity<?> response = patronsController.deletePatron(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDTO);
    }

    @Test
    void testDeletePatronNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(patronsService).deletePatron(1L);

        ResponseEntity<?> response = patronsController.deletePatron(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
