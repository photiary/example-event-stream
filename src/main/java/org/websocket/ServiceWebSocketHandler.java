package org.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceWebSocketHandler.class);
    private final Map<String, Session> sessionMap = new HashMap<String, Session>();

//    private WebSocketSession aiBackendSession;
//    private WebSocketSession clientSession;

    public ServiceWebSocketHandler() {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {

        String path = clientSession.getUri().getPath();
        String[] segments = path.split("/");
        String lastSegment = segments[segments.length - 1];
        System.out.println("Connection Established client session id: " + clientSession.getId());

        Session session = new Session(null, clientSession);
        sessionMap.put(lastSegment, session);




        // AI 백엔드와 WebSocket 연결
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketSession webSocketSession = client.execute(new TextWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession aiBackendSession) throws Exception {
                    System.out.println("Connection Established ai session id: " + aiBackendSession.getId());
                    sessionMap.get(lastSegment).setAiBackendSession(aiBackendSession);
                }

                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    // AI 백엔드에서 받은 메시지 처리
                    String response = message.getPayload();
                    System.out.println("AI SessionId:" + session.getId() + " Received message from AI Backend: " + response);

                    // 메시지 처리를 원하는 대로 수행
                    sessionMap.get(lastSegment).getClientSession().sendMessage(message);

                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    System.out.println("Error in WebSocket communication: " + exception.getMessage());
                }
            }, "ws://211.34.247.146:18000/ws/chat/%s/".formatted(lastSegment)).get();

            System.out.println(">>>>>>>>>>>>>>" + webSocketSession.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession clientSession, TextMessage message) throws Exception {
        System.out.println("Client SessionId:" + clientSession.getId() + " Received message from client: " + message);

        // 클라이언트로부터 메시지 수신 -> AI 백엔드로 전달
        String path = clientSession.getUri().getPath();
        String[] segments = path.split("/");
        String lastSegment = segments[segments.length - 1];

       this.sessionMap.get(lastSegment).getAiBackendSession().sendMessage(message);
    }
}
