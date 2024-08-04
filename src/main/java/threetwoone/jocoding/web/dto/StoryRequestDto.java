package threetwoone.jocoding.web.dto;

import lombok.Data;

@Data
public class StoryRequestDto {
    private String background;
    private String developmentType; // "interactive" or "selective"
    private String additionalRequirements;
}