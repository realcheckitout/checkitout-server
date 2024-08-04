package threetwoone.jocoding.web.dto;

import lombok.Data;

@Data
public class StoryUnfoldRequestDto {
    private String currentStory; // Keep track of the entire story
    private String userChoice;
    private int paragraphNumber;
}
