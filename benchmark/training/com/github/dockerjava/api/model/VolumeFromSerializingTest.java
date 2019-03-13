package com.github.dockerjava.api.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static AccessMode.ro;


public class VolumeFromSerializingTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String json = "\"container1:ro\"";

    @Test
    public void deserializing() throws Exception {
        VolumesFrom volumeFrom = objectMapper.readValue(json, VolumesFrom.class);
        Assert.assertEquals(volumeFrom, new VolumesFrom("container1", ro));
    }

    @Test
    public void serializing() throws Exception {
        VolumesFrom volumeFrom = new VolumesFrom("container1", ro);
        Assert.assertEquals(objectMapper.writeValueAsString(volumeFrom), json);
    }
}

