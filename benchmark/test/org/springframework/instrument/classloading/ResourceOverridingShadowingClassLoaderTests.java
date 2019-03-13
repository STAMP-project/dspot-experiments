/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.instrument.classloading;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 2.0
 */
public class ResourceOverridingShadowingClassLoaderTests {
    private static final String EXISTING_RESOURCE = "org/springframework/instrument/classloading/testResource.xml";

    private ClassLoader thisClassLoader = getClass().getClassLoader();

    private ResourceOverridingShadowingClassLoader overridingLoader = new ResourceOverridingShadowingClassLoader(thisClassLoader);

    @Test
    public void testFindsExistingResourceWithGetResourceAndNoOverrides() {
        Assert.assertNotNull(thisClassLoader.getResource(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        Assert.assertNotNull(overridingLoader.getResource(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
    }

    @Test
    public void testDoesNotFindExistingResourceWithGetResourceAndNullOverride() {
        Assert.assertNotNull(thisClassLoader.getResource(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        overridingLoader.override(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE, null);
        Assert.assertNull(overridingLoader.getResource(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
    }

    @Test
    public void testFindsExistingResourceWithGetResourceAsStreamAndNoOverrides() {
        Assert.assertNotNull(thisClassLoader.getResourceAsStream(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        Assert.assertNotNull(overridingLoader.getResourceAsStream(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
    }

    @Test
    public void testDoesNotFindExistingResourceWithGetResourceAsStreamAndNullOverride() {
        Assert.assertNotNull(thisClassLoader.getResourceAsStream(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        overridingLoader.override(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE, null);
        Assert.assertNull(overridingLoader.getResourceAsStream(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
    }

    @Test
    public void testFindsExistingResourceWithGetResourcesAndNoOverrides() throws IOException {
        Assert.assertNotNull(thisClassLoader.getResources(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        Assert.assertNotNull(overridingLoader.getResources(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        Assert.assertEquals(1, countElements(overridingLoader.getResources(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE)));
    }

    @Test
    public void testDoesNotFindExistingResourceWithGetResourcesAndNullOverride() throws IOException {
        Assert.assertNotNull(thisClassLoader.getResources(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE));
        overridingLoader.override(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE, null);
        Assert.assertEquals(0, countElements(overridingLoader.getResources(ResourceOverridingShadowingClassLoaderTests.EXISTING_RESOURCE)));
    }
}

