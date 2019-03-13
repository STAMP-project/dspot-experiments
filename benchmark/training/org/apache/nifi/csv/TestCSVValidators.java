/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.csv;


import CSVValidators.SingleCharacterValidator;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static CSVValidators.UNESCAPED_SINGLE_CHAR_VALIDATOR;


public class TestCSVValidators {
    /**
     * * SingleCharValidator *
     */
    @Test
    public void testSingleCharNullValue() {
        CSVValidators.SingleCharacterValidator validator = new CSVValidators.SingleCharacterValidator();
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("EscapeChar", null, mockContext);
        Assert.assertEquals("Input is null for this property", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void testSingleCharTab() {
        CSVValidators.SingleCharacterValidator validator = new CSVValidators.SingleCharacterValidator();
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("EscapeChar", "\\t", mockContext);
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void testSingleCharIllegalChar() {
        CSVValidators.SingleCharacterValidator validator = new CSVValidators.SingleCharacterValidator();
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("EscapeChar", "\\r", mockContext);
        Assert.assertEquals("\\r is not a valid character for this property", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void testSingleCharGoodChar() {
        CSVValidators.SingleCharacterValidator validator = new CSVValidators.SingleCharacterValidator();
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("EscapeChar", "'", mockContext);
        Assert.assertTrue(result.isValid());
    }

    /**
     * * Unescaped SingleCharValidator *
     */
    @Test
    public void testUnEscapedSingleCharNullValue() {
        Validator validator = UNESCAPED_SINGLE_CHAR_VALIDATOR;
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("Delimiter", null, mockContext);
        Assert.assertEquals("Input is null for this property", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void testUnescapedSingleCharUnicodeChar() {
        Validator validator = UNESCAPED_SINGLE_CHAR_VALIDATOR;
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("Delimiter", "\\u0001", mockContext);
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void testUnescapedSingleCharGoodChar() {
        Validator validator = UNESCAPED_SINGLE_CHAR_VALIDATOR;
        ValidationContext mockContext = Mockito.mock(ValidationContext.class);
        ValidationResult result = validator.validate("Delimiter", ",", mockContext);
        Assert.assertTrue(result.isValid());
    }
}

