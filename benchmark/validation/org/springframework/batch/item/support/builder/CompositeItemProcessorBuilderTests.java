/**
 * Copyright 2017-2018 the original author or authors.
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
package org.springframework.batch.item.support.builder;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;


/**
 *
 *
 * @author Glenn Renfro
 * @author Drummond Dawson
 */
public class CompositeItemProcessorBuilderTests {
    @Mock
    private ItemProcessor<Object, Object> processor1;

    @Mock
    private ItemProcessor<Object, Object> processor2;

    private List<ItemProcessor<Object, Object>> processors;

    @Test
    public void testTransform() throws Exception {
        Object item = new Object();
        Object itemAfterFirstTransformation = new Object();
        Object itemAfterSecondTransformation = new Object();
        CompositeItemProcessor<Object, Object> composite = new CompositeItemProcessorBuilder().delegates(this.processors).build();
        Mockito.when(processor1.process(item)).thenReturn(itemAfterFirstTransformation);
        Mockito.when(processor2.process(itemAfterFirstTransformation)).thenReturn(itemAfterSecondTransformation);
        Assert.assertSame(itemAfterSecondTransformation, composite.process(item));
    }

    @Test
    public void testTransformVarargs() throws Exception {
        Object item = new Object();
        Object itemAfterFirstTransformation = new Object();
        Object itemAfterSecondTransformation = new Object();
        CompositeItemProcessor<Object, Object> composite = new CompositeItemProcessorBuilder().delegates(this.processor1, this.processor2).build();
        Mockito.when(processor1.process(item)).thenReturn(itemAfterFirstTransformation);
        Mockito.when(processor2.process(itemAfterFirstTransformation)).thenReturn(itemAfterSecondTransformation);
        Assert.assertSame(itemAfterSecondTransformation, composite.process(item));
    }

    @Test
    public void testNullOrEmptyDelegates() throws Exception {
        validateExceptionMessage(new CompositeItemProcessorBuilder().delegates(new ArrayList()), "The delegates list must have one or more delegates.");
        validateExceptionMessage(new CompositeItemProcessorBuilder().delegates(), "The delegates list must have one or more delegates.");
        validateExceptionMessage(new CompositeItemProcessorBuilder(), "A list of delegates is required.");
    }
}

