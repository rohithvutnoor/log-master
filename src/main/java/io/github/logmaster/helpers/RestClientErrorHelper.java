package io.github.logmaster.helpers;

import io.github.logmaster.beans.RestClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@Slf4j
public class RestClientErrorHelper {


    private RestClientErrorHelper() {
        //default constructor
    }

    public static RestClientResponse handle(Throwable rootCause) {
        if (rootCause instanceof ConnectException connectException) {
            return mapConnectionErrors(connectException);
        } else if (rootCause instanceof SocketTimeoutException socketTimeoutException) {
            return mapTimeOutErrors(socketTimeoutException);
        } else if (rootCause instanceof HttpClientErrorException httpClientErrorException) {
            return mapHttpClientErrors(httpClientErrorException);
        } else if (rootCause instanceof HttpServerErrorException httpServerErrorException) {
            return mapHttpServerErrors(httpServerErrorException);
        } else if (rootCause instanceof SSLHandshakeException handshakeException) {
            return mapSSLHandshakeErrors(handshakeException);
        } else if (rootCause instanceof SSLException sslException) {
            return mapSSLErrors(sslException);
        } else if (rootCause instanceof UnknownHostException hostException) {
            return mapUnknownHostErrors(hostException);
        } else {
            return mapHttpGeneralErrors(rootCause);
        }
    }

    private static RestClientResponse map(String error, String code, String msg, HttpHeaders headers) {
        return new RestClientResponse(error, code, msg, headers);
    }

    private static RestClientResponse mapConnectionErrors(ConnectException ex) {
        return map("Connection Error", "", ex.getMessage(), null);
    }

    private static RestClientResponse mapTimeOutErrors(SocketTimeoutException ex) {
        return map("Timeout Error", "", ex.getMessage(), null);
    }

    private static RestClientResponse mapHttpClientErrors(HttpClientErrorException ex) {
        return map("Client Error",
                String.valueOf(ex.getStatusCode().value()),
                ex.getResponseBodyAsString(), ex.getResponseHeaders());
    }

    private static RestClientResponse mapHttpServerErrors(HttpServerErrorException ex) {
        return map("Server Error"
                , String.valueOf(ex.getStatusCode().value()),
                ex.getResponseBodyAsString(), ex.getResponseHeaders());
    }


    private static RestClientResponse mapSSLHandshakeErrors(SSLHandshakeException ex) {
        return map("SSL Handshake Error", "", ex.getMessage(), null);
    }

    private static RestClientResponse mapSSLErrors(SSLException ex) {
        return map("SSL Error",
                "", ex.getMessage(), null);
    }

    private static RestClientResponse mapUnknownHostErrors(UnknownHostException ex) {
        return map("Unknown Host", "", ex.getMessage(), null);
    }

    private static RestClientResponse mapHttpGeneralErrors(Throwable ex) {
        return map("General Error",
                "", ex.getMessage(), null);
    }

}