/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.resource;


import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


/**
 * Unit tests for {@link PathResourceResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PathResourceResolverTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final PathResourceResolver resolver = new PathResourceResolver();

    @Test
    public void resolveFromClasspath() throws IOException {
        Resource location = new ClassPathResource("test/", PathResourceResolver.class);
        String path = "bar.css";
        List<Resource> locations = Collections.singletonList(location);
        Resource actual = this.resolver.resolveResource(null, path, locations, null).block(PathResourceResolverTests.TIMEOUT);
        Assert.assertEquals(location.createRelative(path), actual);
    }

    @Test
    public void resolveFromClasspathRoot() {
        Resource location = new ClassPathResource("/");
        String path = "org/springframework/web/reactive/resource/test/bar.css";
        List<Resource> locations = Collections.singletonList(location);
        Resource actual = this.resolver.resolveResource(null, path, locations, null).block(PathResourceResolverTests.TIMEOUT);
        Assert.assertNotNull(actual);
    }

    @Test
    public void checkResource() throws IOException {
        Resource location = new ClassPathResource("test/", PathResourceResolver.class);
        testCheckResource(location, "../testsecret/secret.txt");
        testCheckResource(location, "test/../../testsecret/secret.txt");
        location = new UrlResource(getClass().getResource("./test/"));
        String secretPath = new UrlResource(getClass().getResource("testsecret/secret.txt")).getURL().getPath();
        testCheckResource(location, ("file:" + secretPath));
        testCheckResource(location, ("/file:" + secretPath));
        testCheckResource(location, ("/" + secretPath));
        testCheckResource(location, ("////../.." + secretPath));
        testCheckResource(location, "/%2E%2E/testsecret/secret.txt");
        testCheckResource(location, "/%2e%2e/testsecret/secret.txt");
        testCheckResource(location, (" " + secretPath));
        testCheckResource(location, ("/  " + secretPath));
        testCheckResource(location, ("url:" + secretPath));
    }

    @Test
    public void checkResourceWithAllowedLocations() {
        this.resolver.setAllowedLocations(new ClassPathResource("test/", PathResourceResolver.class), new ClassPathResource("testalternatepath/", PathResourceResolver.class));
        Resource location = getResource("main.css");
        String actual = this.resolver.resolveUrlPath("../testalternatepath/bar.css", Collections.singletonList(location), null).block(PathResourceResolverTests.TIMEOUT);
        Assert.assertEquals("../testalternatepath/bar.css", actual);
    }

    // SPR-12624
    @Test
    public void checkRelativeLocation() throws Exception {
        String locationUrl = new UrlResource(getClass().getResource("./test/")).getURL().toExternalForm();
        Resource location = new UrlResource(locationUrl.replace("/springframework", "/../org/springframework"));
        List<Resource> locations = Collections.singletonList(location);
        Assert.assertNotNull(this.resolver.resolveResource(null, "main.css", locations, null).block(PathResourceResolverTests.TIMEOUT));
    }

    // SPR-12747
    @Test
    public void checkFileLocation() throws Exception {
        Resource resource = getResource("main.css");
        Assert.assertTrue(this.resolver.checkResource(resource, resource));
    }

    // SPR-13241
    @Test
    public void resolvePathRootResource() {
        Resource webjarsLocation = new ClassPathResource("/META-INF/resources/webjars/", PathResourceResolver.class);
        String path = this.resolver.resolveUrlPathInternal("", Collections.singletonList(webjarsLocation), null).block(PathResourceResolverTests.TIMEOUT);
        Assert.assertNull(path);
    }
}

