package io.github.logmaster.configuration.rest;

import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.interceptor.ClientHttpRequestLogInterceptor;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class RestTemplateConfigTest {
    @InjectMocks
    private RestTemplateConfig restTemplateConfig;
    @Mock
    private DefaultMaskService defaultMaskService;
    @Mock
    private ClientHttpRequestLogInterceptor interceptor;
    @Mock
    private ApiBusinessStepHelper apiBusinessStepHelper;
    @Mock
    private TraceIdKeyFinder traceIdKeyFinder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRestTemplateBeanCreation() {
        RestTemplate restTemplate = restTemplateConfig.restTemplate(defaultMaskService,
                interceptor, apiBusinessStepHelper, traceIdKeyFinder);


        assertNotNull(restTemplate);

        assertNotNull(restTemplate.getInterceptors());

        assertNotNull(restTemplate.getMessageConverters());
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

        assertNotNull(interceptors);
        assert (interceptors.contains(interceptor));
    }
}

