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
package org.springframework.boot.web.servlet.view;


import com.samskivert.mustache.Mustache;
import java.util.Collections;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Tests for {@link MustacheView}.
 *
 * @author Dave Syer
 */
public class MustacheViewTests {
    private final String templateUrl = ("classpath:/" + (getClass().getPackage().getName().replace(".", "/"))) + "/template.html";

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

    @Test
    public void viewResolvesHandlebars() throws Exception {
        MustacheView view = new MustacheView();
        view.setCompiler(Mustache.compiler());
        view.setUrl(this.templateUrl);
        view.setApplicationContext(this.context);
        view.render(Collections.singletonMap("World", "Spring"), this.request, this.response);
        assertThat(this.response.getContentAsString()).isEqualTo("Hello Spring");
    }
}

