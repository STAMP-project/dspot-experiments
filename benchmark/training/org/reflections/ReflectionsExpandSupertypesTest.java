package org.reflections;


import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


public class ReflectionsExpandSupertypesTest {
    private static final String packagePrefix = "org.reflections.ReflectionsExpandSupertypesTest\\$TestModel\\$ScannedScope\\$.*";

    private FilterBuilder inputsFilter = new FilterBuilder().include(ReflectionsExpandSupertypesTest.packagePrefix);

    public interface TestModel {
        interface A {}

        interface B extends ReflectionsExpandSupertypesTest.TestModel.A {}

        interface ScannedScope {
            interface C extends ReflectionsExpandSupertypesTest.TestModel.B {}

            interface D extends ReflectionsExpandSupertypesTest.TestModel.B {}
        }
    }

    @Test
    public void testExpandSupertypes() throws Exception {
        Reflections refExpand = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClass(ReflectionsExpandSupertypesTest.TestModel.ScannedScope.C.class)).filterInputsBy(inputsFilter));
        Assert.assertTrue(refExpand.getConfiguration().shouldExpandSuperTypes());
        Set<Class<? extends ReflectionsExpandSupertypesTest.TestModel.A>> subTypesOf = refExpand.getSubTypesOf(ReflectionsExpandSupertypesTest.TestModel.A.class);
        Assert.assertTrue("expanded", subTypesOf.contains(ReflectionsExpandSupertypesTest.TestModel.B.class));
        Assert.assertTrue("transitivity", subTypesOf.containsAll(refExpand.getSubTypesOf(ReflectionsExpandSupertypesTest.TestModel.B.class)));
    }

    @Test
    public void testNotExpandSupertypes() throws Exception {
        Reflections refDontExpand = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClass(ReflectionsExpandSupertypesTest.TestModel.ScannedScope.C.class)).filterInputsBy(inputsFilter).setExpandSuperTypes(false));
        Assert.assertFalse(refDontExpand.getConfiguration().shouldExpandSuperTypes());
        Set<Class<? extends ReflectionsExpandSupertypesTest.TestModel.A>> subTypesOf1 = refDontExpand.getSubTypesOf(ReflectionsExpandSupertypesTest.TestModel.A.class);
        Assert.assertFalse(subTypesOf1.contains(ReflectionsExpandSupertypesTest.TestModel.B.class));
    }
}

