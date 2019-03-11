package io.hawt.springboot;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HawtioEndpointTest {
    private EndpointPathResolver resolver;

    private HawtioEndpoint hawtioEndpoint;

    @Test
    public void testForwardHawtioRequestToIndexHtml() {
        Mockito.when(resolver.resolve("hawtio")).thenReturn("/actuator/hawtio");
        String result = hawtioEndpoint.forwardHawtioRequestToIndexHtml();
        Assert.assertEquals("forward:/actuator/hawtio/index.html", result);
    }
}

