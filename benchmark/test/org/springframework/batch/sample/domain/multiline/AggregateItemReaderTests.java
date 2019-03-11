/**
 * Copyright 2008-2014 the original author or authors.
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


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ItemReader;


public class AggregateItemReaderTests {
    private ItemReader<AggregateItem<String>> input;

    private AggregateItemReader<String> provider;

    @Test
    public void testNext() throws Exception {
        Object result = provider.read();
        Collection<?> lines = ((Collection<?>) (result));
        Assert.assertEquals(3, lines.size());
        for (Object line : lines) {
            Assert.assertEquals("line", line);
        }
        Assert.assertNull(provider.read());
    }
}

