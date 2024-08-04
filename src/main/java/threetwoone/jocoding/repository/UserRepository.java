package threetwoone.jocoding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import threetwoone.jocoding.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
