/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.commands;


import org.apache.camel.Message;
import org.apache.camel.ValidationException;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidatorListCommandTest {
    private static final Logger LOG = LoggerFactory.getLogger(ValidatorListCommandTest.class);

    @Test
    public void testValidatorList() throws Exception {
        String out = doTest(false);
        Assert.assertTrue(out.contains("xml:foo"));
        Assert.assertTrue(out.contains(("java:" + (this.getClass().getName()))));
        Assert.assertTrue(out.contains("custom"));
        Assert.assertTrue(out.contains("Started"));
        Assert.assertFalse(out.contains("ProcessorValidator["));
        Assert.assertFalse(out.contains("processor='validate(body)'"));
        Assert.assertFalse(out.contains("processor='sendTo(direct://validator)'"));
        Assert.assertFalse(out.contains("MyValidator["));
    }

    @Test
    public void testValidatorListVerbose() throws Exception {
        String out = doTest(true);
        Assert.assertTrue(out.contains("xml:foo"));
        Assert.assertTrue(out.contains(("java:" + (this.getClass().getName()))));
        Assert.assertTrue(out.contains("custom"));
        Assert.assertTrue(out.contains("Started"));
        Assert.assertTrue(out.contains("ProcessorValidator["));
        Assert.assertTrue(out.contains("processor='validate(body)'"));
        Assert.assertTrue(out.contains("processor='sendTo(direct://validator)'"));
        Assert.assertTrue(out.contains("MyValidator["));
    }

    public static class MyValidator extends Validator {
        @Override
        public void validate(Message message, DataType type) throws ValidationException {
            return;
        }
    }
}

