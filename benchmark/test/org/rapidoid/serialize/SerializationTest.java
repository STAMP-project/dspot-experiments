/**
 * -
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.serialize;


import Deleted.DELETED;
import None.NONE;
import java.nio.ByteBuffer;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.data.JSON;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
@SuppressWarnings("unchecked")
public class SerializationTest extends AbstractCommonsTest {
    @Test
    public void testMiniSerialization() {
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        Map<?, ?> data = U.map("a", 213, true, "xyz", "f", NONE, "g", DELETED);
        Serialize.serialize(buf, data);
        buf.rewind();
        Object data2 = Serialize.deserialize(buf);
        String expected = data.toString();
        String real = data2.toString();
        eq(real, expected);
    }

    @Test
    public void testSerialization() {
        ByteBuffer buf = ByteBuffer.allocateDirect(2000);
        Map<?, ?> sub1 = Coll.synchronizedMap();
        fillInData(((Map<Object, Object>) (sub1)));
        Map<?, ?> data = U.map(123, "Foo", "y", U.list(1, "Bar", new int[]{ 1, 500, 10000 }), "sub1", sub1);
        fillInData(((Map<Object, Object>) (data)));
        Serialize.serialize(buf, data);
        buf.rewind();
        Object data2 = Serialize.deserialize(buf);
        String expected = JSON.prettify(data);
        String real = JSON.prettify(data2);
        eq(real, expected);
    }
}

