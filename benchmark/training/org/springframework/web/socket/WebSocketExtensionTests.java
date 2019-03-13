/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.web.socket;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test fixture for {@link WebSocketExtension}
 *
 * @author Brian Clozel
 */
public class WebSocketExtensionTests {
    @Test
    public void parseHeaderSingle() {
        List<WebSocketExtension> extensions = WebSocketExtension.parseExtensions("x-test-extension ; foo=bar ; bar=baz");
        Assert.assertThat(extensions, Matchers.hasSize(1));
        WebSocketExtension extension = extensions.get(0);
        Assert.assertEquals("x-test-extension", extension.getName());
        Assert.assertEquals(2, extension.getParameters().size());
        Assert.assertEquals("bar", extension.getParameters().get("foo"));
        Assert.assertEquals("baz", extension.getParameters().get("bar"));
    }

    @Test
    public void parseHeaderMultiple() {
        List<WebSocketExtension> extensions = WebSocketExtension.parseExtensions("x-foo-extension, x-bar-extension");
        Assert.assertThat(extensions, Matchers.hasSize(2));
        Assert.assertEquals("x-foo-extension", extensions.get(0).getName());
        Assert.assertEquals("x-bar-extension", extensions.get(1).getName());
    }
}

