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
package tests.api.javax.net.ssl;


import javax.net.ssl.SSLEngineResult;
import junit.framework.TestCase;

import static javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.values;
import static javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;


/**
 * Tests for SSLEngineResult class
 */
public class SSLEngineResultTest extends TestCase {
    /**
     * Test for <code>SSLEngineResult(SSLEngineResult.Status status,
     *              SSLEngineResult.HandshakeStatus handshakeStatus,
     *              int bytesConsumed,
     *              int bytesProduced) </code> constructor and
     * <code>getHandshakeStatus()</code>
     * <code>getStatus()</code>
     * <code>bytesConsumed()</code>
     * <code>bytesProduced()</code>
     * <code>toString()</code>
     * methods
     * Assertions:
     * constructor throws IllegalArgumentException when bytesConsumed
     * or bytesProduced is negative or when status or handshakeStatus
     * is null
     */
    public void test_ConstructorLjavax_net_ssl_SSLEngineResult_StatusLjavax_net_ssl_SSLEngineResult_HandshakeStatusII() {
        int[] neg = new int[]{ -1, -10, -1000, Integer.MIN_VALUE, (Integer.MIN_VALUE) + 1 };
        try {
            new SSLEngineResult(null, FINISHED, 1, 1);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        try {
            new SSLEngineResult(BUFFER_OVERFLOW, null, 1, 1);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        for (int i = 0; i < (neg.length); i++) {
            try {
                new SSLEngineResult(BUFFER_OVERFLOW, FINISHED, neg[i], 1);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (neg.length); i++) {
            try {
                new SSLEngineResult(BUFFER_OVERFLOW, FINISHED, 1, neg[i]);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
        try {
            SSLEngineResult res = new SSLEngineResult(BUFFER_OVERFLOW, FINISHED, 1, 2);
            TestCase.assertNotNull("Null object", res);
            TestCase.assertEquals(1, res.bytesConsumed());
            TestCase.assertEquals(2, res.bytesProduced());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * Test for <code>bytesConsumed()</code> method
     */
    public void test_bytesConsumed() {
        int[] pos = new int[]{ 0, 1, 1000, Integer.MAX_VALUE, (Integer.MAX_VALUE) - 1 };
        SSLEngineResult.Status[] enS = SSLEngineResult.Status.values();
        SSLEngineResult.HandshakeStatus[] enHS = values();
        for (int i = 0; i < (enS.length); i++) {
            for (int j = 0; j < (enHS.length); j++) {
                for (int n = 0; n < (pos.length); n++) {
                    for (int l = 0; l < (pos.length); l++) {
                        SSLEngineResult res = new SSLEngineResult(enS[i], enHS[j], pos[n], pos[l]);
                        TestCase.assertEquals("Incorrect bytesConsumed", pos[n], res.bytesConsumed());
                    }
                }
            }
        }
    }

    /**
     * Test for <code>bytesProduced()</code> method
     */
    public void test_bytesProduced() {
        int[] pos = new int[]{ 0, 1, 1000, Integer.MAX_VALUE, (Integer.MAX_VALUE) - 1 };
        SSLEngineResult.Status[] enS = SSLEngineResult.Status.values();
        SSLEngineResult.HandshakeStatus[] enHS = values();
        for (int i = 0; i < (enS.length); i++) {
            for (int j = 0; j < (enHS.length); j++) {
                for (int n = 0; n < (pos.length); n++) {
                    for (int l = 0; l < (pos.length); ++l) {
                        SSLEngineResult res = new SSLEngineResult(enS[i], enHS[j], pos[n], pos[l]);
                        TestCase.assertEquals("Incorrect bytesProduced", pos[l], res.bytesProduced());
                    }
                }
            }
        }
    }

    /**
     * Test for <code>getHandshakeStatus()</code> method
     */
    public void test_getHandshakeStatus() {
        int[] pos = new int[]{ 0, 1, 1000, Integer.MAX_VALUE, (Integer.MAX_VALUE) - 1 };
        SSLEngineResult.Status[] enS = SSLEngineResult.Status.values();
        SSLEngineResult.HandshakeStatus[] enHS = values();
        for (int i = 0; i < (enS.length); i++) {
            for (int j = 0; j < (enHS.length); j++) {
                for (int n = 0; n < (pos.length); n++) {
                    for (int l = 0; l < (pos.length); ++l) {
                        SSLEngineResult res = new SSLEngineResult(enS[i], enHS[j], pos[n], pos[l]);
                        TestCase.assertEquals("Incorrect HandshakeStatus", enHS[j], res.getHandshakeStatus());
                    }
                }
            }
        }
    }

    /**
     * Test for <code>getStatus()</code> method
     */
    public void test_getStatus() {
        int[] pos = new int[]{ 0, 1, 1000, Integer.MAX_VALUE, (Integer.MAX_VALUE) - 1 };
        SSLEngineResult.Status[] enS = SSLEngineResult.Status.values();
        SSLEngineResult.HandshakeStatus[] enHS = values();
        for (int i = 0; i < (enS.length); i++) {
            for (int j = 0; j < (enHS.length); j++) {
                for (int n = 0; n < (pos.length); n++) {
                    for (int l = 0; l < (pos.length); ++l) {
                        SSLEngineResult res = new SSLEngineResult(enS[i], enHS[j], pos[n], pos[l]);
                        TestCase.assertEquals("Incorrect Status", enS[i], res.getStatus());
                    }
                }
            }
        }
    }

    /**
     * Test for <code>toString()</code> method
     */
    public void test_toString() {
        int[] pos = new int[]{ 0, 1, 1000, Integer.MAX_VALUE, (Integer.MAX_VALUE) - 1 };
        SSLEngineResult.Status[] enS = SSLEngineResult.Status.values();
        SSLEngineResult.HandshakeStatus[] enHS = values();
        for (int i = 0; i < (enS.length); i++) {
            for (int j = 0; j < (enHS.length); j++) {
                for (int n = 0; n < (pos.length); n++) {
                    for (int l = 0; l < (pos.length); ++l) {
                        SSLEngineResult res = new SSLEngineResult(enS[i], enHS[j], pos[n], pos[l]);
                        TestCase.assertNotNull("Result of toSring() method is null", res.toString());
                    }
                }
            }
        }
    }
}

