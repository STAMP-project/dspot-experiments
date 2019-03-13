/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter.factory;


import java.util.HashMap;
import org.junit.Test;


/**
 *
 *
 * @author Spencer Gibb
 */
public class SetPathGatewayFilterFactoryTests {
    @Test
    public void setPathFilterWorks() {
        HashMap<String, String> variables = new HashMap<>();
        testFilter("/baz/bar", "/baz/bar", variables);
    }

    @Test
    public void setEncodedPathFilterWorks() {
        HashMap<String, String> variables = new HashMap<>();
        testFilter("/baz/foo%20bar", "/baz/foo%20bar", variables);
    }

    @Test
    public void setPathFilterWithTemplateVarsWorks() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("id", "123");
        testFilter("/bar/baz/{id}", "/bar/baz/123", variables);
    }

    @Test
    public void setPathFilterWithTemplatePrefixVarsWorks() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("org", "123");
        variables.put("scope", "abc");
        testFilter("/{org}/{scope}/function", "/123/abc/function", variables);
    }

    @Test
    public void setPathFilterWithEncodedCharactersWorks() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("id", "12 3");
        testFilter("/bar/baz/{id}", "/bar/baz/12 3", variables);
    }
}

