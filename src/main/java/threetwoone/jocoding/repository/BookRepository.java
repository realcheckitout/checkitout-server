package threetwoone.jocoding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import threetwoone.jocoding.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
