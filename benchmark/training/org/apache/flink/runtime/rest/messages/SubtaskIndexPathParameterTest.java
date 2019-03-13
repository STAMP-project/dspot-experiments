/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest.messages;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SubtaskIndexPathParameter}.
 */
public class SubtaskIndexPathParameterTest {
    private SubtaskIndexPathParameter subtaskIndexPathParameter;

    @Test
    public void testConversionFromString() throws Exception {
        Assert.assertThat(subtaskIndexPathParameter.convertFromString("2147483647"), Matchers.equalTo(Integer.MAX_VALUE));
    }

    @Test
    public void testConversionFromStringNegativeNumber() throws Exception {
        try {
            subtaskIndexPathParameter.convertFromString("-2147483648");
            Assert.fail("Expected exception not thrown");
        } catch (final ConversionException e) {
            Assert.assertThat(e.getMessage(), Matchers.equalTo(("subtaskindex must be positive, was: " + (Integer.MIN_VALUE))));
        }
    }

    @Test
    public void testConvertToString() throws Exception {
        Assert.assertThat(subtaskIndexPathParameter.convertToString(Integer.MAX_VALUE), Matchers.equalTo("2147483647"));
    }

    @Test
    public void testIsMandatoryParameter() {
        Assert.assertTrue(subtaskIndexPathParameter.isMandatory());
    }
}

