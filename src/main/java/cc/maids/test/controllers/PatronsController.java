package cc.maids.test.controllers;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.dto.PatronDTO;
import cc.maids.test.dto.ResponseDTO;
import cc.maids.test.dto.SignInDTO;
import cc.maids.test.exceptions.FieldsValidationException;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.models.Patron;
import cc.maids.test.services.PatronsService;
import cc.maids.test.utils.JWTUtil;
import cc.maids.test.validation.groups.BookValidation1;
import cc.maids.test.validation.groups.PatronsValidation1;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patrons")
public class PatronsController {
    @Autowired
    PatronsService patronsService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@RequestBody SignInDTO signInDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInDTO.getEmail(), signInDTO.getPassword()));
            final String jwt = jwtUtil.generateToken(signInDTO.getEmail());
            signInDTO.setToken(jwt);
            return ResponseEntity.status(200).body(signInDTO);
        } catch (AuthenticationException e) {

            return ResponseEntity.status(403).body(ResponseDTO.forbiddenResponse(e.getMessage()));
        } catch (Exception e) {

            return ResponseEntity.status(500).body(ResponseDTO.badResponse("unknown error happened"));
        }
    }

    @GetMapping
    public ResponseEntity getAllPatrons() {
        try {
            return ResponseEntity.ok(ResponseDTO.successResponse(patronsService.getAllPatrons().stream().map(e -> PatronDTO.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .password(e.getPassword())
                    .email(e.getEmail())
                    .createdAt(e.getCreatedAt())
                    .lastDateModified(e.getLastDateModified()).build()).collect(Collectors.toList())));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getPatronById(@PathVariable Long id) {
        try {
            Optional<Patron> patron = patronsService.getPatronById(id);
            if (patron.isPresent()) {
                Patron foundedPatron = patron.get();
                return ResponseEntity.ok(PatronDTO.builder()
                        .id(foundedPatron.getId())
                        .name(foundedPatron.getName())
                        .password(foundedPatron.getPassword())
                        .email(foundedPatron.getEmail())
                        .lastDateModified(foundedPatron.getLastDateModified())
                        .createdAt(foundedPatron.getCreatedAt())
                        .build());
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse("unknown error happened"));
        }
    }


    @PostMapping()
    public ResponseEntity createPatron(@RequestBody PatronDTO patronDTO) {
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<PatronDTO>> violations = validator.validate(patronDTO, PatronsValidation1.class);
            if (violations != null && !violations.isEmpty()) {
                Map errors = violations.stream()
                        .map(violation ->
                                Map.entry(violation.getPropertyPath(), violation.getMessage())
                        ).collect(Collectors.toMap(
                                        element -> element.getKey(),
                                        element1 -> element1.getValue()
                                )
                        );
                throw new FieldsValidationException("Fields validation errors", errors);
            }
            patronDTO.setPassword(passwordEncoder.encode(patronDTO.getPassword()));
            Patron patron = patronsService.createPatron(patronDTO);
            return ResponseEntity.ok(PatronDTO.builder()
                    .name(patron.getName())
                    .email(patron.getEmail())
                    .password(patron.getPassword())
                    .id(patron.getId())
                    .createdAt(patron.getCreatedAt())
                    .lastDateModified(patron.getLastDateModified())
                    .build());
        } catch (FieldsValidationException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePatron(@RequestBody PatronDTO patronDTO, @PathVariable Long id) {
        try {
            Patron patron = patronsService.updatePatron(patronDTO, id);
            return ResponseEntity.ok().body(ResponseDTO.successResponse(PatronDTO.builder()
                    .email(patron.getEmail())
                    .name(patron.getName())
                    .password(patron.getPassword())
                    .id(patron.getId())
                    .createdAt(patron.getCreatedAt())
                    .lastDateModified(patron.getLastDateModified())
                    .build()));
        } catch (FieldsValidationException e) {
            return ResponseEntity.status(500).body(ResponseDTO.badResponse("Fields validation errors", e.getErrors()));
        } catch (NotFoundException e) {
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {


            return ResponseEntity.status(500).body(ResponseDTO.badResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePatron(@PathVariable Long id) {
        try {
            patronsService.deletePatron(id);
            return ResponseEntity.ok().body(ResponseDTO.successResponse("Element deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }


    }
}
