package org.reflections;


import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


/**
 *
 */
public class ReflectionsCollectTest extends ReflectionsTest {
    @Test
    public void testResourcesScanner() {
        Predicate<String> filter = new FilterBuilder().include(".*\\.xml").include(".*\\.json");
        Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter).setScanners(new ResourcesScanner()).setUrls(Arrays.asList(ClasspathHelper.forClass(TestModel.class))));
        Set<String> resolved = reflections.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
        Assert.assertThat(resolved, ReflectionsTest.are("META-INF/reflections/resource1-reflections.xml"));
        Set<String> resources = reflections.getStore().get(index(ResourcesScanner.class)).keySet();
        Assert.assertThat(resources, ReflectionsTest.are("resource1-reflections.xml", "resource2-reflections.xml", "testModel-reflections.xml", "testModel-reflections.json"));
    }
}

