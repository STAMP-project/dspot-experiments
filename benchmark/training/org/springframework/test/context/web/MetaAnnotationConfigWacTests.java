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
package org.springframework.test.context.web;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;


/**
 * Integration test that verifies meta-annotation support for {@link WebAppConfiguration}
 * and {@link ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 4.0
 * @see WebTestConfiguration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebTestConfiguration
public class MetaAnnotationConfigWacTests {
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected MockServletContext mockServletContext;

    @Autowired
    protected String foo;

    @Test
    public void fooEnigmaAutowired() {
        Assert.assertEquals("enigma", foo);
    }

    @Test
    public void basicWacFeatures() throws Exception {
        Assert.assertNotNull("ServletContext should be set in the WAC.", wac.getServletContext());
        Assert.assertNotNull("ServletContext should have been autowired from the WAC.", mockServletContext);
        Object rootWac = mockServletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        Assert.assertNotNull(("Root WAC must be stored in the ServletContext as: " + (WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)), rootWac);
        Assert.assertSame("test WAC and Root WAC in ServletContext must be the same object.", wac, rootWac);
        Assert.assertSame("ServletContext instances must be the same object.", mockServletContext, wac.getServletContext());
        Assert.assertEquals("Getting real path for ServletContext resource.", new File("src/main/webapp/index.jsp").getCanonicalPath(), mockServletContext.getRealPath("index.jsp"));
    }
}

