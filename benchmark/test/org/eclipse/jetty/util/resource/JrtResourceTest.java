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
package org.eclipse.jetty.util.resource;


import java.net.URI;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.TypeUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;


public class JrtResourceTest {
    private String testResURI = MavenTestingUtils.getTestResourcesDir().getAbsoluteFile().toURI().toASCIIString();

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testResourceFromUriForString() throws Exception {
        URI string_loc = TypeUtil.getLocationOfClass(String.class);
        Resource resource = Resource.newResource(string_loc);
        MatcherAssert.assertThat(resource.exists(), Matchers.is(true));
        MatcherAssert.assertThat(resource.isDirectory(), Matchers.is(false));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.greaterThan(0));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.is(((int) (resource.length()))));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.startsWith("W/\""));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.endsWith("-xxx\""));
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testResourceFromStringForString() throws Exception {
        URI string_loc = TypeUtil.getLocationOfClass(String.class);
        Resource resource = Resource.newResource(string_loc.toASCIIString());
        MatcherAssert.assertThat(resource.exists(), Matchers.is(true));
        MatcherAssert.assertThat(resource.isDirectory(), Matchers.is(false));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.greaterThan(0));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.is(((int) (resource.length()))));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.startsWith("W/\""));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.endsWith("-xxx\""));
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testResourceFromURLForString() throws Exception {
        URI string_loc = TypeUtil.getLocationOfClass(String.class);
        Resource resource = Resource.newResource(string_loc.toURL());
        MatcherAssert.assertThat(resource.exists(), Matchers.is(true));
        MatcherAssert.assertThat(resource.isDirectory(), Matchers.is(false));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.greaterThan(0));
        MatcherAssert.assertThat(IO.readBytes(resource.getInputStream()).length, Matchers.is(((int) (resource.length()))));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.startsWith("W/\""));
        MatcherAssert.assertThat(resource.getWeakETag("-xxx"), Matchers.endsWith("-xxx\""));
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testResourceModule() throws Exception {
        Resource resource = Resource.newResource("jrt:/java.base");
        MatcherAssert.assertThat(resource.exists(), Matchers.is(false));
        MatcherAssert.assertThat(resource.isDirectory(), Matchers.is(false));
        MatcherAssert.assertThat(resource.length(), Matchers.is((-1L)));
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testResourceAllModules() throws Exception {
        Resource resource = Resource.newResource("jrt:/");
        MatcherAssert.assertThat(resource.exists(), Matchers.is(false));
        MatcherAssert.assertThat(resource.isDirectory(), Matchers.is(false));
        MatcherAssert.assertThat(resource.length(), Matchers.is((-1L)));
    }
}

