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
package org.springframework.web.reactive.function.server;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class RequestPredicateAttributesTests {
    private DefaultServerRequest request;

    @Test
    public void negateSucceed() {
        RequestPredicate predicate = negate();
        boolean result = predicate.test(this.request);
        Assert.assertTrue(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertEquals("baz", this.request.attributes().get("predicate"));
    }

    @Test
    public void negateFail() {
        RequestPredicate predicate = negate();
        boolean result = predicate.test(this.request);
        Assert.assertFalse(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("baz"));
    }

    @Test
    public void andBothSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(true, "left", "baz");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(true, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.AndRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertTrue(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertEquals("baz", this.request.attributes().get("left"));
        Assert.assertEquals("qux", this.request.attributes().get("right"));
    }

    @Test
    public void andLeftSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(true, "left", "bar");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(false, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.AndRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertFalse(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("left"));
        Assert.assertFalse(this.request.attributes().containsKey("right"));
    }

    @Test
    public void andRightSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(false, "left", "bar");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(true, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.AndRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertFalse(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("left"));
        Assert.assertFalse(this.request.attributes().containsKey("right"));
    }

    @Test
    public void andBothFail() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(false, "left", "bar");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(false, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.AndRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertFalse(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("left"));
        Assert.assertFalse(this.request.attributes().containsKey("right"));
    }

    @Test
    public void orBothSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(true, "left", "baz");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(true, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.OrRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertTrue(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertEquals("baz", this.request.attributes().get("left"));
        Assert.assertFalse(this.request.attributes().containsKey("right"));
    }

    @Test
    public void orLeftSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(true, "left", "baz");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(false, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.OrRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertTrue(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertEquals("baz", this.request.attributes().get("left"));
        Assert.assertFalse(this.request.attributes().containsKey("right"));
    }

    @Test
    public void orRightSucceed() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(false, "left", "baz");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(true, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.OrRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertTrue(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("left"));
        Assert.assertEquals("qux", this.request.attributes().get("right"));
    }

    @Test
    public void orBothFail() {
        RequestPredicate left = new RequestPredicateAttributesTests.AddAttributePredicate(false, "left", "baz");
        RequestPredicate right = new RequestPredicateAttributesTests.AddAttributePredicate(false, "right", "qux");
        RequestPredicate predicate = new RequestPredicates.OrRequestPredicate(left, right);
        boolean result = predicate.test(this.request);
        Assert.assertFalse(result);
        Assert.assertEquals("bar", this.request.attributes().get("exchange"));
        Assert.assertFalse(this.request.attributes().containsKey("baz"));
        Assert.assertFalse(this.request.attributes().containsKey("quux"));
    }

    private static class AddAttributePredicate implements RequestPredicate {
        private boolean result;

        private final String key;

        private final String value;

        private AddAttributePredicate(boolean result, String key, String value) {
            this.result = result;
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean test(ServerRequest request) {
            request.attributes().put(key, value);
            return this.result;
        }
    }
}

