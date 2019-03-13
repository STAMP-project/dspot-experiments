/**
 * Copyright 2014 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.spring.beans.interceptor;


import SpringBeansConfig.SPRING_BEANS_NAME_PATTERN;
import SpringBeansTargetScope.COMPONENT_SCAN;
import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.plugin.spring.beans.SpringBeansConfig;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;


/**
 *
 *
 * @author Jongho Moon
 * @author jaehong.kim
 */
public class TargetBeanFilterTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testClassLoadedByBootClassLoader() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 3) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Repository");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        if ((String.class.getClassLoader()) != null) {
            logger.debug("String is not loaded by: {}. Skip test.", String.class.getClassLoader());
            return;
        }
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertEquals(false, filter.isTarget(COMPONENT_SCAN, "someBean", beanDefinition));
    }

    @Test
    public void empty() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        filter.addTransformed(String.class.getName());
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void beansNamePattern() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "A.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 3) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "B.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 4) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "C.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 5) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "antstyle:D*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 6) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "antstyle:E?");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 7) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "antstyle:F.A*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 8) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "antstyle:.G*");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "AAA", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "BBB", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "CCC", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "DDD", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "EEE", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "EE", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "F.AA", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, ".GG", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "GG", beanDefinition));
        filter.addTransformed(String.class.getName());
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target1", beanDefinition));
    }

    @Test
    public void classNamePattern() {
        assertClassNamePattern("antstyle:**");
        assertClassNamePattern("antstyle:java.*.String");
        assertClassNamePattern("antstyle:java.**.String");
        assertClassNamePattern("antstyle:java.*.*");
        assertClassNamePattern("antstyle:java.**");
        assertClassNamePattern("antstyle:**.String");
        assertClassNamePattern("antstyle:java.lang.S*");
        assertClassNamePattern("antstyle:java.lang.*");
        assertClassNamePattern("antstyle:java.lang.Strin?");
        assertClassNamePattern("java.*");
        assertClassNamePattern("regex:java.*");
        assertClassNamePattern("regex:java.*.String");
    }

    @Test
    public void annotation() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void target0() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void target1() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void target2() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void target3() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 3) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
    }

    @Test
    public void target4() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 3) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 3) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Foo", beanDefinition));
    }

    @Test
    public void target5() {
        Properties properties = new Properties();
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target1, Target2");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target0");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target1", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target2", beanDefinition));
    }

    @Test
    public void target6() {
        Properties properties = new Properties();
        properties.put(SPRING_BEANS_NAME_PATTERN, "foo");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target1, Target2");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 1) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target0");
        properties.put((((SpringBeansConfig.SPRING_BEANS_PREFIX) + 2) + (SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        TargetBeanFilter filter = TargetBeanFilter.of(config);
        filter.clear();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "foo", beanDefinition));
        Assert.assertFalse(filter.isTarget(COMPONENT_SCAN, "Target0", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target1", beanDefinition));
        Assert.assertTrue(filter.isTarget(COMPONENT_SCAN, "Target2", beanDefinition));
    }
}

