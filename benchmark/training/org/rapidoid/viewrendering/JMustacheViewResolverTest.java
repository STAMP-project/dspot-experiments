/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.viewrendering;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.integrate.Integrate;
import org.rapidoid.integrate.JMustacheViewResolver;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class JMustacheViewResolverTest extends IsolatedIntegrationTest {
    @Test
    public void testRendering() {
        My.templatesPath("view-rendering");
        JMustacheViewResolver viewResolver = Integrate.jMustacheViewResolver();
        viewResolver.setCustomizer(( compiler) -> compiler.defaultValue("DEFAULT"));
        My.viewResolver(viewResolver);
        On.get("/").view("mtmpl").mvc(( req, resp) -> {
            resp.model("y", "bar");
            return U.map("x", "foo");
        });
        getReq("/");
    }
}

