/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import java.io.IOException;
import org.junit.Test;


/**
 * This test demonstrates why we should call mark(Integer.MAX_VALUE) instead of
 * mark(-1).
 */
public class BufferedInputStreamResetTest {
    @Test
    public void testNeatives() throws IOException {
        testNegative((-1));// fixed buffer

        testNegative(19);// 1 byte short

    }

    @Test
    public void testPositives() throws IOException {
        testPositive(20);// just enough

        testPositive(Integer.MAX_VALUE);
    }
}

