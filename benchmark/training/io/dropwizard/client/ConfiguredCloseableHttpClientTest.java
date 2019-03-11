package io.dropwizard.client;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ConfiguredCloseableHttpClientTest {
    private ConfiguredCloseableHttpClient configuredClient;

    private CloseableHttpClient closeableHttpClientMock = Mockito.mock(CloseableHttpClient.class);

    private RequestConfig defaultRequestConfigMock = Mockito.mock(RequestConfig.class);

    @Test
    public void getDefaultRequestConfig_returns_config_provided_at_construction() {
        assertThat(configuredClient.getDefaultRequestConfig()).isEqualTo(defaultRequestConfigMock);
    }

    @Test
    public void getClient_returns_config_provided_at_construction() {
        assertThat(configuredClient.getClient()).isEqualTo(closeableHttpClientMock);
    }
}

