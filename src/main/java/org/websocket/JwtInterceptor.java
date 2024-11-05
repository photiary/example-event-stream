/**
 * packageName : org.websocket
 * fileName : JwtInterceptor
 * author : USER
 * date : 2024-10-11
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2024-10-11           USER             최초 생성
 */
package org.websocket;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TEST_TOKEN = "yJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiZmFtaWx5aGVhbHRoMiJdLCJzdWIiOiJmdW5hIiwidXNlcl9uYW1lIjoiZnVuYSIsInNjb3BlIjpbInBoci5yZWFkIiwicGhyLndyaXRlIl0sImlzcyI6IlJlZHdvb2RQbGF0Zm9ybSIsIm5hbWUiOiLstZztm4giLCJleHAiOjE3Mjk1MTAxNDUsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJmOGMzZWNjNS02ZDg2LTRiMGYtYmFiMC03M2FiN2E4NWE1NTUiLCJjbGllbnRfaWQiOiJmYW1pbHloZWFsdGgyIn0.VdeYVVfKANvu2FnZq5qNVT9DbKn7PlAHTxNPiuhtJGDoG8_cbIi5UJMADR2MYLH6rQtTpdbzrU9Zp3rjOo2le0q5hOuDXDIJlrmEq9Ldawe8ijWOlQ84YRXcjffN33yN72keuBKrq-Uhu0Lo4oyQAc977nzfGN5vwe0xrM4QkR8kiSi-hh0yx2G6_hqC_GYzJ-a0SxW5sBY2FNFxPERqk0ttQOU0QTuVM5E0tVUAtEQK2oP5h836l79_QgtcySgBTu71x7E52KWFUb7PzRvQxXl3C1VZUQ-0lwpLGUu7r32orDhkYyTDrn-NgF2N9kjvMoRYieNWp-DH6KVME228Ew";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 핸들러가 메서드가 아닐 경우 패스
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        JwtAuth jwtAuth = handlerMethod.getMethodAnnotation(JwtAuth.class);

        // @JwtAuth 어노테이션이 없는 경우 패스
        if (jwtAuth == null) {
            return true;
        }

        // JWT 토큰 가져오기
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        token = token.substring(7); // "Bearer " 이후의 토큰만 추출
        log.info("Authorization token: {}", token);

        // JWT 토큰 검증
        int result  = validateToken(token); // 토큰 검증

        if(result == -1) {
            log.error("Invalid expired {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } else if (result == -2) {
            log.error("Invalid token {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }


        // 토큰이 유효할 경우에만 컨트롤러 호출 허용
        return true;
    }

    private int validateToken(String token) {
        if (token.equals(TEST_TOKEN)) {
            return -1;
        } else if (token.equals("2")) {
            return -2;
        }
        return 0;
    }
}
