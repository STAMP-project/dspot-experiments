/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
/**
 * -*
 * Copyright ? 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.ipadic;


import com.atilika.kuromoji.TestUtils;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import org.junit.Test;


public class RandomizedInputTest extends RandomizedTest {
    private static final int LENGTH = 1024;

    private Tokenizer tokenizer = new Tokenizer();

    @Test
    @Repeat(iterations = 50)
    public void testRandomizedUnicodeInput() {
        TestUtils.assertCanTokenizeString(randomUnicodeOfLength(RandomizedInputTest.LENGTH), tokenizer);
    }

    @Test
    @Repeat(iterations = 50)
    public void testRandomizedRealisticUnicodeInput() {
        TestUtils.assertCanTokenizeString(randomRealisticUnicodeOfLength(RandomizedInputTest.LENGTH), tokenizer);
    }

    @Test
    @Repeat(iterations = 50)
    public void testRandomizedAsciiInput() {
        TestUtils.assertCanTokenizeString(randomAsciiOfLength(RandomizedInputTest.LENGTH), tokenizer);
    }
}

