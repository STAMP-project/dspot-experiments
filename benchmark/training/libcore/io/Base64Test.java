/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.io;


import java.util.Arrays;
import junit.framework.TestCase;


public final class Base64Test extends TestCase {
    public void testDecodeEmpty() throws Exception {
        TestCase.assertEquals("[]", Arrays.toString(Base64.decode(new byte[0])));
    }

    public void testEncode() throws Exception {
        assertEncoded("");
        assertEncoded("Eg==", 18);
        assertEncoded("EjQ=", 18, 52);
        assertEncoded("EjRW", 18, 52, 86);
        assertEncoded("EjRWeA==", 18, 52, 86, 120);
        assertEncoded("EjRWeJo=", 18, 52, 86, 120, 154);
        assertEncoded("EjRWeJq8", 18, 52, 86, 120, 154, 188);
    }

    public void testEncodeDoesNotWrap() {
        int[] data = new int[61];
        Arrays.fill(data, 255);
        String expected = "///////////////////////////////////////////////////////////////////////" + "//////////w==";// 84 chars

        assertEncoded(expected, data);
    }
}

