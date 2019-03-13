/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.websocketx;


import org.junit.Test;


public class WebSocketUtilTest {
    // how many times do we want to run each random variable checker
    private static final int NUM_ITERATIONS = 1000;

    @Test
    public void testRandomNumberGenerator() {
        int iteration = 0;
        while ((++iteration) < (WebSocketUtilTest.NUM_ITERATIONS)) {
            WebSocketUtilTest.assertRandomWithinBoundaries(0, 1);
            WebSocketUtilTest.assertRandomWithinBoundaries(0, 1);
            WebSocketUtilTest.assertRandomWithinBoundaries((-1), 1);
            WebSocketUtilTest.assertRandomWithinBoundaries((-1), 0);
        } 
    }
}

