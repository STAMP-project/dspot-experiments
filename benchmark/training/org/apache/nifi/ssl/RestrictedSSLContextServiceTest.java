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
package org.apache.nifi.ssl;


import java.util.HashSet;
import java.util.Set;
import org.apache.nifi.components.AllowableValue;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class RestrictedSSLContextServiceTest {
    @Test
    public void testTLSAlgorithms() {
        final Set<String> expected = new HashSet<>();
        expected.add("TLS");
        expected.add("TLSv1.2");
        final AllowableValue[] allowableValues = RestrictedSSLContextService.buildAlgorithmAllowableValues();
        MatcherAssert.assertThat(allowableValues, IsNull.notNullValue());
        MatcherAssert.assertThat(allowableValues.length, CoreMatchers.equalTo(expected.size()));
        for (final AllowableValue value : allowableValues) {
            Assert.assertTrue(expected.contains(value.getValue()));
        }
    }
}

