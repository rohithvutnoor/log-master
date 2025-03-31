package io.github.logmaster.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanitizerUtilTest {

    @Test
    void testSanitization() {
        // Test HTML Sanitization
        String htmlInput = "<script>alert('XSS')</script>";
        String sanitizedHTML = SanitizerUtil.sanitizeHTML(htmlInput);
        System.out.println("Sanitized HTML: " + sanitizedHTML);
        assertEquals("&lt;script&gt;alert('XSS')&lt;/script&gt;", sanitizedHTML);

        // Test JavaScript Sanitization
        String jsInput = "<script>alert('XSS')</script>";
        String sanitizedJS = SanitizerUtil.sanitizeJS(jsInput);
        System.out.println("Sanitized JS:" + sanitizedJS);

        assertEquals("<script>alert(\\'XSS\\')<\\/script>", sanitizedJS);

        // Test XML Sanitization
        String xmlInput = "<test>\"data\"</test>";
        String sanitizedXML = SanitizerUtil.sanitizeXML(xmlInput);

        System.out.println("Sanitized XML: " + sanitizedXML);
        assertEquals("&lt;test&gt;&quot;data&quot;&lt;/test&gt;", sanitizedXML);

        // Test JSON Sanitization
        String jsonInput = "\"XSS\"";
        String sanitizedJSON = SanitizerUtil.sanitizeJSON(jsonInput);

        System.out.println("Sanitized JSON:" + sanitizedJSON);
        assertEquals("\\\"XSS\\\"", sanitizedJSON);

        // Test URL Encoding
        String urlInput = "https://example.com/?param=<script>";

        String sanitizedURL = SanitizerUtil.sanitizeURL(urlInput);

        System.out.println("Sanitized URL: " + sanitizedURL);

        assertEquals("https%3A%2F%2Fexample.com%2F%3Fparam%3D%3Cscript%3E", sanitizedURL);

        // Test Safe String Sanitization (General)
        String safeInput = "<Hello>";
        String sanitizedSafeString = SanitizerUtil.sanitize(safeInput);
        System.out.println("Sanitized Safe String:" + sanitizedSafeString);

        assertEquals("&lt;Hello&gt;", sanitizedSafeString);

        // Test Header Sanitization
        Map<String, String> headers = new HashMap<>();
        headers.put("<XSS>", "<script>");
        headers.put("<script>", "<XSS>");

        Map<String, String> sanitizedHeaders = SanitizerUtil.sanitizeHeaders(headers);
        System.out.println("Sanitized Headers:" + sanitizedHeaders);
        assertEquals("&lt;script&gt;", sanitizedHeaders.get("&lt;XSS&gt;"));

        assertEquals("&lt;XSS&gt;", sanitizedHeaders.get("&lt;script&gt;"));
        // Test Body Sanitization (JSON case)
        String jsonBody = "{\"key\": \"value\"}";
        String sanitizedBody = SanitizerUtil.sanitizeBody(jsonBody);
        System.out.println("Sanitized Body:" + sanitizedBody);
        assertEquals("{\\\"key\\\": \\\"value\\\"}", sanitizedBody);
    }
}

