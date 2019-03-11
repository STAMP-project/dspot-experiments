/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.builder;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test meant to exercise the extended boolean parsing logic from Elasticsearch
 */
@RunWith(Parameterized.class)
public abstract class AbstractExtendedBooleanValueReaderTest {
    /**
     * This is because nulls are treated oddly across scala and java language boundaries.
     */
    public enum ExpectedOutcome {

        NULL,
        TRUE,
        FALSE;}

    protected ValueReader vr;

    private final String jsonInput;

    private final AbstractExtendedBooleanValueReaderTest.ExpectedOutcome expected;

    public AbstractExtendedBooleanValueReaderTest(String jsonInput, AbstractExtendedBooleanValueReaderTest.ExpectedOutcome expected) {
        this.jsonInput = jsonInput;
        this.expected = expected;
    }

    @Test
    public void testConvertBoolean() throws Exception {
        verify(jsonInput, expected);
    }
}

