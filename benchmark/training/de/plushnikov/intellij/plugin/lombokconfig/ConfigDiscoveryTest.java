package de.plushnikov.intellij.plugin.lombokconfig;


import ConfigKey.ACCESSORS_CHAIN;
import ConfigKey.CONFIG_STOP_BUBBLING;
import LombokConfigIndex.NAME;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static ConfigKey.ACCESSORS_CHAIN;
import static ConfigKey.ACCESSORS_PREFIX;


@RunWith(MockitoJUnitRunner.class)
public class ConfigDiscoveryTest {
    private static final String EXPECTED_VALUE = "xyz";

    private ConfigDiscovery discovery;

    @Mock
    private FileBasedIndex fileBasedIndex;

    @Mock
    private GlobalSearchScope globalSearchScope;

    @Mock
    private MyProject project;

    @Mock
    private PsiClass psiClass;

    @Mock
    private PsiFile psiFile;

    @Mock
    private VirtualFile virtualFile;

    @Mock
    private VirtualFile parentVirtualFile;

    @Test
    public void testDefaultStringConfigProperties() {
        final String property = discovery.getStringLombokConfigProperty(ACCESSORS_CHAIN, psiClass);
        Assert.assertNotNull(property);
        Assert.assertEquals(ACCESSORS_CHAIN.getConfigDefaultValue(), property);
    }

    @Test
    public void testStringConfigPropertySameDirectory() {
        final ConfigKey configKey = ACCESSORS_CHAIN;
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e/f", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList(ConfigDiscoveryTest.EXPECTED_VALUE));
        final String property = discovery.getStringLombokConfigProperty(configKey, psiClass);
        Assert.assertNotNull(property);
        Assert.assertEquals(ConfigDiscoveryTest.EXPECTED_VALUE, property);
    }

    @Test
    public void testStringConfigPropertySubDirectory() {
        final ConfigKey configKey = ACCESSORS_CHAIN;
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList(ConfigDiscoveryTest.EXPECTED_VALUE));
        final String property = discovery.getStringLombokConfigProperty(configKey, psiClass);
        Assert.assertNotNull(property);
        Assert.assertEquals(ConfigDiscoveryTest.EXPECTED_VALUE, property);
    }

    @Test
    public void testStringConfigPropertySubDirectoryStopBubling() {
        final ConfigKey configKey = ACCESSORS_CHAIN;
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e/f", CONFIG_STOP_BUBBLING.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("true"));
        final String property = discovery.getStringLombokConfigProperty(configKey, psiClass);
        Assert.assertNotNull(property);
        Assert.assertEquals(configKey.getConfigDefaultValue(), property);
    }

    @Test
    public void testMultipleStringConfigProperty() {
        final ConfigKey configKey = ACCESSORS_PREFIX;
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("+a;+b"));
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("-a;+cc"));
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.emptyList());
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e/f", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("+_d;"));
        final String[] properties = discovery.getMultipleValueLombokConfigProperty(configKey, psiClass);
        Assert.assertNotNull(properties);
        Assert.assertEquals(3, properties.length);
        final ArrayList<String> list = new ArrayList<>(Arrays.asList(properties));
        Assert.assertTrue(list.contains("b"));
        Assert.assertTrue(list.contains("cc"));
        Assert.assertTrue(list.contains("_d"));
    }

    @Test
    public void testMultipleStringConfigPropertyWithStopBubbling() {
        final ConfigKey configKey = ACCESSORS_PREFIX;
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e", CONFIG_STOP_BUBBLING.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("true"));
        Mockito.when(fileBasedIndex.getValues(NAME, new ConfigIndexKey("/a/b/c/d/e/f", configKey.getConfigKey()), globalSearchScope)).thenReturn(Collections.singletonList("+_d;"));
        final String[] properties = discovery.getMultipleValueLombokConfigProperty(configKey, psiClass);
        Assert.assertNotNull(properties);
        Assert.assertEquals(1, properties.length);
        final ArrayList<String> list = new ArrayList<>(Arrays.asList(properties));
        Assert.assertTrue(list.contains("_d"));
    }
}

