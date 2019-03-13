/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.io;


import java.io.StringReader;
import junit.framework.Assert;
import junit.framework.TestCase;


public class OldAndroidStringReaderTest extends TestCase {
    public void testStringReader() throws Exception {
        String str = "AbCdEfGhIjKlMnOpQrStUvWxYz";
        StringReader a = new StringReader(str);
        StringReader b = new StringReader(str);
        StringReader c = new StringReader(str);
        StringReader d = new StringReader(str);
        Assert.assertEquals(str, OldAndroidStringReaderTest.read(a));
        Assert.assertEquals("AbCdEfGhIj", OldAndroidStringReaderTest.read(b, 10));
        Assert.assertEquals("bdfhjlnprtvxz", OldAndroidStringReaderTest.skipRead(c));
        Assert.assertEquals("AbCdEfGdEfGhIjKlMnOpQrStUvWxYz", OldAndroidStringReaderTest.markRead(d, 3, 4));
    }
}

