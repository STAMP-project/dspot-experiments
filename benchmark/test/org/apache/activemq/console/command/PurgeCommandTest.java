/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.activemq.console.command;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PurgeCommandTest {
    private final List<String> datum;

    private final String expected;

    /**
     * Test if the wildcard queries correctly converted into a valid SQL92
     * statement.
     */
    @Test
    public void testConvertToSQL92() {
        System.out.print(("testTokens  = " + (datum)));
        System.out.println(("  output = " + (expected)));
        PurgeCommand pc = new PurgeCommand();
        Assert.assertEquals(expected, pc.convertToSQL92(datum));
    }

    public PurgeCommandTest(List<String> datum, String expected) {
        this.datum = datum;
        this.expected = expected;
    }
}

