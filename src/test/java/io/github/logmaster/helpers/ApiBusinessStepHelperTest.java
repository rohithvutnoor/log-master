package io.github.logmaster.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ApiBusinessStepHelperTest {
    @InjectMocks
    private ApiBusinessStepHelper apiBusinessStepHelper;
    @Mock
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, String> details = new HashMap<>();
        details.put("get./api/users.desc", "Retrieve user details");
        details.put("post./api/users.desc", "Create a new user");
        details.put("get./api/orders/*.desc", "Retrieve order details");
        details.put("get./api/products/**.desc", "Retrieve product information");
        ReflectionTestUtils.setField(apiBusinessStepHelper, "details", details);
    }

    @Test
    void testGetBusinessDescriptionPostMethod() {
        String result = apiBusinessStepHelper.getBusinessDescription("POST", "/profile/update");
        assertEquals("Update Profile", result);
    }

    @Test
    void testGetBusinessDescriptionExactMatch() {
        String result = apiBusinessStepHelper.getBusinessDescription("GET", "/api/users");
        apiBusinessStepHelper.getDetails();
        assertEquals("Retrieve user details", result);
    }

    @Test
    void testGetBusinessDescriptionWildcardMatch() {
        String result = apiBusinessStepHelper.getBusinessDescription("GET", "/api/orders/123");
        assertEquals("Retrieve order details", result);
    }

    @Test
    void testGetBusinessDescriptionDoubleWildcardMatch() {
        String result = apiBusinessStepHelper.getBusinessDescription("GET",
                "/api/products/electronics/phones");
        assertEquals("Get Electronics Phones", result);
    }

    @Test
    void testGetBusinessDescriptionDefaultDescription() {
        String result = apiBusinessStepHelper.getBusinessDescription("DELETE", "/api/unknown");
        assertEquals("Delete Api Unknown", result);

    }

    @Test
    void testGetSubsystemBusinessDescription_HeaderPresent() {

        when(headers.getFirst("serviceDescription")).thenReturn("Custom Service Description");

        String result = apiBusinessStepHelper.getSubsystemBusinessDescription(headers, "GET",
                "/api/users");

        assertEquals("Custom Service Description", result);
    }

    @Test
    void testGetSubsystemBusinessDescriptionHeaderAbsent() {
        when(headers.getFirst("serviceDescription")).thenReturn(null);
        String result = apiBusinessStepHelper.getSubsystemBusinessDescription(headers, "GET",
                "/api/users");
        assertEquals("Get Api Users", result);
    }
}