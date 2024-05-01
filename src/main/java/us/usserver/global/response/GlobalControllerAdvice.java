package us.usserver.global.response;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import us.usserver.global.response.exception.BaseException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ApiCsResponse<Void>> novelNotFoundHandler(BaseException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getStatus())
                .body(ApiCsResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ApiCsResponse<Void>> illegalArgumentHandler(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ApiCsResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ApiCsResponse<Void>> unsupportedOperationHandler(UnsupportedOperationException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ApiCsResponse.fail(e.getMessage()));
    }

    @ExceptionHandler({JWTVerificationException.class, AuthenticationServiceException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ApiCsResponse<Void>> authenticationHandler(RuntimeException e) {
        log.error("로그인 오류 : {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiCsResponse.fail(e.getMessage()));
    }
}
