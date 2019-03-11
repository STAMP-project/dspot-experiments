/**
 * Copyright 2017 the original author or authors.
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
package org.springframework.batch.item.function;


import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ItemProcessor;


/**
 *
 *
 * @author Michael Minella
 */
public class FunctionItemProcessorTests {
    private Function<Object, String> function;

    @Test
    public void testConstructorValidation() {
        try {
            new FunctionItemProcessor(null);
            Assert.fail("null should not be accepted as a constructor arg");
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void testFunctionItemProcessor() throws Exception {
        ItemProcessor<Object, String> itemProcessor = new FunctionItemProcessor(this.function);
        Assert.assertEquals("1", itemProcessor.process(1L));
        Assert.assertEquals("foo", itemProcessor.process("foo"));
    }
}

