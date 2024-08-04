package threetwoone.jocoding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import threetwoone.jocoding.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
