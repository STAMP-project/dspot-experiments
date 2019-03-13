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
package org.apache.nifi.processors.hadoop.inotify;


import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.junit.Assert;
import org.junit.Test;


public class TestEventTypeValidator {
    ValidationContext context;

    EventTypeValidator eventTypeValidator;

    @Test
    public void nullInputShouldProperlyFail() throws Exception {
        String subject = "subject";
        String input = null;
        ValidationResult result = eventTypeValidator.validate(subject, input, context);
        Assert.assertEquals("subject", result.getSubject());
        Assert.assertEquals(null, result.getInput());
        Assert.assertEquals("Empty event types are not allowed.", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void emptyInputShouldProperlyFail() throws Exception {
        String subject = "subject";
        String input = "";
        ValidationResult result = eventTypeValidator.validate(subject, input, context);
        Assert.assertEquals("subject", result.getSubject());
        Assert.assertEquals("", result.getInput());
        Assert.assertEquals("Empty event types are not allowed.", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void validEventTypesShouldProperlyValidate() throws Exception {
        String input = "  append, Create, CLOSE";
        String subject = "subject";
        ValidationResult result = eventTypeValidator.validate(subject, input, context);
        Assert.assertEquals("subject", result.getSubject());
        Assert.assertEquals("  append, Create, CLOSE", result.getInput());
        Assert.assertEquals("", result.getExplanation());
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void inputWithInvalidEventTypeShouldProperlyDisplayEventsInExplanation() throws Exception {
        String subject = "subject";
        String input = "append, CREATE, invalidValue1, rename, metadata, unlink";
        ValidationResult result = eventTypeValidator.validate(subject, input, context);
        Assert.assertEquals("subject", result.getSubject());
        Assert.assertEquals("append, CREATE, invalidValue1, rename, metadata, unlink", result.getInput());
        Assert.assertEquals("The following are not valid event types: [invalidValue1]", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void inputWithMultipleInvalidEventTypeShouldProperlyDisplayEventsInExplanation() throws Exception {
        String subject = "subject";
        String input = "append, CREATE, invalidValue1, rename, metadata, unlink, invalidValue2";
        ValidationResult result = eventTypeValidator.validate(subject, input, context);
        Assert.assertEquals("subject", result.getSubject());
        Assert.assertEquals("append, CREATE, invalidValue1, rename, metadata, unlink, invalidValue2", result.getInput());
        Assert.assertEquals("The following are not valid event types: [invalidValue1, invalidValue2]", result.getExplanation());
        Assert.assertFalse(result.isValid());
    }
}

