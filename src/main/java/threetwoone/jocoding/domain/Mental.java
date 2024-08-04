// File: src/main/java/threetwoone/jocoding/domain/Mental.java

package threetwoone.jocoding.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Mental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mentalId;

    private int stability;
    private int positivity;
    private int negativity;
    private int excitement;

    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}
