/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.web.reactive.result.condition;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ServerWebExchange;


/**
 * Unit tests for {@link RequestMethodsRequestCondition}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestMethodsRequestConditionTests {
    // TODO: custom method, CORS pre-flight (see @Ignored)
    @Test
    public void getMatchingCondition() throws Exception {
        testMatch(new RequestMethodsRequestCondition(GET), GET);
        testMatch(new RequestMethodsRequestCondition(GET, POST), GET);
        testNoMatch(new RequestMethodsRequestCondition(GET), POST);
    }

    @Test
    public void getMatchingConditionWithHttpHead() throws Exception {
        testMatch(new RequestMethodsRequestCondition(HEAD), HEAD);
        testMatch(new RequestMethodsRequestCondition(GET), GET);
        testNoMatch(new RequestMethodsRequestCondition(POST), HEAD);
    }

    @Test
    public void getMatchingConditionWithEmptyConditions() throws Exception {
        RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition();
        for (RequestMethod method : RequestMethod.values()) {
            if (method != (OPTIONS)) {
                ServerWebExchange exchange = getExchange(method.name());
                Assert.assertNotNull(condition.getMatchingCondition(exchange));
            }
        }
        testNoMatch(condition, OPTIONS);
    }

    @Test
    public void compareTo() throws Exception {
        RequestMethodsRequestCondition c1 = new RequestMethodsRequestCondition(GET, HEAD);
        RequestMethodsRequestCondition c2 = new RequestMethodsRequestCondition(POST);
        RequestMethodsRequestCondition c3 = new RequestMethodsRequestCondition();
        ServerWebExchange exchange = getExchange("GET");
        int result = c1.compareTo(c2, exchange);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = c2.compareTo(c1, exchange);
        Assert.assertTrue(("Invalid comparison result: " + result), (result > 0));
        result = c2.compareTo(c3, exchange);
        Assert.assertTrue(("Invalid comparison result: " + result), (result < 0));
        result = c1.compareTo(c1, exchange);
        Assert.assertEquals("Invalid comparison result ", 0, result);
    }

    @Test
    public void combine() {
        RequestMethodsRequestCondition condition1 = new RequestMethodsRequestCondition(GET);
        RequestMethodsRequestCondition condition2 = new RequestMethodsRequestCondition(POST);
        RequestMethodsRequestCondition result = condition1.combine(condition2);
        Assert.assertEquals(2, result.getContent().size());
    }
}

