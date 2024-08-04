// File: src/main/java/threetwoone/jocoding/domain/Emotion.java

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
public class Emotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    private int joy;
    private int interest;
    private int sadness;
    private int fear;
    private int disgust;
    private int anger;

    @OneToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
}
