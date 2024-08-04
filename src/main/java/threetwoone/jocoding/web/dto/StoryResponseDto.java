package threetwoone.jocoding.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StoryResponseDto {
    private String storyParagraph;
    private List<String> choices;
    private int paragraphNumber;
}
