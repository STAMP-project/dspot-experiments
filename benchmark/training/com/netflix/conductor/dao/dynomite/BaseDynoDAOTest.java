package com.netflix.conductor.dao.dynomite;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.dyno.DynoProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BaseDynoDAOTest {
    @Mock
    private DynoProxy dynoClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Configuration config;

    private BaseDynoDAO baseDynoDAO;

    @Test
    public void testNsKey() {
        Assert.assertEquals("", baseDynoDAO.nsKey());
        String[] keys = new String[]{ "key1", "key2" };
        Assert.assertEquals("key1.key2", baseDynoDAO.nsKey(keys));
        Mockito.when(config.getProperty("workflow.namespace.prefix", null)).thenReturn("test");
        Assert.assertEquals("test", baseDynoDAO.nsKey());
        Assert.assertEquals("test.key1.key2", baseDynoDAO.nsKey(keys));
        Mockito.when(config.getStack()).thenReturn("stack");
        Assert.assertEquals("test.stack.key1.key2", baseDynoDAO.nsKey(keys));
    }
}

