/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.file.builder;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.AbstractItemStreamItemReaderTests;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.Resource;


/**
 *
 *
 * @author Glenn Renfro
 */
public class MultiResourceItemReaderBuilderTests extends AbstractItemStreamItemReaderTests {
    @Test
    public void testNullDelegate() {
        try {
            new MultiResourceItemReaderBuilder<String>().resources(new Resource[]{  }).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "delegate is required.", ise.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNullResources() {
        try {
            new MultiResourceItemReaderBuilder<String>().delegate(Mockito.mock(FlatFileItemReader.class)).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "resources array is required.", ise.getMessage());
        }
    }
}

