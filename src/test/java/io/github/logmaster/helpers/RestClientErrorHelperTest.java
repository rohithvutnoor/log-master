package io.github.logmaster.helpers;

import io.github.logmaster.beans.RestClientResponse;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestClientErrorHelperTest {

    @Test
    public void testHandleAllExceptions() {
        ConnectException connectException = new ConnectException("'Connection refused");
        RestClientResponse response = RestClientErrorHelper.handle(connectException);
        assertEquals("Connection Error", response.getErrorType());
        assertTrue(response.getResponseBody().contains("Connection refused"));

        SocketTimeoutException socketTimeoutException = new SocketTimeoutException("Connection timed out");
        response = RestClientErrorHelper.handle(socketTimeoutException);
        assertEquals("Timeout Error", response.getErrorType());
        assertTrue(response.getResponseBody().contains("Connection timed out"));

        HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        clientErrorException.getResponseBodyAsString();
        response = RestClientErrorHelper.handle(clientErrorException);
        assertEquals("Client Error", response.getErrorType());
        assertEquals("400", response.getStatusCode());

        HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        serverErrorException.getResponseBodyAsString();
        response = RestClientErrorHelper.handle(serverErrorException);
        assertEquals("Server Error", response.getErrorType());
        assertEquals("500", response.getStatusCode());

        SSLHandshakeException sslHandshakeException = new SSLHandshakeException("SSL handshake failed");
        response = RestClientErrorHelper.handle(sslHandshakeException);
        assertEquals("SSL Handshake Error", response.getErrorType());
        assertTrue(response.getResponseBody().contains("SSL handshake failed"));

        SSLException sslException = new SSLException("SSL connection failed");
        response = RestClientErrorHelper.handle(sslException);
        assertEquals("SSL Error", response.getErrorType());
        assertTrue(response.getResponseBody().contains("SSL connection failed"));

        UnknownHostException unknownHostException = new UnknownHostException("Host not found");
        response = RestClientErrorHelper.handle(unknownHostException);
        assertEquals("Unknown Host", response.getErrorType());
        assertTrue(response.getResponseBody().contains("Host not found"));

        Throwable generalException = new Throwable("General error occurred");
        response = RestClientErrorHelper.handle(generalException);
        assertEquals("General Error", response.getErrorType());
        assertTrue(response.getResponseBody().contains("General error occurred"));
    }
}
