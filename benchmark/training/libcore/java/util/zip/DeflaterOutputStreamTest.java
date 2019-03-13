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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import junit.framework.TestCase;


public class DeflaterOutputStreamTest extends TestCase {
    public void testSyncFlushEnabled() throws Exception {
        InputStream in = DeflaterOutputStreamTest.createInflaterStream(DeflaterOutputStream.class, true);
        TestCase.assertEquals(1, in.read());
        TestCase.assertEquals(2, in.read());
        TestCase.assertEquals(3, in.read());
        in.close();
    }

    public void testSyncFlushDisabled() throws Exception {
        InputStream in = DeflaterOutputStreamTest.createInflaterStream(DeflaterOutputStream.class, false);
        try {
            in.read();
            TestCase.fail();
        } catch (IOException expected) {
        }
        in.close();
    }

    /**
     * Confirm that a DeflaterOutputStream constructed with Deflater
     * with flushParm == SYNC_FLUSH does not need to to be flushed.
     *
     * http://b/4005091
     */
    public void testSyncFlushDeflater() throws Exception {
        Deflater def = new Deflater();
        Field f = def.getClass().getDeclaredField("flushParm");
        f.setAccessible(true);
        f.setInt(def, Deflater.SYNC_FLUSH);
        final int deflaterBufferSize = 512;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(baos, def, deflaterBufferSize);
        // make output buffer large enough that even if compressed it
        // won't all fit within the deflaterBufferSize.
        final int outputBufferSize = 128 * deflaterBufferSize;
        byte[] output = new byte[outputBufferSize];
        for (int i = 0; i < (output.length); i++) {
            output[i] = ((byte) (i));
        }
        dos.write(output);
        byte[] compressed = baos.toByteArray();
        // this main reason for this assert is to make sure that the
        // compressed byte count is larger than the
        // deflaterBufferSize. However, when the original bug exists,
        // it will also fail because the compressed length will be
        // exactly the length of the deflaterBufferSize.
        TestCase.assertTrue(((("compressed=" + (compressed.length)) + " but deflaterBufferSize=") + deflaterBufferSize), ((compressed.length) > deflaterBufferSize));
        // assert that we returned data matches the input exactly.
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        InflaterInputStream iis = new InflaterInputStream(bais);
        byte[] input = new byte[output.length];
        int total = 0;
        while (true) {
            int n = iis.read(input, total, ((input.length) - total));
            if (n == (-1)) {
                break;
            }
            total += n;
            if (total == (input.length)) {
                try {
                    iis.read();
                    TestCase.fail();
                } catch (EOFException expected) {
                    break;
                }
            }
        } 
        TestCase.assertEquals(output.length, total);
        TestCase.assertTrue(Arrays.equals(input, output));
        // ensure Deflater.finish has not been called at any point
        // during the test, since that would lead to the results being
        // flushed even without SYNC_FLUSH being used
        TestCase.assertFalse(def.finished());
        // Quieten CloseGuard.
        def.end();
        iis.close();
    }
}

