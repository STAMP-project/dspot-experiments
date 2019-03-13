/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jute;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void testXMLString() {
        assertXMLString("hello");
        assertXMLString(",}%");// special characters

    }

    @Test
    public void testXMLBuffer() {
        assertXMLBuffer("hello".getBytes());
    }

    @Test
    public void testCSVString() {
        assertCSVString("hello");
        assertCSVString(",}%");// special characters

    }

    @Test
    public void testFromCSVString() {
        try {
            Utils.fromCSVString("1");// not starting with '

            Assert.fail("Should have thrown an IOException");
        } catch (IOException ioex) {
        }
    }

    @Test
    public void testCSVBuffer() {
        assertCSVBuffer("universe".getBytes());
    }
}

