/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text.translate;


import java.io.CharArrayWriter;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for {@link org.apache.commons.lang3.text.translate.UnicodeUnpairedSurrogateRemover}.
 */
@Deprecated
public class UnicodeUnpairedSurrogateRemoverTest {
    final UnicodeUnpairedSurrogateRemover subject = new UnicodeUnpairedSurrogateRemover();

    final CharArrayWriter writer = new CharArrayWriter();// nothing is ever written to it


    @Test
    public void testValidCharacters() throws IOException {
        Assertions.assertFalse(subject.translate(55295, writer));
        Assertions.assertFalse(subject.translate(57344, writer));
        Assertions.assertEquals(0, writer.size());
    }

    @Test
    public void testInvalidCharacters() throws IOException {
        Assertions.assertTrue(subject.translate(55296, writer));
        Assertions.assertTrue(subject.translate(57343, writer));
        Assertions.assertEquals(0, writer.size());
    }
}

