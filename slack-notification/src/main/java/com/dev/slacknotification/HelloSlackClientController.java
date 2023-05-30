package com.dev.slacknotification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class HelloSlackClientController {

    @Value("${notification.slack.webhook.url}")
    private String slackAlertWebhookUrl;

    private final ObjectMapper objectMapper;

    @GetMapping(value = "/hello-error-slack-client")
    public String helloErrorSlackClient() throws IOException {
        Slack slack = Slack.getInstance();
        String errorMessage = "주문에서 에러 메세지 발생";
        SlackErrorMessage slackErrorMessage = new SlackErrorMessage(errorMessage);
        WebhookResponse response = slack.send(slackAlertWebhookUrl, objectMapper.writeValueAsString(slackErrorMessage));
        return "Hello Slack Alert Sent = " + response.getCode();
    }
}
