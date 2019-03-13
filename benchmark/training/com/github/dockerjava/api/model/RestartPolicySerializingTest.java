package com.github.dockerjava.api.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Compares serialization results of various {@link RestartPolicy}s with what Docker (as of 1.3.3) actually sends when executing
 * <code>docker run --restart xxx</code>.
 */
public class RestartPolicySerializingTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // --restart no
    @Test
    public void noRestart() throws Exception {
        String json = objectMapper.writeValueAsString(RestartPolicy.noRestart());
        Assert.assertEquals(json, "{\"MaximumRetryCount\":0,\"Name\":\"\"}");
    }

    // --restart always
    @Test
    public void alwaysRestart() throws Exception {
        String json = objectMapper.writeValueAsString(RestartPolicy.alwaysRestart());
        Assert.assertEquals(json, "{\"MaximumRetryCount\":0,\"Name\":\"always\"}");
    }

    // --restart unless-stopped
    @Test
    public void unlessStoppedRestart() throws Exception {
        String json = objectMapper.writeValueAsString(RestartPolicy.unlessStoppedRestart());
        Assert.assertEquals(json, "{\"MaximumRetryCount\":0,\"Name\":\"unless-stopped\"}");
    }

    // --restart on-failure
    @Test
    public void onFailureRestart() throws Exception {
        String json = objectMapper.writeValueAsString(RestartPolicy.onFailureRestart(0));
        Assert.assertEquals(json, "{\"MaximumRetryCount\":0,\"Name\":\"on-failure\"}");
    }

    // --restart on-failure:2
    @Test
    public void onFailureRestartWithCount() throws Exception {
        String json = objectMapper.writeValueAsString(RestartPolicy.onFailureRestart(2));
        Assert.assertEquals(json, "{\"MaximumRetryCount\":2,\"Name\":\"on-failure\"}");
    }
}

