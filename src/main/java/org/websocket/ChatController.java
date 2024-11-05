/**
 * packageName : org.websocket
 * fileName : ChatController
 * author : USER
 * date : 2024-10-08
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2024-10-08           USER             최초 생성
 */
package org.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://10.112.58.50:8080/")
@RequestMapping("/chatbot")
public class ChatController {

    private final ChannelSessionManager sessionManager;

    public ChatController(ChannelSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // SSE 연결을 생성 (세션 등록)
    @GetMapping("/subscribe")
    @JwtAuth
    public  SseEmitter  connect(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String authorization
            ) {
        String channelId = "1";
        String sessionId = "a";
        log.info("Connecting to channel {} with session {}, authorization {} ", channelId, sessionId, authorization);


        SseEmitter sseEmitter = sessionManager.addSession( channelId, sessionId);
        sessionManager.connectedMessage(channelId, "connected");
        return sseEmitter;
    }



    // 채팅 메시지 브로드캐스트
    @PostMapping("/broadcast")
    public void sendMessage(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @RequestBody HashMap<String, Object> body) {
        String channelId = "1";
        log.info("Authorization {} Channel {}, Sending message: {}", authorization, channelId, body.get("message"));
        List<String> responseList = new ArrayList<>();
        responseList.add("## 타이틀\\n");
        responseList.add("- 유산소 운동: 빠르게 걷기를 추천드립니다.\\n" );
        responseList.add("\\n");
        responseList.add("가\\n\\n");
        responseList.add("## 타이틀\\n");
        responseList.add("- 1\\n");
        responseList.add("- 2\\n");
        responseList.add("# 타이틀1\\n");
        responseList.add("## 타이틀2\\n");
        responseList.add("### 타이틀3\\n");
        responseList.add("#### 타이틀4\\n");
        responseList.add("- 목록\\n");
        responseList.add("* 목록\\n");
        responseList.add("*이태릭*\\n");
        responseList.add("**강조**\\n");
        responseList.add("~삭제~\\n");
        responseList.add("<https://en.wikipedia.org/wiki/Hobbit#Lifestyle> \\\"Hobbit lifestyles\\\"\\n");
        responseList.add("![The San Juan Mountains are beautiful!](https://mdg.imgix.net/assets/images/san-juan-mountains.jpg \\\"San Juan Mountains\\\")\\n");
        responseList.add("<img src=\\\"https://mdg.imgix.net/assets/images/san-juan-mountains.jpg\\\" alt=\\\"The San Juan Mountains are beautiful!\\\" title=\\\"San Juan Mountains\\\">");
        responseList.add("끝.[EOS]");
        responseList.forEach(value -> {
            try {
                Thread.sleep(1000); // 500ms 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 발생 시 현재 스레드를 복구
            }
            sessionManager.broadcastMessage(channelId, "{\"message\": \"%s\"}".formatted(value));
        });
    }

    @GetMapping("/unsubscribe")
    public void unsubscribe(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String authorization
            ) {
        String channelId = "1";
        log.info("Authorization {} Channel {}", authorization, channelId);
        sessionManager.connectedMessage(channelId, "disconnected");
    }

    @PostMapping("/heartbeat/{channelId}")
    public void sendHeartbeat(@PathVariable String channelId, @RequestParam String message) {
        log.info("Channel {}, Sending message: {}", channelId, message);
        sessionManager.broadcastMessage(channelId, "{\"heartbeat\": \"%s\"}".formatted(message));
    }
}

