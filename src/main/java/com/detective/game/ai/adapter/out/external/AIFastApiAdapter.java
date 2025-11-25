package com.detective.game.ai.adapter.out.external;

import com.detective.game.ai.adapter.out.external.dto.AIApiRequest;
import com.detective.game.ai.adapter.out.external.dto.AIApiResponse;
import com.detective.game.ai.adapter.out.external.dto.AIEvaluateRequest;
import com.detective.game.ai.adapter.out.external.dto.AIEvaluateResponse;
import com.detective.game.ai.application.port.command.AskAIOutboundCommand;
import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import com.detective.game.ai.application.port.out.CallAIPort;
import com.detective.game.ai.application.port.out.EvaluateFinalSubmitPort;
import com.detective.game.ai.application.port.out.dto.AIApiRawAnswer;
import com.detective.game.ai.application.port.out.dto.AIEvaluateRawResponse;
import com.detective.game.common.exception.ExternalAIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static com.detective.game.common.exception.ErrorMessage.AI_BAD_RESPONSE;
import static com.detective.game.common.exception.ErrorMessage.AI_COMMUNICATION_FAILED;
import static com.detective.game.common.exception.ErrorMessage.AI_EMPTY_RESPONSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIFastApiAdapter implements CallAIPort, EvaluateFinalSubmitPort {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url:http://localhost:8000}")
    private String baseUrl;

    // ===== 1) 질문 (/ask) =====
    @Override
    public AIApiRawAnswer call(AskAIOutboundCommand command) {

        AIApiRequest body = AIApiRequest.from(command);

        HttpEntity<AIApiRequest> entity =
                new HttpEntity<>(body, createJsonHeaders());

        try {
            ResponseEntity<AIApiResponse> response =
                    restTemplate.exchange(
                            baseUrl + "/api/ai/ask",
                            HttpMethod.POST,
                            entity,
                            AIApiResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalAIException(AI_BAD_RESPONSE);
            }

            AIApiResponse res = response.getBody();

            if (res == null || res.getAnswer() == null) {
                throw new ExternalAIException(AI_EMPTY_RESPONSE);
            }

            return new AIApiRawAnswer(res.getAnswer());

        } catch (RestClientException e) {
            throw new ExternalAIException(AI_COMMUNICATION_FAILED);
        }
    }

    // ===== 2) 채점 (/evaluate) =====
    @Override
    public AIEvaluateRawResponse evaluate(EvaluateFinalSubmitCommand command) {

        AIEvaluateRequest body = AIEvaluateRequest.from(command);
        HttpEntity<AIEvaluateRequest> entity =
                new HttpEntity<>(body, createJsonHeaders());

        try {
            ResponseEntity<AIEvaluateResponse> response =
                    restTemplate.exchange(
                            baseUrl + "/api/ai/score",
                            HttpMethod.POST,
                            entity,
                            AIEvaluateResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalAIException(AI_BAD_RESPONSE);
            }

            AIEvaluateResponse res = response.getBody();
            if (res == null) {
                throw new ExternalAIException(AI_EMPTY_RESPONSE);
            }

            return new AIEvaluateRawResponse(
                    res.getScore(),
                    res.getFeedback()
            );

        } catch (RestClientException e) {
            throw new ExternalAIException(AI_COMMUNICATION_FAILED);
        }
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
