package threetwoone.jocoding.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    private Long chapterNumber;
    private String contentText;
    private String choiceText;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
