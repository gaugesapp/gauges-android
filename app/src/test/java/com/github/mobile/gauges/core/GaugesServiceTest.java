package com.github.mobile.gauges.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link GaugesService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GaugesServiceTest {

    /**
     * Create reader for string
     *
     * @param value
     * @return input stream reader
     * @throws IOException
     */
    private static BufferedReader createReader(String value) throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                value.getBytes(HttpRequest.CHARSET_UTF8))));
    }

    @Mock
    private HttpRequest request;

    private GaugesService service;

    /**
     * Set up default mocks
     *
     * @throws IOException
     */
    @Before
    public void before() throws IOException {
        service = new GaugesService("key") {
            protected HttpRequest execute(HttpRequest request) throws IOException {
                return GaugesServiceTest.this.request;
            }
        };
        doReturn(true).when(request).ok();
    }

    /**
     * Verify getting gauges with an empty response
     *
     * @throws IOException
     */
    @Test
    public void getGaugesEmptyResponse() throws IOException {
        doReturn(createReader("")).when(request).bufferedReader();
        List<Gauge> gauges = service.getGauges();
        assertNotNull(gauges);
        assertTrue(gauges.isEmpty());
    }

    /**
     * Verify getting page content with an empty response
     *
     * @throws IOException
     */
    @Test
    public void getContentEmptyResponse() throws IOException {
        doReturn(createReader("")).when(request).bufferedReader();
        List<PageContent> content = service.getContent("id");
        assertNotNull(content);
        assertTrue(content.isEmpty());
    }

    /**
     * Verify getting referrers with an empty response
     *
     * @throws IOException
     */
    @Test
    public void getReferrersEmptyResponse() throws IOException {
        doReturn(createReader("")).when(request).bufferedReader();
        List<Referrer> referrers = service.getReferrers("id");
        assertNotNull(referrers);
        assertTrue(referrers.isEmpty());
    }
}
