/**
 * Copyright 2008-2013 the original author or authors.
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
package org.springframework.batch.item.validator;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link ValidatingItemProcessor}.
 */
public class ValidatingItemProcessorTests {
    @SuppressWarnings("unchecked")
    private Validator<String> validator = Mockito.mock(Validator.class);

    private static final String ITEM = "item";

    @Test
    public void testSuccessfulValidation() throws Exception {
        ValidatingItemProcessor<String> tested = new ValidatingItemProcessor(validator);
        validator.validate(ValidatingItemProcessorTests.ITEM);
        Assert.assertSame(ValidatingItemProcessorTests.ITEM, tested.process(ValidatingItemProcessorTests.ITEM));
    }

    @Test(expected = ValidationException.class)
    public void testFailedValidation() throws Exception {
        ValidatingItemProcessor<String> tested = new ValidatingItemProcessor(validator);
        processFailedValidation(tested);
    }

    @Test
    public void testFailedValidation_Filter() throws Exception {
        ValidatingItemProcessor<String> tested = new ValidatingItemProcessor(validator);
        tested.setFilter(true);
        Assert.assertNull(processFailedValidation(tested));
    }
}

