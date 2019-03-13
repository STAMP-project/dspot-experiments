/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.webapp;


import JavaVersion.JAVA_TARGET_PLATFORM;
import WebInfConfiguration.CONTAINER_JAR_PATTERN;
import java.util.List;
import org.eclipse.jetty.util.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;


/**
 * WebInfConfigurationTest
 */
public class WebInfConfigurationTest {
    /**
     * Assume target < jdk9. In this case, we should be able to extract
     * the urls from the application classloader, and we should not look
     * at the java.class.path property.
     *
     * @throws Exception
     * 		
     */
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    public void testFindAndFilterContainerPaths() throws Exception {
        WebInfConfiguration config = new WebInfConfiguration();
        WebAppContext context = new WebAppContext();
        context.setAttribute(CONTAINER_JAR_PATTERN, ".*/jetty-util-[^/]*\\.jar$|.*/jetty-util/target/classes/");
        WebAppClassLoader loader = new WebAppClassLoader(context);
        context.setClassLoader(loader);
        config.findAndFilterContainerPaths(context);
        List<Resource> containerResources = context.getMetaData().getContainerResources();
        Assertions.assertEquals(1, containerResources.size());
        MatcherAssert.assertThat(containerResources.get(0).toString(), Matchers.containsString("jetty-util"));
    }

    /**
     * Assume target jdk9 or above. In this case we should extract what we need
     * from the java.class.path. We should also examine the module path.
     *
     * @throws Exception
     * 		
     */
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    @EnabledIfSystemProperty(named = "jdk.module.path", matches = ".*")
    public void testFindAndFilterContainerPathsJDK9() throws Exception {
        WebInfConfiguration config = new WebInfConfiguration();
        WebAppContext context = new WebAppContext();
        context.setAttribute(CONTAINER_JAR_PATTERN, ".*/jetty-util-[^/]*\\.jar$|.*/jetty-util/target/classes/$|.*/foo-bar-janb.jar");
        WebAppClassLoader loader = new WebAppClassLoader(context);
        context.setClassLoader(loader);
        config.findAndFilterContainerPaths(context);
        List<Resource> containerResources = context.getMetaData().getContainerResources();
        Assertions.assertEquals(2, containerResources.size());
        for (Resource r : containerResources) {
            String s = r.toString();
            MatcherAssert.assertThat(s, Matchers.anyOf(Matchers.endsWith("foo-bar-janb.jar"), Matchers.containsString("jetty-util")));
        }
    }

    /**
     * Assume runtime is jdk9 or above. Target is jdk 8. In this
     * case we must extract from the java.class.path (because jdk 9
     * has no url based application classloader), but we should
     * ignore the module path.
     *
     * @throws Exception
     * 		
     */
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    @EnabledIfSystemProperty(named = "jdk.module.path", matches = ".*")
    public void testFindAndFilterContainerPathsTarget8() throws Exception {
        WebInfConfiguration config = new WebInfConfiguration();
        WebAppContext context = new WebAppContext();
        context.setAttribute(JAVA_TARGET_PLATFORM, "8");
        context.setAttribute(CONTAINER_JAR_PATTERN, ".*/jetty-util-[^/]*\\.jar$|.*/jetty-util/target/classes/$|.*/foo-bar-janb.jar");
        WebAppClassLoader loader = new WebAppClassLoader(context);
        context.setClassLoader(loader);
        config.findAndFilterContainerPaths(context);
        List<Resource> containerResources = context.getMetaData().getContainerResources();
        Assertions.assertEquals(1, containerResources.size());
        MatcherAssert.assertThat(containerResources.get(0).toString(), Matchers.containsString("jetty-util"));
    }
}

