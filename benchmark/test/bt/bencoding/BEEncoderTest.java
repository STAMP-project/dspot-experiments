/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.bencoding;


import BEType.INTEGER;
import BEType.LIST;
import BEType.MAP;
import BEType.STRING;
import bt.bencoding.model.BEInteger;
import bt.bencoding.model.BEList;
import bt.bencoding.model.BEMap;
import bt.bencoding.model.BEObject;
import bt.bencoding.model.BEString;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BEEncoderTest {
    private static final Charset defaultCharset = Charset.forName("UTF-8");

    @Test
    public void testEncoder_String() {
        String s = "some string";
        BEParser parser = BEEncoderTest.encodeAndCreateParser(new BEString(s.getBytes(BEEncoderTest.defaultCharset)));
        Assert.assertEquals(STRING, parser.readType());
        Assert.assertEquals(s, parser.readString().getValue(BEEncoderTest.defaultCharset));
    }

    @Test
    public void testEncoder_Integer() {
        BigInteger i = BigInteger.valueOf(1234567890);
        BEParser parser = BEEncoderTest.encodeAndCreateParser(new BEInteger(null, i));
        Assert.assertEquals(INTEGER, parser.readType());
        Assert.assertEquals(i, parser.readInteger().getValue());
    }

    @Test
    public void testEncode_List() {
        List<BEObject<?>> l = new ArrayList<>();
        l.add(new BEString("some string1:2#3".getBytes(BEEncoderTest.defaultCharset)));
        l.add(new BEInteger(null, BigInteger.valueOf(1234567890)));
        l.add(new BEMap(null, new HashMap()));
        BEParser parser = BEEncoderTest.encodeAndCreateParser(new BEList(null, l));
        Assert.assertEquals(LIST, parser.readType());
        Assert.assertEquals(l, parser.readList().getValue());
    }

    @Test
    public void testEncode_Map() {
        BEString s = new BEString("some string1:2#3".getBytes(BEEncoderTest.defaultCharset));
        BEInteger i = new BEInteger(null, BigInteger.valueOf(1234567890));
        BEMap emptyMap = new BEMap(null, new HashMap());
        BEList l = new BEList(null, Arrays.asList(s, i, emptyMap));
        Map<String, BEObject<?>> m = new HashMap<>();
        m.put("4:list", l);
        m.put("key1", s);
        m.put("key2", emptyMap);
        BEParser parser = BEEncoderTest.encodeAndCreateParser(new BEMap(null, m));
        Assert.assertEquals(MAP, parser.readType());
        Assert.assertEquals(m, parser.readMap().getValue());
    }
}

