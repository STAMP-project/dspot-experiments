package com.github.dockerjava.api.model;


import Capability.ALL;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class CapabilityTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializeCapability() throws Exception {
        String json = objectMapper.writeValueAsString(ALL);
        Assert.assertEquals(json, "\"ALL\"");
    }

    @Test
    public void deserializeCapability() throws Exception {
        Capability capability = objectMapper.readValue("\"ALL\"", Capability.class);
        Assert.assertEquals(capability, ALL);
    }

    @Test(expected = JsonMappingException.class)
    public void deserializeInvalidCapability() throws Exception {
        objectMapper.readValue("\"nonsense\"", Capability.class);
    }
}

