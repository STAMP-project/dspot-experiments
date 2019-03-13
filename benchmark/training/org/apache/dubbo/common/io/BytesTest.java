/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.io;


import java.io.File;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BytesTest {
    private final byte[] b1 = "adpfioha;eoh;aldfadl;kfadslkfdajfio123431241235123davas;odvwe;lmzcoqpwoewqogineopwqihwqetup\n\tejqf;lajsfd\u4e2d\u6587\u5b57\u7b260da0gsaofdsf==adfasdfs".getBytes();

    private final String C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";// default base64.


    private byte[] bytes1 = new byte[]{ 3, 12, 14, 41, 12, 2, 3, 12, 4, 67, 23 };

    private byte[] bytes2 = new byte[]{ 3, 12, 14, 41, 12, 2, 3, 12, 4, 67 };

    @Test
    public void testMain() throws Exception {
        short s = ((short) (43981));
        MatcherAssert.assertThat(s, CoreMatchers.is(Bytes.bytes2short(Bytes.short2bytes(s))));
        int i = 198284;
        MatcherAssert.assertThat(i, CoreMatchers.is(Bytes.bytes2int(Bytes.int2bytes(i))));
        long l = 13841747174L;
        MatcherAssert.assertThat(l, CoreMatchers.is(Bytes.bytes2long(Bytes.long2bytes(l))));
        float f = 1.3F;
        MatcherAssert.assertThat(f, CoreMatchers.is(Bytes.bytes2float(Bytes.float2bytes(f))));
        double d = 11213.3;
        MatcherAssert.assertThat(d, CoreMatchers.is(Bytes.bytes2double(Bytes.double2bytes(d))));
        MatcherAssert.assertThat(Bytes.int2bytes(i), CoreMatchers.is(int2bytes(i)));
        MatcherAssert.assertThat(Bytes.long2bytes(l), CoreMatchers.is(long2bytes(l)));
        String str = Bytes.bytes2base64("dubbo".getBytes());
        byte[] bytes = Bytes.base642bytes(str, 0, str.length());
        MatcherAssert.assertThat(bytes, CoreMatchers.is("dubbo".getBytes()));
        byte[] bytesWithC64 = Bytes.base642bytes(str, C64);
        MatcherAssert.assertThat(bytesWithC64, CoreMatchers.is(bytes));
    }

    @Test
    public void testWrongBase64Code() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Bytes.bytes2base64("dubbo".getBytes(), 0, 1, new char[]{ 'a' }));
    }

    @Test
    public void testWrongOffSet() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Bytes.bytes2base64("dubbo".getBytes(), (-1), 1));
    }

    @Test
    public void testLargeLength() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Bytes.bytes2base64("dubbo".getBytes(), 0, 100000));
    }

    @Test
    public void testSmallLength() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Bytes.bytes2base64("dubbo".getBytes(), 0, (-1)));
    }

    @Test
    public void testBase64S2b2sFailCaseLog() throws Exception {
        String s1 = Bytes.bytes2base64(bytes1);
        byte[] out1 = Bytes.base642bytes(s1);
        MatcherAssert.assertThat(bytes1, CoreMatchers.is(out1));
        String s2 = Bytes.bytes2base64(bytes2);
        byte[] out2 = Bytes.base642bytes(s2);
        MatcherAssert.assertThat(bytes2, CoreMatchers.is(out2));
    }

    @Test
    public void testHex() {
        String str = Bytes.bytes2hex(b1);
        MatcherAssert.assertThat(b1, CoreMatchers.is(Bytes.hex2bytes(str)));
    }

    @Test
    public void testMD5ForString() {
        byte[] md5 = Bytes.getMD5("dubbo");
        MatcherAssert.assertThat(md5, CoreMatchers.is(Bytes.base642bytes("qk4bjCzJ3u2W/gEu8uB1Kg==")));
    }

    @Test
    public void testMD5ForFile() throws IOException {
        byte[] md5 = Bytes.getMD5(new File(getClass().getClassLoader().getResource("md5.testfile.txt").getFile()));
        MatcherAssert.assertThat(md5, CoreMatchers.is(Bytes.base642bytes("iNZ+5qHafVNPLJxHwLKJ3w==")));
    }

    @Test
    public void testZip() throws IOException {
        String s = "hello world";
        byte[] zip = Bytes.zip(s.getBytes());
        byte[] unzip = Bytes.unzip(zip);
        MatcherAssert.assertThat(unzip, CoreMatchers.is(s.getBytes()));
    }

    @Test
    public void testBytes2HexWithWrongOffset() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Bytes.bytes2hex("hello".getBytes(), (-1), 1));
    }

    @Test
    public void testBytes2HexWithWrongLength() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> Bytes.bytes2hex("hello".getBytes(), 0, 6));
    }
}

