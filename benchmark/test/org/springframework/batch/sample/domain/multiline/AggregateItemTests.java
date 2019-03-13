/**
 * Copyright 2006-2014 the original author or authors.
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
package org.springframework.batch.sample.domain.multiline;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Syer
 */
public class AggregateItemTests {
    /**
     * Test method for {@link org.springframework.batch.sample.domain.multiline.AggregateItem#getFooter()}.
     */
    @Test
    public void testGetFooter() {
        Assert.assertTrue(AggregateItem.getFooter().isFooter());
        Assert.assertFalse(AggregateItem.getFooter().isHeader());
    }

    /**
     * Test method for {@link org.springframework.batch.sample.domain.multiline.AggregateItem#getHeader()}.
     */
    @Test
    public void testGetHeader() {
        Assert.assertTrue(AggregateItem.getHeader().isHeader());
        Assert.assertFalse(AggregateItem.getHeader().isFooter());
    }

    @Test
    public void testBeginRecordHasNoItem() throws Exception {
        try {
            AggregateItem.getHeader().getItem();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testEndRecordHasNoItem() throws Exception {
        try {
            AggregateItem.getFooter().getItem();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }
}

