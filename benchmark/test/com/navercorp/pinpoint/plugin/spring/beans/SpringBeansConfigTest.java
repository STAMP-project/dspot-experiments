/**
 * Copyright 2016 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.spring.beans;


import SpringBeansConfig.SPRING_BEANS_ANNOTATION;
import SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN;
import SpringBeansConfig.SPRING_BEANS_NAME_PATTERN;
import SpringBeansTargetScope.COMPONENT_SCAN;
import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

import static SpringBeansConfig.SPRING_BEANS_ANNOTATION_POSTFIX;
import static SpringBeansConfig.SPRING_BEANS_BASE_PACKAGES_POSTFIX;
import static SpringBeansConfig.SPRING_BEANS_CLASS_PATTERN_POSTFIX;
import static SpringBeansConfig.SPRING_BEANS_NAME_PATTERN_POSTFIX;
import static SpringBeansConfig.SPRING_BEANS_PREFIX;
import static SpringBeansConfig.SPRING_BEANS_SCOPE_POSTFIX;


/**
 *
 *
 * @author jaehong.kim
 */
public class SpringBeansConfigTest {
    @Test
    public void config() {
        Properties properties = new Properties();
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Repository");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        Assert.assertEquals(3, springBeansConfig.getTargets().size());
        Assert.assertEquals(COMPONENT_SCAN, springBeansConfig.getTarget(1).getScope());
        Assert.assertEquals(null, springBeansConfig.getTarget(1).getBasePackages());
        Assert.assertEquals(1, springBeansConfig.getTarget(1).getNamePatterns().size());
        Assert.assertEquals(1, springBeansConfig.getTarget(1).getAnnotations().size());
        Assert.assertEquals(null, springBeansConfig.getTarget(1).getClassPatterns());
        Assert.assertEquals(COMPONENT_SCAN, springBeansConfig.getTarget(2).getScope());
        Assert.assertEquals(null, springBeansConfig.getTarget(2).getBasePackages());
        Assert.assertEquals(null, springBeansConfig.getTarget(2).getNamePatterns());
        Assert.assertEquals(1, springBeansConfig.getTarget(2).getAnnotations().size());
        Assert.assertEquals(1, springBeansConfig.getTarget(2).getClassPatterns().size());
        Assert.assertEquals(COMPONENT_SCAN, springBeansConfig.getTarget(3).getScope());
        Assert.assertEquals(null, springBeansConfig.getTarget(3).getBasePackages());
        Assert.assertEquals(null, springBeansConfig.getTarget(3).getNamePatterns());
        Assert.assertEquals(1, springBeansConfig.getTarget(3).getAnnotations().size());
        Assert.assertEquals(null, springBeansConfig.getTarget(3).getClassPatterns());
    }

    @Test
    public void backwardCompatibility() {
        Properties properties = new Properties();
        properties.put(SPRING_BEANS_NAME_PATTERN, "");
        properties.put(SPRING_BEANS_CLASS_PATTERN, "com.navercorp.*");
        properties.put(SPRING_BEANS_ANNOTATION, "org.springframework.stereotype.Controller, org.springframework.stereotype.Servicem, org.springframework.stereotype.Repository");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Repository");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        // backward compatiblity.
        Assert.assertEquals(5, springBeansConfig.getTargets().size());
    }

    @Test
    public void empty() {
        Properties properties = new Properties();
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        // empty
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        // empty
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        // empty
        properties.put((((SPRING_BEANS_PREFIX) + 4) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((((SPRING_BEANS_PREFIX) + 4) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        // empty
        properties.put((((SPRING_BEANS_PREFIX) + 5) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((((SPRING_BEANS_PREFIX) + 5) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        properties.put((((SPRING_BEANS_PREFIX) + 6) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + 6) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        Assert.assertEquals(2, springBeansConfig.getTargets().size());
    }

    @Test
    public void pattern() {
        Properties properties = new Properties();
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "Target.*");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Controller");
        // normal
        properties.put((((SPRING_BEANS_PREFIX) + 6) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + 6) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        // old
        properties.put(SPRING_BEANS_NAME_PATTERN, "com.navercorp.*");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        Assert.assertEquals(3, springBeansConfig.getTargets().size());
    }

    @Test
    public void invalid() {
        Properties properties = new Properties();
        properties.put((((SPRING_BEANS_PREFIX) + 1) + "foo"), "Target.*");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + "bar"), "org.springframework.stereotype.Controller");
        // empty
        properties.put((("foo" + 2) + (SPRING_BEANS_NAME_PATTERN_POSTFIX)), "");
        properties.put((("bar" + 2) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "");
        // wrong number 1.
        properties.put((((SPRING_BEANS_PREFIX) + 6.12) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + 6.12) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        // wrong number 2.
        properties.put((((SPRING_BEANS_PREFIX) + "A") + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put((((SPRING_BEANS_PREFIX) + "A") + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        // not found number
        properties.put(((SPRING_BEANS_PREFIX) + (SPRING_BEANS_CLASS_PATTERN_POSTFIX)), "java.lang.String");
        properties.put(((SPRING_BEANS_PREFIX) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        Assert.assertEquals(0, springBeansConfig.getTargets().size());
    }

    @Test
    public void scope() {
        Properties properties = new Properties();
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_SCOPE_POSTFIX)), "component-scan");
        properties.put((((SPRING_BEANS_PREFIX) + 1) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_SCOPE_POSTFIX)), "component-scan");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_BASE_PACKAGES_POSTFIX)), "com.navercorp");
        properties.put((((SPRING_BEANS_PREFIX) + 2) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_SCOPE_POSTFIX)), "post-processor");
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_BASE_PACKAGES_POSTFIX)), "com.navercorp");
        properties.put((((SPRING_BEANS_PREFIX) + 3) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        properties.put((((SPRING_BEANS_PREFIX) + 4) + (SPRING_BEANS_SCOPE_POSTFIX)), "post-processor");
        properties.put((((SPRING_BEANS_PREFIX) + 4) + (SPRING_BEANS_ANNOTATION_POSTFIX)), "org.springframework.stereotype.Service");
        ProfilerConfig config = new DefaultProfilerConfig(properties);
        SpringBeansConfig springBeansConfig = new SpringBeansConfig(config);
        Assert.assertEquals(4, springBeansConfig.getTargets().size());
    }
}

