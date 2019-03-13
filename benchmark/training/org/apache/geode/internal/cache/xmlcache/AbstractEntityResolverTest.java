/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.xmlcache;


import java.util.ServiceLoader;
import org.apache.geode.internal.ClassPathLoader;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;


/**
 * Unit test for {@link PivotalEntityResolver} and {@link DefaultEntityResolver2}.
 */
public abstract class AbstractEntityResolverTest {
    /**
     * Assert that {@link PivotalEntityResolver} extends {@link DefaultEntityResolver2}.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testInstanceOfDefaultEntityResolver2() {
        Assert.assertTrue(DefaultEntityResolver2.class.isAssignableFrom(getEntityResolver().getClass()));
    }

    /**
     * Find the {@link PivotalEntityResolver} in the {@link ClassPathLoader}. Verifies that the
     * META-INF/services file is correctly found and the the implementation class is loadable.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testDiscovery() {
        boolean found = false;
        final ServiceLoader<EntityResolver2> entityResolvers = ServiceLoader.load(EntityResolver2.class, ClassPathLoader.getLatestAsClassLoader());
        for (final EntityResolver2 entityResolver : entityResolvers) {
            if (getEntityResolver().getClass().isAssignableFrom(entityResolver.getClass())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Resolver not found.", found);
    }

    /**
     * Resolve the cache.xml XSD using the {@link PivotalEntityResolver}. Verifies that the
     * META-INF/schemas files are correctly found.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testResolveEntity() throws Exception {
        final InputSource inputSource = getEntityResolver().resolveEntity(null, getSystemId());
        Assert.assertNotNull(inputSource);
        Assert.assertEquals(getSystemId(), inputSource.getSystemId());
    }

    /**
     * Test {@link PivotalEntityResolver#resolveEntity(String, String)} with <code>null</code>
     * <code>systemId</code>. Asserts that returns to <code>null<code>.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testResolveEntityNullSystemId() throws Exception, SAXException {
        final String systemId = null;
        final InputSource inputSource = getEntityResolver().resolveEntity(null, systemId);
        Assert.assertNull(inputSource);
    }

    /**
     * Test {@link PivotalEntityResolver#resolveEntity(String, String)} with
     * <code>"--not-a-valid-system-id--"</code> <code>systemId</code>, which is not in the Pivotal
     * namespace.. Asserts that returns to <code>null<code>.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testResolveEntityUnkownSystemId() throws Exception {
        final String systemId = "--not-a-valid-system-id--";
        final InputSource inputSource = getEntityResolver().resolveEntity(null, systemId);
        Assert.assertNull(inputSource);
    }

    /**
     * Test {@link PivotalEntityResolver#resolveEntity(String, String)} with
     * <code>"http://schema.pivotal.io/this/should/be/not/found.xsd"</code> <code>systemId</code>,
     * which should not be found. Asserts that returns to <code>null<code>.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testResolveEntityNotFoundSystemId() throws Exception {
        final String systemId = "http://schema.pivotal.io/this/should/be/not/found.xsd";
        final InputSource inputSource = getEntityResolver().resolveEntity(null, systemId);
        Assert.assertNull(inputSource);
    }
}

