package com.netflix.discovery.shared.resolver.aws;


import com.netflix.discovery.EurekaClientConfig;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class ConfigClusterResolverTest {
    private final EurekaClientConfig clientConfig = Mockito.mock(EurekaClientConfig.class);

    private final List<String> endpointsC = Arrays.asList("http://1.1.1.1:8000/eureka/v2/", "http://1.1.1.2:8000/eureka/v2/", "http://1.1.1.3:8000/eureka/v2/");

    private final List<String> endpointsD = Arrays.asList("http://1.1.2.1:8000/eureka/v2/", "http://1.1.2.2:8000/eureka/v2/");

    private final List<String> endpointsWithBasicAuth = Arrays.asList("https://myuser:mypassword@1.1.3.1/eureka/v2/");

    private ConfigClusterResolver resolver;

    @Test
    public void testReadFromConfig() {
        List<AwsEndpoint> endpoints = resolver.getClusterEndpoints();
        Assert.assertThat(endpoints.size(), CoreMatchers.equalTo(6));
        for (AwsEndpoint endpoint : endpoints) {
            if (endpoint.getZone().equals("us-east-1e")) {
                Assert.assertThat("secure was wrong", endpoint.isSecure(), CoreMatchers.is(true));
                Assert.assertThat("serviceUrl contains -1", endpoint.getServiceUrl().contains("-1"), CoreMatchers.is(false));
                Assert.assertThat("BASIC auth credentials expected", endpoint.getServiceUrl().contains("myuser:mypassword"), CoreMatchers.is(true));
            }
        }
    }
}

