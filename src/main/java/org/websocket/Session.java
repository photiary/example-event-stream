/**
 * packageName : org.websocket
 * fileName : Session
 * author : USER
 * date : 2024-10-02
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2024-10-02           USER             최초 생성
 */
package org.websocket;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class Session {
    private WebSocketSession aiBackendSession;
    private WebSocketSession clientSession;

    public Session(WebSocketSession aiBackendSession, WebSocketSession clientSession) {
        this.aiBackendSession = aiBackendSession;
        this.clientSession = clientSession;
    }

    public void setAiBackendSession(WebSocketSession aiBackendSession) {
        this.aiBackendSession = aiBackendSession;
    }
}
