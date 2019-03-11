/**
 * Copyright (c) 2010-2015. Axon Framework
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
package org.axonframework.test.matchers;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class MatchAllFieldFilterTest {
    @SuppressWarnings("unused")
    private String field;

    @Test
    public void testAcceptWhenEmpty() throws Exception {
        Assert.assertTrue(new MatchAllFieldFilter(Collections.emptyList()).accept(MatchAllFieldFilterTest.class.getDeclaredField("field")));
    }

    @Test
    public void testAcceptWhenAllAccept() throws Exception {
        Assert.assertTrue(accept(MatchAllFieldFilterTest.class.getDeclaredField("field")));
    }

    @Test
    public void testRejectWhenOneRejects() throws Exception {
        Assert.assertFalse(accept(MatchAllFieldFilterTest.class.getDeclaredField("field")));
    }
}

