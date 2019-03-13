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
package org.springframework.batch.sample.domain.trade;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;


/**
 *
 *
 * @author Lucas Ward
 */
public class CompositeCustomerUpdateLineTokenizerTests {
    private CompositeCustomerUpdateLineTokenizerTests.StubLineTokenizer customerTokenizer;

    private FieldSet customerFieldSet = new DefaultFieldSet(null);

    private FieldSet footerFieldSet = new DefaultFieldSet(null);

    private CompositeCustomerUpdateLineTokenizer compositeTokenizer;

    @Test
    public void testCustomerAdd() throws Exception {
        String customerAddLine = "AFDASFDASFDFSA";
        FieldSet fs = compositeTokenizer.tokenize(customerAddLine);
        Assert.assertEquals(customerFieldSet, fs);
        Assert.assertEquals(customerAddLine, customerTokenizer.getTokenizedLine());
    }

    @Test
    public void testCustomerDelete() throws Exception {
        String customerAddLine = "DFDASFDASFDFSA";
        FieldSet fs = compositeTokenizer.tokenize(customerAddLine);
        Assert.assertEquals(customerFieldSet, fs);
        Assert.assertEquals(customerAddLine, customerTokenizer.getTokenizedLine());
    }

    @Test
    public void testCustomerUpdate() throws Exception {
        String customerAddLine = "UFDASFDASFDFSA";
        FieldSet fs = compositeTokenizer.tokenize(customerAddLine);
        Assert.assertEquals(customerFieldSet, fs);
        Assert.assertEquals(customerAddLine, customerTokenizer.getTokenizedLine());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLine() throws Exception {
        String invalidLine = "INVALID";
        compositeTokenizer.tokenize(invalidLine);
    }

    private static class StubLineTokenizer implements LineTokenizer {
        private final FieldSet fieldSetToReturn;

        private String tokenizedLine;

        public StubLineTokenizer(FieldSet fieldSetToReturn) {
            this.fieldSetToReturn = fieldSetToReturn;
        }

        @Override
        public FieldSet tokenize(String line) {
            this.tokenizedLine = line;
            return fieldSetToReturn;
        }

        public String getTokenizedLine() {
            return tokenizedLine;
        }
    }
}

