package io.github.logmaster.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
@AllArgsConstructor
public class RestClientResponse {
    String errorType;
    String statusCode;
    String responseBody;
    HttpHeaders responseHeaders;
}

