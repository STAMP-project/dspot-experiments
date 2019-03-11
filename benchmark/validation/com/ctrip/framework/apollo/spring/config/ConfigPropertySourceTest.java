package com.ctrip.framework.apollo.spring.config;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class ConfigPropertySourceTest {
    private ConfigPropertySource configPropertySource;

    @Mock
    private Config someConfig;

    @Test
    public void testGetPropertyNames() throws Exception {
        String somePropertyName = "somePropertyName";
        String anotherPropertyName = "anotherPropertyName";
        Set<String> somePropertyNames = Sets.newHashSet(somePropertyName, anotherPropertyName);
        Mockito.when(someConfig.getPropertyNames()).thenReturn(somePropertyNames);
        String[] result = configPropertySource.getPropertyNames();
        Mockito.verify(someConfig, Mockito.times(1)).getPropertyNames();
        Assert.assertArrayEquals(somePropertyNames.toArray(), result);
    }

    @Test
    public void testGetEmptyPropertyNames() throws Exception {
        Mockito.when(someConfig.getPropertyNames()).thenReturn(Sets.<String>newHashSet());
        Assert.assertEquals(0, configPropertySource.getPropertyNames().length);
    }

    @Test
    public void testGetProperty() throws Exception {
        String somePropertyName = "somePropertyName";
        String someValue = "someValue";
        Mockito.when(someConfig.getProperty(somePropertyName, null)).thenReturn(someValue);
        Assert.assertEquals(someValue, configPropertySource.getProperty(somePropertyName));
        Mockito.verify(someConfig, Mockito.times(1)).getProperty(somePropertyName, null);
    }

    @Test
    public void testAddChangeListener() throws Exception {
        ConfigChangeListener someListener = Mockito.mock(ConfigChangeListener.class);
        ConfigChangeListener anotherListener = Mockito.mock(ConfigChangeListener.class);
        final List<ConfigChangeListener> listeners = Lists.newArrayList();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listeners.add(getArgumentAt(0, ConfigChangeListener.class));
                return Void.class;
            }
        }).when(someConfig).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class));
        configPropertySource.addChangeListener(someListener);
        configPropertySource.addChangeListener(anotherListener);
        Assert.assertEquals(2, listeners.size());
        Assert.assertTrue(listeners.containsAll(Lists.newArrayList(someListener, anotherListener)));
    }
}

