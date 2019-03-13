/**
 * ========================================================================
 */
/**
 * Copyright 2007-2009 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff;


import SerializableObjects.bar;
import SerializableObjects.negativeBar;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Testing for json ser/deser against messages.
 *
 * @author David Yu
 * @unknown Nov 20, 2009
 */
public class JsonNumericCoreSerDeserTest extends TestCase {
    public void testFoo() throws Exception {
        Foo fooCompare = SerializableObjects.foo;
        Foo dfoo = new Foo();
        byte[] data = JsonIOUtil.toByteArray(fooCompare, fooCompare.cachedSchema(), true);
        JsonIOUtil.mergeFrom(data, dfoo, dfoo.cachedSchema(), true);
        SerializableObjects.assertEquals(fooCompare, dfoo);
    }

    public void testBar() throws Exception {
        for (Bar barCompare : new Bar[]{ SerializableObjects.bar, SerializableObjects.negativeBar }) {
            Bar dbar = new Bar();
            byte[] data = JsonIOUtil.toByteArray(barCompare, barCompare.cachedSchema(), true);
            JsonIOUtil.mergeFrom(data, dbar, dbar.cachedSchema(), true);
            SerializableObjects.assertEquals(barCompare, dbar);
        }
    }

    public void testBaz() throws Exception {
        for (Baz bazCompare : new Baz[]{ SerializableObjects.baz, SerializableObjects.negativeBaz }) {
            Baz dbaz = new Baz();
            byte[] data = JsonIOUtil.toByteArray(bazCompare, bazCompare.cachedSchema(), true);
            JsonIOUtil.mergeFrom(data, dbaz, dbaz.cachedSchema(), true);
            SerializableObjects.assertEquals(bazCompare, dbaz);
        }
    }

    public void testListIO() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        bars.add(bar);
        bars.add(negativeBar);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonIOUtil.writeListTo(out, bars, bar.cachedSchema(), true);
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = JsonIOUtil.parseListFrom(in, bar.cachedSchema(), true);
        TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (Bar b : parsedBars)
            SerializableObjects.assertEquals(bars.get((i++)), b);

    }
}

