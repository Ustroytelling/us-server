package us.usserver.domain.notification;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import us.usserver.domain.notification.service.NotificationService;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") Long lastEventId,
            HttpServletResponse response
    ) {
        Long receiverId = 500L; // TODO: 변경 예정

        response.setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
        SseEmitter sseEmitter = notificationService.subscribe(receiverId, lastEventId);
        return ResponseEntity.ok(sseEmitter);
    }
}
