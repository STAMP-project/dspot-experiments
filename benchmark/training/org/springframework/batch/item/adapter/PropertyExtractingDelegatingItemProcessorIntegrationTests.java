/**
 * Copyright 2008-2018 the original author or authors.
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
package org.springframework.batch.item.adapter;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.sample.Foo;
import org.springframework.batch.item.sample.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests for {@link PropertyExtractingDelegatingItemWriter}
 *
 * @author Robert Kasanicky
 * @author Mahmoud Ben Hassine
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "pe-delegating-item-writer.xml")
public class PropertyExtractingDelegatingItemProcessorIntegrationTests {
    @Autowired
    private PropertyExtractingDelegatingItemWriter<Foo> processor;

    @Autowired
    private FooService fooService;

    /* Regular usage scenario - input object should be passed to the service the injected invoker points to. */
    @Test
    public void testProcess() throws Exception {
        Foo foo;
        while ((foo = fooService.generateFoo()) != null) {
            processor.write(Collections.singletonList(foo));
        } 
        List<Foo> input = fooService.getGeneratedFoos();
        List<Foo> processed = fooService.getProcessedFooNameValuePairs();
        Assert.assertEquals(input.size(), processed.size());
        Assert.assertFalse(fooService.getProcessedFooNameValuePairs().isEmpty());
        for (int i = 0; i < (input.size()); i++) {
            Foo inputFoo = input.get(i);
            Foo outputFoo = processed.get(i);
            Assert.assertEquals(inputFoo.getName(), outputFoo.getName());
            Assert.assertEquals(inputFoo.getValue(), outputFoo.getValue());
            Assert.assertEquals(0, outputFoo.getId());
        }
    }
}

