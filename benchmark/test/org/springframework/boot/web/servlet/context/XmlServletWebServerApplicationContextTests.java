/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.servlet.context;


import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * Tests for {@link XmlServletWebServerApplicationContext}.
 *
 * @author Phillip Webb
 */
public class XmlServletWebServerApplicationContextTests {
    private static final String PATH = (XmlServletWebServerApplicationContextTests.class.getPackage().getName().replace('.', '/')) + "/";

    private static final String FILE = "exampleEmbeddedWebApplicationConfiguration.xml";

    private XmlServletWebServerApplicationContext context;

    @Test
    public void createFromResource() {
        this.context = new XmlServletWebServerApplicationContext(new ClassPathResource(XmlServletWebServerApplicationContextTests.FILE, getClass()));
        verifyContext();
    }

    @Test
    public void createFromResourceLocation() {
        this.context = new XmlServletWebServerApplicationContext(((XmlServletWebServerApplicationContextTests.PATH) + (XmlServletWebServerApplicationContextTests.FILE)));
        verifyContext();
    }

    @Test
    public void createFromRelativeResourceLocation() {
        this.context = new XmlServletWebServerApplicationContext(getClass(), XmlServletWebServerApplicationContextTests.FILE);
        verifyContext();
    }

    @Test
    public void loadAndRefreshFromResource() {
        this.context = new XmlServletWebServerApplicationContext();
        this.context.load(new ClassPathResource(XmlServletWebServerApplicationContextTests.FILE, getClass()));
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void loadAndRefreshFromResourceLocation() {
        this.context = new XmlServletWebServerApplicationContext();
        this.context.load(((XmlServletWebServerApplicationContextTests.PATH) + (XmlServletWebServerApplicationContextTests.FILE)));
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void loadAndRefreshFromRelativeResourceLocation() {
        this.context = new XmlServletWebServerApplicationContext();
        this.context.load(getClass(), XmlServletWebServerApplicationContextTests.FILE);
        this.context.refresh();
        verifyContext();
    }
}

