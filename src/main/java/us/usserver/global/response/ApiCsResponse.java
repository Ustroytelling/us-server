package us.usserver.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
public class ApiCsResponse<T> {
    private boolean isSuccess;
    private String message;
    private T data;

    public static <T> ApiCsResponse<T> success(T data) {
        return new ApiCsResponse<>(true, null, data);
    }
    public static ApiCsResponse<Void> success() {
        return new ApiCsResponse<>(true, null, null);
    }
    public static ApiCsResponse<Void> fail(String message) {
        return new ApiCsResponse<>(false, message, null);
    }
}
