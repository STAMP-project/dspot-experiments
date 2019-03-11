/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.utils.serializer;


import com.esotericsoftware.kryo.io.Input;
import org.junit.Assert;
import org.junit.Test;

import static KryoInputPool.MAX_POOLED_BUFFER_SIZE;


public class KryoInputPoolTest {
    private KryoInputPool kryoInputPool;

    @Test
    public void discardOutput() {
        final Input[] result = new Input[2];
        kryoInputPool.run(( input) -> {
            result[0] = input;
            return null;
        }, ((MAX_POOLED_BUFFER_SIZE) + 1));
        kryoInputPool.run(( input) -> {
            result[1] = input;
            return null;
        }, 0);
        Assert.assertTrue(((result[0]) != (result[1])));
    }

    @Test
    public void recycleOutput() {
        final Input[] result = new Input[2];
        kryoInputPool.run(( input) -> {
            assertEquals(0, input.position());
            byte[] payload = new byte[]{ 1, 2, 3, 4 };
            input.setBuffer(payload);
            assertArrayEquals(payload, input.readBytes(4));
            result[0] = input;
            return null;
        }, 0);
        Assert.assertNull(result[0].getInputStream());
        Assert.assertEquals(0, result[0].position());
        kryoInputPool.run(( input) -> {
            result[1] = input;
            return null;
        }, 0);
        Assert.assertTrue(((result[0]) == (result[1])));
    }
}

