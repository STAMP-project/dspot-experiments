/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.context.support;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.ContextLoader;


/**
 * Unit tests for {@link ContextLoaderUtils} involving context hierarchies.
 *
 * @author Sam Brannen
 * @since 3.2.2
 */
public class ContextLoaderUtilsContextHierarchyTests extends AbstractContextConfigurationUtilsTests {
    @Test(expected = IllegalStateException.class)
    public void resolveContextHierarchyAttributesForSingleTestClassWithContextConfigurationAndContextHierarchy() {
        resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithContextConfigurationAndContextHierarchy.class);
    }

    @Test(expected = IllegalStateException.class)
    public void resolveContextHierarchyAttributesForSingleTestClassWithContextConfigurationAndContextHierarchyOnSingleMetaAnnotation() {
        resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithContextConfigurationAndContextHierarchyOnSingleMetaAnnotation.class);
    }

    @Test
    public void resolveContextHierarchyAttributesForSingleTestClassWithImplicitSingleLevelContextHierarchy() {
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(AbstractContextConfigurationUtilsTests.BareAnnotations.class);
        Assert.assertEquals(1, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesList = hierarchyAttributes.get(0);
        Assert.assertEquals(1, configAttributesList.size());
        debugConfigAttributes(configAttributesList);
    }

    @Test
    public void resolveContextHierarchyAttributesForSingleTestClassWithSingleLevelContextHierarchy() {
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithSingleLevelContextHierarchy.class);
        Assert.assertEquals(1, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesList = hierarchyAttributes.get(0);
        Assert.assertEquals(1, configAttributesList.size());
        debugConfigAttributes(configAttributesList);
    }

    @Test
    public void resolveContextHierarchyAttributesForSingleTestClassWithSingleLevelContextHierarchyFromMetaAnnotation() {
        Class<ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithSingleLevelContextHierarchyFromMetaAnnotation> testClass = ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithSingleLevelContextHierarchyFromMetaAnnotation.class;
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(testClass);
        Assert.assertEquals(1, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesList = hierarchyAttributes.get(0);
        Assert.assertNotNull(configAttributesList);
        Assert.assertEquals(1, configAttributesList.size());
        debugConfigAttributes(configAttributesList);
        assertAttributes(configAttributesList.get(0), testClass, new String[]{ "A.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
    }

    @Test
    public void resolveContextHierarchyAttributesForSingleTestClassWithTripleLevelContextHierarchy() {
        Class<ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithTripleLevelContextHierarchy> testClass = ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithTripleLevelContextHierarchy.class;
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(testClass);
        Assert.assertEquals(1, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesList = hierarchyAttributes.get(0);
        Assert.assertNotNull(configAttributesList);
        Assert.assertEquals(3, configAttributesList.size());
        debugConfigAttributes(configAttributesList);
        assertAttributes(configAttributesList.get(0), testClass, new String[]{ "A.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
        assertAttributes(configAttributesList.get(1), testClass, new String[]{ "B.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
        assertAttributes(configAttributesList.get(2), testClass, new String[]{ "C.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithSingleLevelContextHierarchies() {
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass3WithSingleLevelContextHierarchy.class);
        Assert.assertEquals(3, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesListClassLevel1 = hierarchyAttributes.get(0);
        debugConfigAttributes(configAttributesListClassLevel1);
        Assert.assertEquals(1, configAttributesListClassLevel1.size());
        Assert.assertThat(configAttributesListClassLevel1.get(0).getLocations()[0], CoreMatchers.equalTo("one.xml"));
        List<ContextConfigurationAttributes> configAttributesListClassLevel2 = hierarchyAttributes.get(1);
        debugConfigAttributes(configAttributesListClassLevel2);
        Assert.assertEquals(1, configAttributesListClassLevel2.size());
        Assert.assertArrayEquals(new String[]{ "two-A.xml", "two-B.xml" }, configAttributesListClassLevel2.get(0).getLocations());
        List<ContextConfigurationAttributes> configAttributesListClassLevel3 = hierarchyAttributes.get(2);
        debugConfigAttributes(configAttributesListClassLevel3);
        Assert.assertEquals(1, configAttributesListClassLevel3.size());
        Assert.assertThat(configAttributesListClassLevel3.get(0).getLocations()[0], CoreMatchers.equalTo("three.xml"));
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithSingleLevelContextHierarchiesAndMetaAnnotations() {
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass3WithSingleLevelContextHierarchyFromMetaAnnotation.class);
        Assert.assertEquals(3, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesListClassLevel1 = hierarchyAttributes.get(0);
        debugConfigAttributes(configAttributesListClassLevel1);
        Assert.assertEquals(1, configAttributesListClassLevel1.size());
        Assert.assertThat(configAttributesListClassLevel1.get(0).getLocations()[0], CoreMatchers.equalTo("A.xml"));
        assertAttributes(configAttributesListClassLevel1.get(0), ContextLoaderUtilsContextHierarchyTests.TestClass1WithSingleLevelContextHierarchyFromMetaAnnotation.class, new String[]{ "A.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
        List<ContextConfigurationAttributes> configAttributesListClassLevel2 = hierarchyAttributes.get(1);
        debugConfigAttributes(configAttributesListClassLevel2);
        Assert.assertEquals(1, configAttributesListClassLevel2.size());
        Assert.assertArrayEquals(new String[]{ "B-one.xml", "B-two.xml" }, configAttributesListClassLevel2.get(0).getLocations());
        assertAttributes(configAttributesListClassLevel2.get(0), ContextLoaderUtilsContextHierarchyTests.TestClass2WithSingleLevelContextHierarchyFromMetaAnnotation.class, new String[]{ "B-one.xml", "B-two.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
        List<ContextConfigurationAttributes> configAttributesListClassLevel3 = hierarchyAttributes.get(2);
        debugConfigAttributes(configAttributesListClassLevel3);
        Assert.assertEquals(1, configAttributesListClassLevel3.size());
        Assert.assertThat(configAttributesListClassLevel3.get(0).getLocations()[0], CoreMatchers.equalTo("C.xml"));
        assertAttributes(configAttributesListClassLevel3.get(0), ContextLoaderUtilsContextHierarchyTests.TestClass3WithSingleLevelContextHierarchyFromMetaAnnotation.class, new String[]{ "C.xml" }, AbstractContextConfigurationUtilsTests.EMPTY_CLASS_ARRAY, ContextLoader.class, true);
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithBareContextConfigurationInSuperclass() {
        assertOneTwo(resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass2WithBareContextConfigurationInSuperclass.class));
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithBareContextConfigurationInSubclass() {
        assertOneTwo(resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass2WithBareContextConfigurationInSubclass.class));
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithBareMetaContextConfigWithOverridesInSuperclass() {
        assertOneTwo(resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass2WithBareMetaContextConfigWithOverridesInSuperclass.class));
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithBareMetaContextConfigWithOverridesInSubclass() {
        assertOneTwo(resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass2WithBareMetaContextConfigWithOverridesInSubclass.class));
    }

    @Test
    public void resolveContextHierarchyAttributesForTestClassHierarchyWithMultiLevelContextHierarchies() {
        List<List<ContextConfigurationAttributes>> hierarchyAttributes = resolveContextHierarchyAttributes(ContextLoaderUtilsContextHierarchyTests.TestClass3WithMultiLevelContextHierarchy.class);
        Assert.assertEquals(3, hierarchyAttributes.size());
        List<ContextConfigurationAttributes> configAttributesListClassLevel1 = hierarchyAttributes.get(0);
        debugConfigAttributes(configAttributesListClassLevel1);
        Assert.assertEquals(2, configAttributesListClassLevel1.size());
        Assert.assertThat(configAttributesListClassLevel1.get(0).getLocations()[0], CoreMatchers.equalTo("1-A.xml"));
        Assert.assertThat(configAttributesListClassLevel1.get(1).getLocations()[0], CoreMatchers.equalTo("1-B.xml"));
        List<ContextConfigurationAttributes> configAttributesListClassLevel2 = hierarchyAttributes.get(1);
        debugConfigAttributes(configAttributesListClassLevel2);
        Assert.assertEquals(2, configAttributesListClassLevel2.size());
        Assert.assertThat(configAttributesListClassLevel2.get(0).getLocations()[0], CoreMatchers.equalTo("2-A.xml"));
        Assert.assertThat(configAttributesListClassLevel2.get(1).getLocations()[0], CoreMatchers.equalTo("2-B.xml"));
        List<ContextConfigurationAttributes> configAttributesListClassLevel3 = hierarchyAttributes.get(2);
        debugConfigAttributes(configAttributesListClassLevel3);
        Assert.assertEquals(3, configAttributesListClassLevel3.size());
        Assert.assertThat(configAttributesListClassLevel3.get(0).getLocations()[0], CoreMatchers.equalTo("3-A.xml"));
        Assert.assertThat(configAttributesListClassLevel3.get(1).getLocations()[0], CoreMatchers.equalTo("3-B.xml"));
        Assert.assertThat(configAttributesListClassLevel3.get(2).getLocations()[0], CoreMatchers.equalTo("3-C.xml"));
    }

    @Test
    public void buildContextHierarchyMapForTestClassHierarchyWithMultiLevelContextHierarchies() {
        Map<String, List<ContextConfigurationAttributes>> map = buildContextHierarchyMap(ContextLoaderUtilsContextHierarchyTests.TestClass3WithMultiLevelContextHierarchy.class);
        Assert.assertThat(map.size(), CoreMatchers.is(3));
        Assert.assertThat(map.keySet(), CoreMatchers.hasItems("alpha", "beta", "gamma"));
        List<ContextConfigurationAttributes> alphaConfig = map.get("alpha");
        Assert.assertThat(alphaConfig.size(), CoreMatchers.is(3));
        Assert.assertThat(alphaConfig.get(0).getLocations()[0], CoreMatchers.is("1-A.xml"));
        Assert.assertThat(alphaConfig.get(1).getLocations()[0], CoreMatchers.is("2-A.xml"));
        Assert.assertThat(alphaConfig.get(2).getLocations()[0], CoreMatchers.is("3-A.xml"));
        List<ContextConfigurationAttributes> betaConfig = map.get("beta");
        Assert.assertThat(betaConfig.size(), CoreMatchers.is(3));
        Assert.assertThat(betaConfig.get(0).getLocations()[0], CoreMatchers.is("1-B.xml"));
        Assert.assertThat(betaConfig.get(1).getLocations()[0], CoreMatchers.is("2-B.xml"));
        Assert.assertThat(betaConfig.get(2).getLocations()[0], CoreMatchers.is("3-B.xml"));
        List<ContextConfigurationAttributes> gammaConfig = map.get("gamma");
        Assert.assertThat(gammaConfig.size(), CoreMatchers.is(1));
        Assert.assertThat(gammaConfig.get(0).getLocations()[0], CoreMatchers.is("3-C.xml"));
    }

    @Test
    public void buildContextHierarchyMapForTestClassHierarchyWithMultiLevelContextHierarchiesAndUnnamedConfig() {
        Map<String, List<ContextConfigurationAttributes>> map = buildContextHierarchyMap(ContextLoaderUtilsContextHierarchyTests.TestClass3WithMultiLevelContextHierarchyAndUnnamedConfig.class);
        String level1 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 1;
        String level2 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 2;
        String level3 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 3;
        String level4 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 4;
        String level5 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 5;
        String level6 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 6;
        String level7 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 7;
        Assert.assertThat(map.size(), CoreMatchers.is(7));
        Assert.assertThat(map.keySet(), CoreMatchers.hasItems(level1, level2, level3, level4, level5, level6, level7));
        List<ContextConfigurationAttributes> level1Config = map.get(level1);
        Assert.assertThat(level1Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level1Config.get(0).getLocations()[0], CoreMatchers.is("1-A.xml"));
        List<ContextConfigurationAttributes> level2Config = map.get(level2);
        Assert.assertThat(level2Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level2Config.get(0).getLocations()[0], CoreMatchers.is("1-B.xml"));
        List<ContextConfigurationAttributes> level3Config = map.get(level3);
        Assert.assertThat(level3Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level3Config.get(0).getLocations()[0], CoreMatchers.is("2-A.xml"));
        // ...
        List<ContextConfigurationAttributes> level7Config = map.get(level7);
        Assert.assertThat(level7Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level7Config.get(0).getLocations()[0], CoreMatchers.is("3-C.xml"));
    }

    @Test
    public void buildContextHierarchyMapForTestClassHierarchyWithMultiLevelContextHierarchiesAndPartiallyNamedConfig() {
        Map<String, List<ContextConfigurationAttributes>> map = buildContextHierarchyMap(ContextLoaderUtilsContextHierarchyTests.TestClass2WithMultiLevelContextHierarchyAndPartiallyNamedConfig.class);
        String level1 = "parent";
        String level2 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 2;
        String level3 = (GENERATED_CONTEXT_HIERARCHY_LEVEL_PREFIX) + 3;
        Assert.assertThat(map.size(), CoreMatchers.is(3));
        Assert.assertThat(map.keySet(), CoreMatchers.hasItems(level1, level2, level3));
        Iterator<String> levels = map.keySet().iterator();
        Assert.assertThat(levels.next(), CoreMatchers.is(level1));
        Assert.assertThat(levels.next(), CoreMatchers.is(level2));
        Assert.assertThat(levels.next(), CoreMatchers.is(level3));
        List<ContextConfigurationAttributes> level1Config = map.get(level1);
        Assert.assertThat(level1Config.size(), CoreMatchers.is(2));
        Assert.assertThat(level1Config.get(0).getLocations()[0], CoreMatchers.is("1-A.xml"));
        Assert.assertThat(level1Config.get(1).getLocations()[0], CoreMatchers.is("2-A.xml"));
        List<ContextConfigurationAttributes> level2Config = map.get(level2);
        Assert.assertThat(level2Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level2Config.get(0).getLocations()[0], CoreMatchers.is("1-B.xml"));
        List<ContextConfigurationAttributes> level3Config = map.get(level3);
        Assert.assertThat(level3Config.size(), CoreMatchers.is(1));
        Assert.assertThat(level3Config.get(0).getLocations()[0], CoreMatchers.is("2-C.xml"));
    }

    @Test
    public void buildContextHierarchyMapForSingleTestClassWithMultiLevelContextHierarchyWithEmptyContextConfig() {
        assertContextConfigEntriesAreNotUnique(ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithMultiLevelContextHierarchyWithEmptyContextConfig.class);
    }

    @Test
    public void buildContextHierarchyMapForSingleTestClassWithMultiLevelContextHierarchyWithDuplicatedContextConfig() {
        assertContextConfigEntriesAreNotUnique(ContextLoaderUtilsContextHierarchyTests.SingleTestClassWithMultiLevelContextHierarchyWithDuplicatedContextConfig.class);
    }

    /**
     * Used to reproduce bug reported in https://jira.spring.io/browse/SPR-10997
     */
    @Test
    public void buildContextHierarchyMapForTestClassHierarchyWithMultiLevelContextHierarchiesAndOverriddenInitializers() {
        Map<String, List<ContextConfigurationAttributes>> map = buildContextHierarchyMap(ContextLoaderUtilsContextHierarchyTests.TestClass2WithMultiLevelContextHierarchyWithOverriddenInitializers.class);
        Assert.assertThat(map.size(), CoreMatchers.is(2));
        Assert.assertThat(map.keySet(), CoreMatchers.hasItems("alpha", "beta"));
        List<ContextConfigurationAttributes> alphaConfig = map.get("alpha");
        Assert.assertThat(alphaConfig.size(), CoreMatchers.is(2));
        Assert.assertThat(alphaConfig.get(0).getLocations().length, CoreMatchers.is(1));
        Assert.assertThat(alphaConfig.get(0).getLocations()[0], CoreMatchers.is("1-A.xml"));
        Assert.assertThat(alphaConfig.get(0).getInitializers().length, CoreMatchers.is(0));
        Assert.assertThat(alphaConfig.get(1).getLocations().length, CoreMatchers.is(0));
        Assert.assertThat(alphaConfig.get(1).getInitializers().length, CoreMatchers.is(1));
        Assert.assertEquals(ContextLoaderUtilsContextHierarchyTests.DummyApplicationContextInitializer.class, alphaConfig.get(1).getInitializers()[0]);
        List<ContextConfigurationAttributes> betaConfig = map.get("beta");
        Assert.assertThat(betaConfig.size(), CoreMatchers.is(2));
        Assert.assertThat(betaConfig.get(0).getLocations().length, CoreMatchers.is(1));
        Assert.assertThat(betaConfig.get(0).getLocations()[0], CoreMatchers.is("1-B.xml"));
        Assert.assertThat(betaConfig.get(0).getInitializers().length, CoreMatchers.is(0));
        Assert.assertThat(betaConfig.get(1).getLocations().length, CoreMatchers.is(0));
        Assert.assertThat(betaConfig.get(1).getInitializers().length, CoreMatchers.is(1));
        Assert.assertEquals(ContextLoaderUtilsContextHierarchyTests.DummyApplicationContextInitializer.class, betaConfig.get(1).getInitializers()[0]);
    }

    // -------------------------------------------------------------------------
    @ContextConfiguration("foo.xml")
    @ContextHierarchy(@ContextConfiguration("bar.xml"))
    private static class SingleTestClassWithContextConfigurationAndContextHierarchy {}

    @ContextConfiguration("foo.xml")
    @ContextHierarchy(@ContextConfiguration("bar.xml"))
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ContextConfigurationAndContextHierarchyOnSingleMeta {}

    @ContextLoaderUtilsContextHierarchyTests.ContextConfigurationAndContextHierarchyOnSingleMeta
    private static class SingleTestClassWithContextConfigurationAndContextHierarchyOnSingleMetaAnnotation {}

    @ContextHierarchy(@ContextConfiguration("A.xml"))
    private static class SingleTestClassWithSingleLevelContextHierarchy {}

    @ContextHierarchy({ @ContextConfiguration("A.xml"), @ContextConfiguration("B.xml"), @ContextConfiguration("C.xml") })
    private static class SingleTestClassWithTripleLevelContextHierarchy {}

    @ContextHierarchy(@ContextConfiguration("one.xml"))
    private static class TestClass1WithSingleLevelContextHierarchy {}

    @ContextHierarchy(@ContextConfiguration({ "two-A.xml", "two-B.xml" }))
    private static class TestClass2WithSingleLevelContextHierarchy extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithSingleLevelContextHierarchy {}

    @ContextHierarchy(@ContextConfiguration("three.xml"))
    private static class TestClass3WithSingleLevelContextHierarchy extends ContextLoaderUtilsContextHierarchyTests.TestClass2WithSingleLevelContextHierarchy {}

    @ContextConfiguration("one.xml")
    private static class TestClass1WithBareContextConfigurationInSuperclass {}

    @ContextHierarchy(@ContextConfiguration("two.xml"))
    private static class TestClass2WithBareContextConfigurationInSuperclass extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithBareContextConfigurationInSuperclass {}

    @ContextHierarchy(@ContextConfiguration("one.xml"))
    private static class TestClass1WithBareContextConfigurationInSubclass {}

    @ContextConfiguration("two.xml")
    private static class TestClass2WithBareContextConfigurationInSubclass extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithBareContextConfigurationInSubclass {}

    @ContextHierarchy({ @ContextConfiguration(locations = "1-A.xml", name = "alpha"), @ContextConfiguration(locations = "1-B.xml", name = "beta") })
    private static class TestClass1WithMultiLevelContextHierarchy {}

    @ContextHierarchy({ @ContextConfiguration(locations = "2-A.xml", name = "alpha"), @ContextConfiguration(locations = "2-B.xml", name = "beta") })
    private static class TestClass2WithMultiLevelContextHierarchy extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithMultiLevelContextHierarchy {}

    @ContextHierarchy({ @ContextConfiguration(locations = "3-A.xml", name = "alpha"), @ContextConfiguration(locations = "3-B.xml", name = "beta"), @ContextConfiguration(locations = "3-C.xml", name = "gamma") })
    private static class TestClass3WithMultiLevelContextHierarchy extends ContextLoaderUtilsContextHierarchyTests.TestClass2WithMultiLevelContextHierarchy {}

    @ContextHierarchy({ @ContextConfiguration(locations = "1-A.xml"), @ContextConfiguration(locations = "1-B.xml") })
    private static class TestClass1WithMultiLevelContextHierarchyAndUnnamedConfig {}

    @ContextHierarchy({ @ContextConfiguration(locations = "2-A.xml"), @ContextConfiguration(locations = "2-B.xml") })
    private static class TestClass2WithMultiLevelContextHierarchyAndUnnamedConfig extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithMultiLevelContextHierarchyAndUnnamedConfig {}

    @ContextHierarchy({ @ContextConfiguration(locations = "3-A.xml"), @ContextConfiguration(locations = "3-B.xml"), @ContextConfiguration(locations = "3-C.xml") })
    private static class TestClass3WithMultiLevelContextHierarchyAndUnnamedConfig extends ContextLoaderUtilsContextHierarchyTests.TestClass2WithMultiLevelContextHierarchyAndUnnamedConfig {}

    @ContextHierarchy({ @ContextConfiguration(locations = "1-A.xml", name = "parent"), @ContextConfiguration(locations = "1-B.xml") })
    private static class TestClass1WithMultiLevelContextHierarchyAndPartiallyNamedConfig {}

    @ContextHierarchy({ @ContextConfiguration(locations = "2-A.xml", name = "parent"), @ContextConfiguration(locations = "2-C.xml") })
    private static class TestClass2WithMultiLevelContextHierarchyAndPartiallyNamedConfig extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithMultiLevelContextHierarchyAndPartiallyNamedConfig {}

    @ContextHierarchy({ @ContextConfiguration, @ContextConfiguration })
    private static class SingleTestClassWithMultiLevelContextHierarchyWithEmptyContextConfig {}

    @ContextHierarchy({ @ContextConfiguration("foo.xml"), @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.BarConfig.class), @ContextConfiguration("baz.xml"), @ContextConfiguration(classes = AbstractContextConfigurationUtilsTests.BarConfig.class), @ContextConfiguration(loader = AnnotationConfigContextLoader.class) })
    private static class SingleTestClassWithMultiLevelContextHierarchyWithDuplicatedContextConfig {}

    /**
     * Used to reproduce bug reported in https://jira.spring.io/browse/SPR-10997
     */
    @ContextHierarchy({ @ContextConfiguration(name = "alpha", locations = "1-A.xml"), @ContextConfiguration(name = "beta", locations = "1-B.xml") })
    private static class TestClass1WithMultiLevelContextHierarchyWithUniqueContextConfig {}

    /**
     * Used to reproduce bug reported in https://jira.spring.io/browse/SPR-10997
     */
    @ContextHierarchy({ @ContextConfiguration(name = "alpha", initializers = ContextLoaderUtilsContextHierarchyTests.DummyApplicationContextInitializer.class), @ContextConfiguration(name = "beta", initializers = ContextLoaderUtilsContextHierarchyTests.DummyApplicationContextInitializer.class) })
    private static class TestClass2WithMultiLevelContextHierarchyWithOverriddenInitializers extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithMultiLevelContextHierarchyWithUniqueContextConfig {}

    /**
     * Used to reproduce bug reported in https://jira.spring.io/browse/SPR-10997
     */
    private static class DummyApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            /* no-op */
        }
    }

    // -------------------------------------------------------------------------
    @ContextHierarchy(@ContextConfiguration("A.xml"))
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ContextHierarchyA {}

    @ContextHierarchy(@ContextConfiguration({ "B-one.xml", "B-two.xml" }))
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ContextHierarchyB {}

    @ContextHierarchy(@ContextConfiguration("C.xml"))
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ContextHierarchyC {}

    @ContextLoaderUtilsContextHierarchyTests.ContextHierarchyA
    private static class SingleTestClassWithSingleLevelContextHierarchyFromMetaAnnotation {}

    @ContextLoaderUtilsContextHierarchyTests.ContextHierarchyA
    private static class TestClass1WithSingleLevelContextHierarchyFromMetaAnnotation {}

    @ContextLoaderUtilsContextHierarchyTests.ContextHierarchyB
    private static class TestClass2WithSingleLevelContextHierarchyFromMetaAnnotation extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithSingleLevelContextHierarchyFromMetaAnnotation {}

    @ContextLoaderUtilsContextHierarchyTests.ContextHierarchyC
    private static class TestClass3WithSingleLevelContextHierarchyFromMetaAnnotation extends ContextLoaderUtilsContextHierarchyTests.TestClass2WithSingleLevelContextHierarchyFromMetaAnnotation {}

    // -------------------------------------------------------------------------
    @ContextConfiguration
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface ContextConfigWithOverrides {
        String[] locations() default "A.xml";
    }

    @ContextLoaderUtilsContextHierarchyTests.ContextConfigWithOverrides(locations = "one.xml")
    private static class TestClass1WithBareMetaContextConfigWithOverridesInSuperclass {}

    @ContextHierarchy(@ContextConfiguration(locations = "two.xml"))
    private static class TestClass2WithBareMetaContextConfigWithOverridesInSuperclass extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithBareMetaContextConfigWithOverridesInSuperclass {}

    @ContextHierarchy(@ContextConfiguration(locations = "one.xml"))
    private static class TestClass1WithBareMetaContextConfigWithOverridesInSubclass {}

    @ContextLoaderUtilsContextHierarchyTests.ContextConfigWithOverrides(locations = "two.xml")
    private static class TestClass2WithBareMetaContextConfigWithOverridesInSubclass extends ContextLoaderUtilsContextHierarchyTests.TestClass1WithBareMetaContextConfigWithOverridesInSubclass {}
}

