/**
 * packageName : org.websocket
 * fileName : JwtAuth
 * author : USER
 * date : 2024-10-11
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2024-10-11           USER             최초 생성
 */
package org.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에도 사용 가능
public @interface JwtAuth {
}