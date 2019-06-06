package org.superbiz.moviefun;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RabbitMessageController {

    private final RabbitTemplate template;
    private final String queue;

    public RabbitMessageController(RabbitTemplate template, @Value("${rabbitmq.queue}") String queue) {
        this.template = template;
        this.queue = queue;
    }

    @PostMapping("/rabbit")
    public Map<String, String> publishMessage() {
        template.convertAndSend(queue, "This message will trigger the consumer");

        Map<String, String> response = new HashMap<>();
        response.put("response", "This is an unrelated JSON response");
        return response;
    }
}
