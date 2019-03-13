package com.github.dockerjava.api.model;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class VolumeBindsTest {
    @Test
    public void t() throws IOException {
        String s = "{\"/data\":\"/some/path\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        VolumeBinds volumeBinds = objectMapper.readValue(s, VolumeBinds.class);
        VolumeBind[] binds = volumeBinds.getBinds();
        Assert.assertEquals(binds.length, 1);
        Assert.assertEquals(binds[0].getHostPath(), "/some/path");
        Assert.assertEquals(binds[0].getContainerPath(), "/data");
    }

    @Test(expected = JsonMappingException.class)
    public void t1() throws IOException {
        String s = "{\"/data\": {} }";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.readValue(s, VolumeBinds.class);
    }
}

