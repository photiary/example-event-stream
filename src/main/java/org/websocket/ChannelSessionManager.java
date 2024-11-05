/**
 * packageName : org.websocket
 * fileName : ChannelSessionManager
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
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChannelSessionManager {
    private final Map<String, Map<String, SseEmitter>> channelSessions = new ConcurrentHashMap<>();

    public SseEmitter addSession(String channelId, String sessionId) {
        SseEmitter emitter = new SseEmitter();
        channelSessions.computeIfAbsent(channelId, k -> new ConcurrentHashMap<>()).put(sessionId, emitter);
        emitter.onCompletion(() -> removeSession(channelId, sessionId));
        emitter.onTimeout(() -> removeSession(channelId, sessionId));
        return emitter;
    }

    public void removeSession(String channelId, String sessionId) {
        Map<String, SseEmitter> sessions = channelSessions.get(channelId);
        if (sessions != null) {
            sessions.remove(sessionId);
        }
    }

    public void broadcastMessage(String channelId, String message) {
        Map<String, SseEmitter> sessions = channelSessions.get(channelId);
        if (sessions != null) {
            for (SseEmitter emitter : sessions.values()) {
                try {
                    log.info("Response message: {}",message);
                    emitter.send(SseEmitter
                            .event()
                            .name("chatbot")
                            .data(message));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }

    public void connectedMessage(String channelId, String message) {
        Map<String, SseEmitter> sessions = channelSessions.get(channelId);
        if (sessions != null) {
            for (SseEmitter emitter : sessions.values()) {
                try {
                    emitter.send(SseEmitter
                            .event()
                            .name("connected")
                            .data(message));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }
}
