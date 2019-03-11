/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RouteParameterTest {
    @Test
    public void parse() {
        Map<String, RouteParameter> params;
        RouteParameter param;
        // no named parameters is null
        params = RouteParameter.parse("/user");
        Assert.assertThat(params, Matchers.aMapWithSize(0));
        params = RouteParameter.parse("/user/{id}/{email: [0-9]+}");
        param = params.get("id");
        Assert.assertThat(param.getName(), CoreMatchers.is("id"));
        Assert.assertThat(param.getToken(), CoreMatchers.is("{id}"));
        Assert.assertThat(param.getRegex(), CoreMatchers.is(CoreMatchers.nullValue()));
        param = params.get("email");
        Assert.assertThat(param.getName(), CoreMatchers.is("email"));
        Assert.assertThat(param.getToken(), CoreMatchers.is("{email: [0-9]+}"));
        Assert.assertThat(param.getRegex(), CoreMatchers.is("[0-9]+"));
    }
}

