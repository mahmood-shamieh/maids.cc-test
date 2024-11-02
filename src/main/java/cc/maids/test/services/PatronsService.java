package cc.maids.test.services;

import cc.maids.test.dto.BookDTO;
import cc.maids.test.dto.PatronDTO;
import cc.maids.test.dto.SignInDTO;
import cc.maids.test.exceptions.FieldsValidationException;
import cc.maids.test.exceptions.NotFoundException;
import cc.maids.test.models.Book;
import cc.maids.test.models.Patron;
import cc.maids.test.repositories.PatronsRepo;
import cc.maids.test.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatronsService implements UserDetailsService {

    private final PatronsRepo patronsRepo;





//    @Autowired
//    private JWTUtil jwtUtil;

    @Autowired
    public PatronsService(PatronsRepo patronsRepo) {
        this.patronsRepo = patronsRepo;

    }

    @Cacheable(value = "patrons")
    public List<Patron> getAllPatrons() throws Exception {

        return patronsRepo.findAll();

    }

    @Cacheable(value = "patron", key = "#id")
    public Optional<Patron> getPatronById(Long id) throws Exception {

        return patronsRepo.findById(id);

    }

    @CacheEvict(value = "patrons", allEntries = true)
    public Patron createPatron(PatronDTO patronDto) throws Exception {
        boolean checkEmailExist = patronsRepo.existsByEmail(patronDto.getEmail());
        if (checkEmailExist) {
            throw new FieldsValidationException("Email is used before (Duplicate Entry)", Map.ofEntries(Map.entry("email", "Email is used before")));
        }
        Patron patron = Patron.builder()
                .name(patronDto.getName())
                .password(patronDto.getPassword())
                .email(patronDto.getEmail())
                .build();
        return patronsRepo.save(patron);


    }

    @CachePut(value = "patron", key = "#id")
    @CacheEvict(value = "patrons", allEntries = true)
    public Patron updatePatron(PatronDTO patronDTO, Long id) throws Exception {
        Optional<Patron> foundedPatron = patronsRepo.findById(id);
        if (foundedPatron.isPresent()) {
            Optional<Patron> emailExist = patronsRepo.findByEmail(patronDTO.getEmail());

            if (emailExist.isPresent() && emailExist.get().getId() != id)
                throw new FieldsValidationException("Email is used before (Duplicate Entry)",
                        Map.ofEntries(Map.entry("Email", "Email is used before")));

            Patron savedPatron = patronsRepo.save(Patron.builder()
                    .name(patronDTO.getName())
                    .password(patronDTO.getPassword())
                    .email(patronDTO.getEmail())
                    .createdAt(foundedPatron.get().getCreatedAt())
                    .id(id)
                    .build());
            return savedPatron;
        } else {
            throw new NotFoundException("Patron not found");
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "patrons", allEntries = true),
            @CacheEvict(value = "patron", key = "#id")
    })
    public void deletePatron(Long id) throws Exception {
        if (patronsRepo.existsById(id)) {
            patronsRepo.deleteById(id);
        } else {
            throw new NotFoundException("Patron not found");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Patron> patronCheck = patronsRepo.findByEmail(username);
        if (!patronCheck.isPresent()) {
            throw new UsernameNotFoundException("Wrong credentials");
        } else {

            return new User(patronCheck.get().getEmail(),patronCheck.get().getPassword() , new ArrayList<>());
        }
    }

//    public SignInDTO authenticate(SignInDTO signInDTO) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(signInDTO.getEmail(), signInDTO.getPassword()));
//        } catch (AuthenticationException e) {
//            System.out.println(e.getClass());
//            throw new Exception("Incorrect username or password", e);
//        }
//        final String jwt = jwtUtil.generateToken(signInDTO.getEmail());
//        signInDTO.setToken(jwt);
//        return signInDTO;
//    }
}
