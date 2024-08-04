package threetwoone.jocoding.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SentimentAnalysisRequestDto {
    private String story;
    private List<String> userChoices;
}