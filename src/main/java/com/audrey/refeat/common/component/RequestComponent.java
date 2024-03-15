package com.audrey.refeat.common.component;

import com.audrey.refeat.domain.chat.dto.request.SendAiMessageDto;
import com.audrey.refeat.domain.project.exception.AiServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.time.Duration;

@Component
public class RequestComponent {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final String aiUrl;


    public RequestComponent(RestTemplateBuilder restTemplateBuilder, WebClient.Builder webClientBuilder, @Value("${ai.url}") String aiUrl) {
        this.restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(60L)).build();
        this.aiUrl = aiUrl;
        this.webClient = webClientBuilder.baseUrl(this.aiUrl).build();

    }

    public <D, T> T jsonPost(String url, D data, Class<T> responseType) throws Exception {
        // create headers

        // build the request
        HttpEntity<D> entity = new HttpEntity<>(data);
        try {
            ResponseEntity<T> response = restTemplate.postForEntity(aiUrl + url, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            e.getResponseBodyAsString();
            throw new AiServerErrorException();
        }
    }


    public StreamingResponseBody getChatStreaming(String url, SendAiMessageDto data) throws Exception {

        ResponseEntity<InputStream> responseEntity = restTemplate.postForEntity(aiUrl + url, data, InputStream.class);

        return outputStream -> {
            try {
                final InputStream inputStream = responseEntity.getBody();

                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) {
                    outputStream.write(bytes, 0, length);
                }
                inputStream.close();
                outputStream.flush();

            } catch (final Exception e) {
                System.out.println(e);
            }
        };
    }

    public Flux<ServerSentEvent<String>> getStreamingText(SendAiMessageDto data, Long projectId){

        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
        };
        return webClient.get()
                .uri("/aichat_dummy")
                .retrieve()
                .bodyToFlux(type);
    }

    public Boolean healthCheck() {
        try {
            return restTemplate.getForEntity(aiUrl + "/health", String.class).getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

}
