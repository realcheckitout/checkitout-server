package threetwoone.jocoding.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SentimentAnalysisDto {
    private Map<String, Integer> emotions;
    private Map<String, Integer> mentalState;
    private String totalAnalysis;
}