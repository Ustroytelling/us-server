package us.usserver.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import us.usserver.global.exception.*;

import java.net.BindException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(NovelNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> novelNotFoundHandler(Exception e) {
        log.error(ExceptionMessage.Novel_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> authorNotFoundHandler(Exception e) {
        log.error(ExceptionMessage.Author_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> memberNotFoundHandler(Exception e) {
        log.error(ExceptionMessage.Member_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(ChapterNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> chapterNotFoundHandler(Exception e) {
        log.error(ExceptionMessage.Chapter_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(ParagraphNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> paragraphNotFoundHandler(Exception e) {
        log.error(ExceptionMessage.Paragraph_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(MainAuthorIsNotMatchedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    protected ApiCsResponse<Object> mainAuthorIsNotMatchedHandler(Exception e) {
        log.error(ExceptionMessage.Main_Author_NOT_MATCHED);
        return new ApiCsResponse<>(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> ValidRequestModelHandler(BindException e) {
        log.error(ExceptionMessage.Valid_ModelAttribute_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> ValidRequestBodyHandler(MethodArgumentNotValidException e) {
        log.error(ExceptionMessage.Valid_RequestBody_NOT_FOUND);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiCsResponse<Object> tokenInvalidHandler(Exception e) {
        log.error(ExceptionMessage.Token_VERIFICATION);
        log.error(ExceptionMessage.Token_EXPIRED);
        return new ApiCsResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }

}
