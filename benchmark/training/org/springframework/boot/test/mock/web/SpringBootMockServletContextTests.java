/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.mock.web;


import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLDecoder;
import javax.servlet.ServletContext;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ServletContextAware;


/**
 * Tests for {@link SpringBootMockServletContext}.
 *
 * @author Phillip Webb
 */
@DirtiesContext
@RunWith(SpringRunner.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@WebAppConfiguration("src/test/webapp")
public class SpringBootMockServletContextTests implements ServletContextAware {
    private ServletContext servletContext;

    @Test
    public void getResourceLocation() throws Exception {
        testResource("/inwebapp", "src/test/webapp");
        testResource("/inmetainfresources", "/META-INF/resources");
        testResource("/inresources", "/resources");
        testResource("/instatic", "/static");
        testResource("/inpublic", "/public");
    }

    // gh-2654
    @Test
    public void getRootUrlExistsAndIsEmpty() throws Exception {
        SpringBootMockServletContext context = new SpringBootMockServletContext("src/test/doesntexist") {
            @Override
            protected String getResourceLocation(String path) {
                // Don't include the Spring Boot defaults for this test
                return getResourceBasePathLocation(path);
            }
        };
        URL resource = context.getResource("/");
        assertThat(resource).isNotEqualTo(Matchers.nullValue());
        File file = new File(URLDecoder.decode(resource.getPath(), "UTF-8"));
        assertThat(file).exists().isDirectory();
        String[] contents = file.list(( dir, name) -> !((".".equals(name)) || ("..".equals(name))));
        assertThat(contents).isNotEqualTo(Matchers.nullValue());
        assertThat(contents.length).isEqualTo(0);
    }

    @Configuration
    static class Config {}
}

