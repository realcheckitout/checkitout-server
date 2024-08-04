package threetwoone.jocoding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import threetwoone.jocoding.domain.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "book")
    private List<Content> contents;
}
