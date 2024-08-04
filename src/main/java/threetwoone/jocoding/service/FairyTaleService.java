package threetwoone.jocoding.service;

import threetwoone.jocoding.web.dto.*;

public interface FairyTaleService {
    StoryResponseDto generateStoryParagraph(StoryRequestDto request);

    SentimentAnalysisDto analyzeSentiment(SentimentAnalysisRequestDto request);

    StoryResponseDto unfoldStoryParagraph(StoryUnfoldRequestDto request);
}