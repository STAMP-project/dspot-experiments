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
import bt.bencoding.model.BEList;
import bt.bencoding.model.BEObject;
import bt.bencoding.model.BEString;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class BEParserTest {
    private Charset charset = Charset.forName("UTF-8");

    @Test
    public void testParse_String1() {
        BEParser parser = new BEParser("1:s".getBytes());
        Assert.assertEquals(STRING, parser.readType());
        Assert.assertEquals("s", parser.readString().getValue(charset));
    }

    @Test
    public void testParse_String2() {
        BEParser parser = new BEParser("11:!@#$%^&*()_".getBytes());
        Assert.assertEquals(STRING, parser.readType());
        Assert.assertEquals("!@#$%^&*()_", parser.readString().getValue(charset));
    }

    @Test(expected = Exception.class)
    public void testParse_String_Exception_EmptyString() {
        new BEParser("".getBytes());
    }

    @Test
    public void testParse_String_LengthStartsWithZero() {
        BEString string = new BEParser("0:".getBytes()).readString();
        Assert.assertNotNull(string);
        Assert.assertEquals(0, string.getValue().length);
    }

    @Test(expected = Exception.class)
    public void testParse_String_Exception_InsufficientContent() {
        new BEParser("7:abcdef".getBytes()).readString();
    }

    @Test
    public void testParse_Integer1() {
        BEParser parser = new BEParser("i1e".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        Assert.assertEquals(BigInteger.ONE, parser.readInteger().getValue());
    }

    @Test
    public void testParse_Integer_Negative() {
        BEParser parser = new BEParser("i-1e".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        Assert.assertEquals(BigInteger.ONE.negate(), parser.readInteger().getValue());
    }

    @Test(expected = Exception.class)
    public void testParse_Integer_Exception_ZeroLength() {
        BEParser parser = new BEParser("ie".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        parser.readInteger();
    }

    // (expected = Exception.class)
    @Test
    public void testParse_Integer_Exception_NegativeZero() {
        // not sure why the protocol spec forbids negative zeroes,
        // so let it be for now
        BEParser parser = new BEParser("i-0e".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        Assert.assertEquals(BigInteger.ZERO.negate(), parser.readInteger().getValue());
    }

    @Test(expected = Exception.class)
    public void testParse_Integer_Exception_NotTerminated() {
        BEParser parser = new BEParser("i1".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        parser.readInteger();
    }

    @Test(expected = Exception.class)
    public void testParse_Integer_Exception_UnexpectedTokens() {
        BEParser parser = new BEParser("i-1-e".getBytes());
        Assert.assertEquals(INTEGER, parser.readType());
        parser.readInteger();
    }

    @Test
    public void testParse_List1() {
        BEParser parser = new BEParser("l4:spam4:eggsi1ee".getBytes());
        Assert.assertEquals(LIST, parser.readType());
        Assert.assertArrayEquals(new Object[]{ "spam".getBytes(charset), "eggs".getBytes(charset), BigInteger.ONE }, parser.readList().getValue().stream().map(( o) -> ((BEObject) (o)).getValue()).collect(Collectors.toList()).toArray());
    }

    @Test
    public void testParse_Map1() {
        BEParser parser = new BEParser("d4:spaml1:a1:bee".getBytes());
        Assert.assertEquals(MAP, parser.readType());
        byte[][] expected = new byte[][]{ "a".getBytes(charset), "b".getBytes(charset) };
        Map<String, BEObject<?>> map = parser.readMap().getValue();
        Object o = map.get("spam");
        Assert.assertNotNull(o);
        Assert.assertTrue((o instanceof BEList));
        BEList actual = ((BEList) (o));
        Assert.assertArrayEquals(expected, actual.getValue().stream().map(( s) -> ((BEString) (s)).getValue()).collect(Collectors.toList()).toArray());
    }
}

