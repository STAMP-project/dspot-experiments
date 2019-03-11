/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.zip;


import java.util.zip.Inflater;
import junit.framework.TestCase;


public class InflaterTest extends TestCase {
    public void testDefaultDictionary() throws Exception {
        InflaterTest.assertRoundTrip(null);
    }

    public void testPresetCustomDictionary() throws Exception {
        InflaterTest.assertRoundTrip("dictionary".getBytes("UTF-8"));
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=11755
     */
    public void testEmptyFileAndEmptyBuffer() throws Exception {
        byte[] emptyInput = InflaterTest.deflate(new byte[0], null);
        Inflater inflater = new Inflater();
        inflater.setInput(emptyInput);
        TestCase.assertFalse(inflater.finished());
        TestCase.assertEquals(0, inflater.inflate(new byte[0], 0, 0));
        TestCase.assertTrue(inflater.finished());
    }
}

