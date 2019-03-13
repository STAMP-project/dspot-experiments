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
package org.apache.nifi.util.validator;


import StandardValidators.BOOLEAN_VALIDATOR;
import StandardValidators.NON_BLANK_VALIDATOR;
import StandardValidators.NON_EMPTY_VALIDATOR;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;
import org.apache.nifi.processor.util.StandardValidators;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestStandardValidators {
    @Test
    public void testNonBlankValidator() {
        Validator val = StandardValidators.NON_BLANK_VALIDATOR;
        ValidationContext vc = Mockito.mock(ValidationContext.class);
        ValidationResult vr = val.validate("foo", "", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "    ", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "    h", vc);
        Assert.assertTrue(vr.isValid());
    }

    @Test
    public void testNonEmptyELValidator() {
        Validator val = StandardValidators.NON_EMPTY_EL_VALIDATOR;
        ValidationContext vc = Mockito.mock(ValidationContext.class);
        Mockito.when(vc.isExpressionLanguageSupported("foo")).thenReturn(true);
        ValidationResult vr = val.validate("foo", "", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "    h", vc);
        Assert.assertTrue(vr.isValid());
        Mockito.when(vc.isExpressionLanguagePresent("${test}")).thenReturn(true);
        vr = val.validate("foo", "${test}", vc);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("foo", "${test", vc);
        Assert.assertTrue(vr.isValid());
    }

    @Test
    public void testHostnamePortListValidator() {
        Validator val = StandardValidators.HOSTNAME_PORT_LIST_VALIDATOR;
        ValidationContext vc = Mockito.mock(ValidationContext.class);
        Mockito.when(vc.isExpressionLanguageSupported("foo")).thenReturn(true);
        ValidationResult vr = val.validate("foo", "", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "localhost", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "test:0", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "test:65536", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "test:6666,localhost", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "test:65535", vc);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("foo", "test:65535,localhost:666,127.0.0.1:8989", vc);
        Assert.assertTrue(vr.isValid());
        Mockito.when(vc.isExpressionLanguagePresent("${test}")).thenReturn(true);
        vr = val.validate("foo", "${test}", vc);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("foo", "${test", vc);
        Assert.assertFalse(vr.isValid());
    }

    @Test
    public void testTimePeriodValidator() {
        Validator val = StandardValidators.createTimePeriodValidator(1L, TimeUnit.SECONDS, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        ValidationResult vr;
        final ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        vr = val.validate("TimePeriodTest", "0 sense made", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("TimePeriodTest", null, validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("TimePeriodTest", "0 secs", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("TimePeriodTest", "999 millis", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("TimePeriodTest", "999999999 nanos", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("TimePeriodTest", "1 sec", validationContext);
        Assert.assertTrue(vr.isValid());
    }

    @Test
    public void testDataSizeBoundsValidator() {
        Validator val = StandardValidators.createDataSizeBoundsValidator(100, 1000);
        ValidationResult vr;
        final ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        vr = val.validate("DataSizeBounds", "5 GB", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("DataSizeBounds", "0 B", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("DataSizeBounds", "99 B", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("DataSizeBounds", "100 B", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("DataSizeBounds", "999 B", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("DataSizeBounds", "1000 B", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("DataSizeBounds", "1001 B", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("DataSizeBounds", "water", validationContext);
        Assert.assertFalse(vr.isValid());
    }

    @Test
    public void testListValidator() {
        Validator val = StandardValidators.createListValidator(true, false, NON_EMPTY_VALIDATOR);
        ValidationResult vr;
        final ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        vr = val.validate("List", null, validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "", validationContext);
        Assert.assertFalse(vr.isValid());
        // Whitespace will be trimmed
        vr = val.validate("List", " ", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "1", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("List", "1,2,3", validationContext);
        Assert.assertTrue(vr.isValid());
        // The parser will not bother with whitespace after the last comma
        vr = val.validate("List", "a,", validationContext);
        Assert.assertTrue(vr.isValid());
        // However it will bother if there is an empty element in the list (two commas in a row, e.g.)
        vr = val.validate("List", "a,,c", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "a,  ,c, ", validationContext);
        Assert.assertFalse(vr.isValid());
        // Try without trim and use a non-blank validator instead of a non-empty one
        val = StandardValidators.createListValidator(false, true, NON_BLANK_VALIDATOR);
        vr = val.validate("List", null, validationContext);
        Assert.assertFalse(vr.isValid());
        // Validator will ignore empty entries
        vr = val.validate("List", "", validationContext);
        Assert.assertTrue(vr.isValid());
        // Whitespace will not be trimmed, but it is still invalid because a non-blank validator is used
        vr = val.validate("List", " ", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "a,,c", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("List", "a,  ,c, ", validationContext);
        Assert.assertFalse(vr.isValid());
        // Try without trim and use a non-empty validator
        val = StandardValidators.createListValidator(false, false, NON_EMPTY_VALIDATOR);
        vr = val.validate("List", null, validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "", validationContext);
        Assert.assertFalse(vr.isValid());
        // Whitespace will not be trimmed
        vr = val.validate("List", " ", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("List", "a,  ,c, ", validationContext);
        Assert.assertTrue(vr.isValid());
        // Try with trim and use a boolean validator
        val = StandardValidators.createListValidator(true, true, BOOLEAN_VALIDATOR);
        vr = val.validate("List", "notbool", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "    notbool \n   ", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("List", "true", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("List", "    true   \n   ", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("List", " , false,  true,\n", validationContext);
        Assert.assertTrue(vr.isValid());
    }

    @Test
    public void testCreateURLorFileValidator() {
        Validator val = StandardValidators.createURLorFileValidator();
        ValidationResult vr;
        final ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        vr = val.validate("URLorFile", null, validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("URLorFile", "", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("URLorFile", "http://nifi.apache.org", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("URLorFile", "http//nifi.apache.org", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("URLorFile", "nifi.apache.org", validationContext);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("URLorFile", "src/test/resources/this_file_exists.txt", validationContext);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("URLorFile", "src/test/resources/this_file_does_not_exist.txt", validationContext);
        Assert.assertFalse(vr.isValid());
    }

    @Test
    public void testiso8061InstantValidator() {
        Validator val = StandardValidators.ISO8061_INSTANT_VALIDATOR;
        ValidationContext vc = Mockito.mock(ValidationContext.class);
        ValidationResult vr = val.validate("foo", "", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "2016-01-01T01:01:01.000-0100", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "2016-01-01T01:01:01.000Z", vc);
        Assert.assertTrue(vr.isValid());
    }

    @Test
    public void testURIListValidator() {
        Validator val = StandardValidators.URI_LIST_VALIDATOR;
        ValidationContext vc = Mockito.mock(ValidationContext.class);
        ValidationResult vr = val.validate("foo", null, vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "/no_scheme", vc);
        Assert.assertTrue(vr.isValid());
        vr = val.validate("foo", "http://localhost 8080, https://host2:8080 ", vc);
        Assert.assertFalse(vr.isValid());
        vr = val.validate("foo", "http://localhost , https://host2:8080 ", vc);
        Assert.assertTrue(vr.isValid());
    }
}

