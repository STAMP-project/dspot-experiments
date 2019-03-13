/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.resource;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;


/**
 * Unit tests for {@link ContentVersionStrategy}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 */
public class ContentBasedVersionStrategyTests {
    private ContentVersionStrategy versionStrategy = new ContentVersionStrategy();

    @Test
    public void extractVersion() {
        String hash = "7fbe76cdac6093784895bb4989203e5a";
        String path = ("font-awesome/css/font-awesome.min-" + hash) + ".css";
        Assert.assertEquals(hash, this.versionStrategy.extractVersion(path));
        Assert.assertNull(this.versionStrategy.extractVersion("foo/bar.css"));
    }

    @Test
    public void removeVersion() {
        String hash = "7fbe76cdac6093784895bb4989203e5a";
        String file = "font-awesome/css/font-awesome.min%s%s.css";
        Assert.assertEquals(String.format(file, "", ""), this.versionStrategy.removeVersion(String.format(file, "-", hash), hash));
    }

    @Test
    public void getResourceVersion() throws IOException {
        Resource expected = new ClassPathResource("test/bar.css", getClass());
        String hash = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(expected.getInputStream()));
        Assert.assertEquals(hash, this.versionStrategy.getResourceVersion(expected));
    }

    @Test
    public void addVersionToUrl() {
        Assert.assertEquals("test/bar-123.css", this.versionStrategy.addVersion("test/bar.css", "123"));
    }
}

