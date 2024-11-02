package cc.maids.test.repositories;

import cc.maids.test.models.Book;
import cc.maids.test.models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatronsRepo extends JpaRepository<Patron, Long> {

    boolean existsByEmail(String email);
    Optional<Patron> findByEmail(String email);
}
