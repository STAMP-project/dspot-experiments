/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.core.io.support;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;


/**
 * If this test case fails, uncomment diagnostics in the
 * {@link #assertProtocolAndFilenames} method.
 *
 * @author Oliver Hutchison
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 17.11.2004
 */
public class PathMatchingResourcePatternResolverTests {
    private static final String[] CLASSES_IN_CORE_IO_SUPPORT = new String[]{ "EncodedResource.class", "LocalizedResourceHelper.class", "PathMatchingResourcePatternResolver.class", "PropertiesLoaderSupport.class", "PropertiesLoaderUtils.class", "ResourceArrayPropertyEditor.class", "ResourcePatternResolver.class", "ResourcePatternUtils.class" };

    private static final String[] TEST_CLASSES_IN_CORE_IO_SUPPORT = new String[]{ "PathMatchingResourcePatternResolverTests.class" };

    private static final String[] CLASSES_IN_REACTIVESTREAMS = new String[]{ "Processor.class", "Publisher.class", "Subscriber.class", "Subscription.class" };

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Test(expected = FileNotFoundException.class)
    public void invalidPrefixWithPatternElementInIt() throws IOException {
        resolver.getResources("xx**:**/*.xy");
    }

    @Test
    public void singleResourceOnFileSystem() throws IOException {
        Resource[] resources = resolver.getResources("org/springframework/core/io/support/PathMatchingResourcePatternResolverTests.class");
        Assert.assertEquals(1, resources.length);
        assertProtocolAndFilenames(resources, "file", "PathMatchingResourcePatternResolverTests.class");
    }

    @Test
    public void singleResourceInJar() throws IOException {
        Resource[] resources = resolver.getResources("org/reactivestreams/Publisher.class");
        Assert.assertEquals(1, resources.length);
        assertProtocolAndFilenames(resources, "jar", "Publisher.class");
    }

    @Test
    public void classpathWithPatternInJar() throws IOException {
        Resource[] resources = resolver.getResources("classpath:org/reactivestreams/*.class");
        assertProtocolAndFilenames(resources, "jar", PathMatchingResourcePatternResolverTests.CLASSES_IN_REACTIVESTREAMS);
    }

    @Test
    public void classpathStarWithPatternInJar() throws IOException {
        Resource[] resources = resolver.getResources("classpath*:org/reactivestreams/*.class");
        assertProtocolAndFilenames(resources, "jar", PathMatchingResourcePatternResolverTests.CLASSES_IN_REACTIVESTREAMS);
    }

    @Test
    public void rootPatternRetrievalInJarFiles() throws IOException {
        Resource[] resources = resolver.getResources("classpath*:*.dtd");
        boolean found = false;
        for (Resource resource : resources) {
            if (resource.getFilename().equals("aspectj_1_5_0.dtd")) {
                found = true;
            }
        }
        Assert.assertTrue("Could not find aspectj_1_5_0.dtd in the root of the aspectjweaver jar", found);
    }
}

