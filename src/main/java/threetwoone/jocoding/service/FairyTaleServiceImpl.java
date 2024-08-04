package threetwoone.jocoding.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import threetwoone.jocoding.web.dto.*;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class FairyTaleServiceImpl implements FairyTaleService {

    private final String geminiApiKey;
    private final String geminiApiUrl;
    private final RestTemplate restTemplate;
    private final Gson gson;

    public FairyTaleServiceImpl(
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.url}") String geminiApiUrl,
            RestTemplate restTemplate) {
        this.geminiApiKey = geminiApiKey;
        this.geminiApiUrl = geminiApiUrl;
        this.restTemplate = restTemplate;
        this.gson = new Gson();
    }

    @Override
    public StoryResponseDto generateStoryParagraph(StoryRequestDto request) {
        String prompt = constructPrompt(request);
        String rawResponse = callGeminiApi(prompt);

        // Parse the raw response
        String[] sections = rawResponse.split("---");

        if (sections.length < 2) {
            throw new RuntimeException("Unexpected response format from Gemini API. Expected story paragraph and choices separated by '---'.");
        }

        String storyParagraph = sections[0].trim();
        List<String> choices = extractChoices(sections[1].trim());

        // Start with paragraph number 1
        int paragraphNumber = 1;

        return new StoryResponseDto(storyParagraph, choices, paragraphNumber);
    }

    private List<String> extractChoices(String choicesSection) {
        // Split the choices section into individual choices and filter non-empty strings
        List<String> choices = Arrays.stream(choicesSection.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Ensure that exactly two choices are provided
        if (choices.size() != 2) {
            throw new RuntimeException("Unexpected number of choices. Expected 2 choices, but got " + choices.size());
        }

        return choices;
    }




    private String constructPrompt(StoryRequestDto request) {
        return String.format(
                "You are an AI who creates storybooks for children. The children's books you create will be delivered to children so that their mental and emotional states can be analyzed. Next is the process of creating a fairy tale. Use the following details to craft a unique story:\n" +
                        "\n1. **Background**: %s\n" +
                        "2. **Development Type**: %s (Interactive means dialogue unfolds at each midpoint, Selective means the story unfolds by choosing from given options)\n" +
                        "3. **Additional Requirements**: %s\n\n" +
                        "Create a detailed first paragraph of a children's story based on these inputs. The story should be written in Korean.\n" +
                        "The story paragraph should be at least 5-7 sentences long, setting the scene and introducing the character and situation.\n" +
                        "create a midpoint in the story so that the story can unfold through dialogue or choices.\n" +
                        "After the story paragraph, provide a transitional sentence leading to the choices, such as \"어떤 것을 선택할까?\" (Which one did you choose?)\n" +
                        "Provide exactly two options for progressing the story, separated by '---'.\n" +
                        "Ensure the story and options relate to the provided background and development type.\n\n" +
                        "Example Format:\n" +
                        "어두컴컴한 밤, 깊은 숲 속에는 신비로운 마법이 가득했어.\n" +
                        "\n" +
                        "은은한 달빛이 춤추듯 숲을 비추고, 나무들은 살아있는 듯 꿈틀거렸지.\n" +
                        "\n" +
                        "숲 가장자리에는 작고 귀여운 요정, '반짝'이가 살고 있었어.\n" +
                        "\n" +
                        "반짝이는 밤마다 숲 속을 누비며 반짝이는 빛으로 숲을 밝혀 주었지.\n" +
                        "\n" +
                        "어느 날 밤, 반짝이는 숲 가장자리에서 이상한 소리를 들었어.\n" +
                        "\n" +
                        "\"도와주세요! 제발 도와주세요!\"\n" +
                        "\n" +
                        "반짝이는 소리 나는 곳으로 달려갔어.\n" +
                        "\n" +
                        "어두운 밤, 반짝이는 누구를 만났을까?\n" +
                        "\n" +
                        "---\n" +
                        "1. 커다란 나무 아래에서 울고 있는 토끼\n" +
                        "2. 빛을 잃은 반딧불이\n",
                request.getBackground(),
                request.getDevelopmentType(),
                request.getAdditionalRequirements()
        );
    }

    private String callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(
                Map.of("parts", Collections.singletonList(
                        Map.of("text", prompt)
                ))
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, entity, String.class);
            String rawResponse = response.getBody();

            Logger logger = LoggerFactory.getLogger(FairyTaleServiceImpl.class);
            logger.info("Raw Response from Gemini API: {}", rawResponse);

            return parseGeminiResponse(rawResponse);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to get response from Gemini API: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    @Override
    public StoryResponseDto unfoldStoryParagraph(StoryUnfoldRequestDto request) {
        int currentParagraphNumber = request.getParagraphNumber();

        // Check if the story has reached the maximum paragraph number
        if (currentParagraphNumber >= 10) {
            return new StoryResponseDto("The story has reached its conclusion. Thank you for participating!", Collections.emptyList(), currentParagraphNumber);
        }

        // Construct prompt for the next story paragraph
        String prompt = constructUnfoldPrompt(request);
        String rawResponse = callGeminiApi(prompt);

        // Parse the raw response
        String[] sections = rawResponse.split("---");

        if (sections.length < 2) {
            throw new RuntimeException("Unexpected response format from Gemini API. Expected story paragraph and choices separated by '---'.");
        }

        String nextStoryParagraph = sections[0].trim();
        List<String> nextChoices = extractChoices(sections[1].trim());

        // Increment the paragraph number
        int nextParagraphNumber = currentParagraphNumber + 1;

        return new StoryResponseDto(nextStoryParagraph, nextChoices, nextParagraphNumber);
    }

    private String constructUnfoldPrompt(StoryUnfoldRequestDto request) {
        return String.format(
                "You are continuing a children's story. The following is the current state of the story and the user's choice. Continue the story based on the user's choice and provide the next paragraph and options.\n\n" +
                        "### Story So Far\n" +
                        "%s\n\n" +
                        "### User Choice\n" +
                        "%s\n\n" +
                        "Please continue the story in Korean. The next paragraph should be detailed and set the scene for what happens after the user's choice. " +
                        "The story paragraph should be at least 8 sentences long, setting the scene and introducing the character and situation.\n" +
                        "After the paragraph, provide exactly two options for the user to choose from for the next step in the story, clearly separated by '---'.\n" +
                        "Include a connecting sentence like \"어떤 것을 선택할까요?\" (Which one did you choose?) before the options.\n" +
                        "Ensure that the options are logically connected to the story and reflect possible outcomes or directions the story could take.\n\n" +
                        "Finish the story when there are about 10 paragraphs.\n" +
                        "When the story ends, don't give any more choices and end with a story that concludes the storybook.\n" +
                        "### Example Story Format\n" +
                        "깊은 마법의 숲에는 '벨라'라는 호기심 많은 여우가 살고 있었습니다. 어느 날 벨라는 숲을 탐험하며 거대한 나무에 다가갔습니다. 나무에 다다랐을 때, 벨라는 이상한 빛을 보았습니다...\n" +
                        "어떤 것을 선택할까요?\n" +
                        "---\n" +
                        "1. 나무 구멍에서 빛나는 보물\n" +
                        "2. 반짝반짝 빛나는 반딧불\n",
                request.getCurrentStory(),
                request.getUserChoice()
        );
    }



    private String parseGeminiResponse(String responseBody) {
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

        if (candidates != null && candidates.size() > 0) {
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");

            if (parts != null && parts.size() > 0) {
                return parts.get(0).getAsJsonObject().get("text").getAsString();
            }
        }

        throw new RuntimeException("Unexpected response format from Gemini API");
    }


    public SentimentAnalysisDto analyzeSentiment(SentimentAnalysisRequestDto request) {
        String prompt = constructSentimentAnalysisPrompt(request);
        String rawResponse = callGeminiApi(prompt);
        return parseSentimentAnalysisResponse(rawResponse);
    }

    private String constructSentimentAnalysisPrompt(SentimentAnalysisRequestDto request) {
        return String.format(
                "이야기: %s\n" +
                        "사용자의 선택: %s\n\n" +
                        "위의 이야기와 사용자의 선택을 바탕으로 어린이의 감정 분석을 수행하세요.\n" +
                        "감정: 기쁨, 흥미, 슬픔, 두려움, 혐오감, 분노의 비중을 각각 출력하세요. 총합이 100%%가 되도록 하세요.\n" +
                        "정신 상태: 안정성, 긍정성, 부정성, 흥분의 비율을 계산하여 출력하세요. 총합이 100%%가 되도록 하세요.\n" +
                        "종합 분석 결과: 이야기의 내용과 어린이의 답변 및 선택을 참고하여 작성하세요.",
                request.getStory(),
                String.join(", ", request.getUserChoices())
        );
    }

    private SentimentAnalysisDto parseSentimentAnalysisResponse(String response) {
        // Parse the response and create a SentimentAnalysisDto
        // This is a placeholder implementation and should be adjusted based on the actual response format
        return new SentimentAnalysisDto(
                Map.of("기쁨", 30, "흥미", 20, "슬픔", 10, "두려움", 15, "혐오감", 5, "분노", 20),
                Map.of("안정성", 40, "긍정성", 30, "부정성", 20, "흥분", 10),
                "종합 분석 결과: " + response
        );
    }
}