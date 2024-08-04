package threetwoone.jocoding.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import threetwoone.jocoding.service.FairyTaleService;
import threetwoone.jocoding.web.dto.*;

@RestController
@RequestMapping("/api/fairytale")
public class FairyTaleController {

    private final FairyTaleService fairyTaleService;

    public FairyTaleController(FairyTaleService fairyTaleService) {
        this.fairyTaleService = fairyTaleService;
    }

    @PostMapping("/generate")
    public ResponseEntity<StoryResponseDto> generateStory(@RequestBody StoryRequestDto request) {
        StoryResponseDto response = fairyTaleService.generateStoryParagraph(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unfold")
    public ResponseEntity<StoryResponseDto> unfoldStory(@RequestBody StoryUnfoldRequestDto request) {
        StoryResponseDto response = fairyTaleService.unfoldStoryParagraph(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<SentimentAnalysisDto> analyzeSentiment(@RequestBody SentimentAnalysisRequestDto request) {
        SentimentAnalysisDto analysis = fairyTaleService.analyzeSentiment(request);
        return ResponseEntity.ok(analysis);
    }
}