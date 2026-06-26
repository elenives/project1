package com.example.learning.controller;

import com.example.learning.dto.ApiResponse;
import com.example.learning.mq.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
public class MqController {

    private final MessageProducer messageProducer;

    @PostMapping("/send")
    public ApiResponse<String> send(@RequestParam(defaultValue = "notify") String tag,
                                    @RequestParam String message) {
        messageProducer.sendSync(tag, message);
        return ApiResponse.ok("消息已发送: tag=" + tag);
    }
}
