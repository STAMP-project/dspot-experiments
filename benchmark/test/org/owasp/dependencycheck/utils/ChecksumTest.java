/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.utils;


import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Jeremy Long
 */
public class ChecksumTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Test of getChecksum method, of class Checksum.
     *
     * @throws Exception
     * 		thrown when an exception occurs.
     */
    @Test
    public void testGetChecksum() throws Exception {
        String algorithm = "MD5";
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        byte[] expResult = new byte[]{ -16, -111, 92, 95, 70, -72, -49, -94, -125, -27, -83, 103, -96, -101, 55, -109 };
        byte[] result = Checksum.getChecksum(algorithm, file);
        Assert.assertArrayEquals(expResult, result);
    }

    /**
     * Test of getChecksum method, of class Checksum. This checks that an exception is thrown when an invalid path is specified.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testGetChecksum_FileNotFound() throws Exception {
        String algorithm = "MD5";
        File file = new File("not a valid file");
        expectedException.expect(IOException.class);
        Checksum.getChecksum(algorithm, file);
        Assert.fail("exception should be thrown");
    }

    /**
     * Test of getChecksum method, of class Checksum. This checks that an exception is thrown when an invalid algorithm is
     * specified.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testGetChecksum_NoSuchAlgorithm() throws Exception {
        String algorithm = "some unknown algorithm";
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").getPath());
        expectedException.expect(NoSuchAlgorithmException.class);
        Checksum.getChecksum(algorithm, file);
    }

    /**
     * Test of getMD5Checksum method, of class Checksum.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testGetMD5Checksum() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        // String expResult = "F0915C5F46B8CFA283E5AD67A09B3793";
        String expResult = "f0915c5f46b8cfa283e5ad67a09b3793";
        String result = Checksum.getMD5Checksum(file);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getSHA1Checksum method, of class Checksum.
     *
     * @throws Exception
     * 		is thrown when an exception occurs.
     */
    @Test
    public void testGetSHA1Checksum() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        // String expResult = "B8A9FF28B21BCB1D0B50E24A5243D8B51766851A";
        String expResult = "b8a9ff28b21bcb1d0b50e24a5243d8b51766851a";
        String result = Checksum.getSHA1Checksum(file);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getHex method, of class Checksum.
     */
    @Test
    public void testGetHex() {
        byte[] raw = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // String expResult = "000102030405060708090A0B0C0D0E0F10";
        String expResult = "000102030405060708090a0b0c0d0e0f10";
        String result = Checksum.getHex(raw);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getChecksum method, of class Checksum.
     */
    @Test
    public void testGetChecksum_String_File() throws Exception {
        String algorithm = "MD5";
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        byte[] expResult = new byte[]{ -16, -111, 92, 95, 70, -72, -49, -94, -125, -27, -83, 103, -96, -101, 55, -109 };
        byte[] result = Checksum.getChecksum(algorithm, file);
        Assert.assertArrayEquals(expResult, result);
    }

    /**
     * Test of getMD5Checksum method, of class Checksum.
     */
    @Test
    public void testGetMD5Checksum_File() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        String expResult = "f0915c5f46b8cfa283e5ad67a09b3793";
        String result = Checksum.getMD5Checksum(file);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getSHA1Checksum method, of class Checksum.
     */
    @Test
    public void testGetSHA1Checksum_File() throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource("checkSumTest.file").toURI().getPath());
        String expResult = "b8a9ff28b21bcb1d0b50e24a5243d8b51766851a";
        String result = Checksum.getSHA1Checksum(file);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getChecksum method, of class Checksum.
     */
    @Test
    public void testGetChecksum_String_byteArr() {
        String algorithm = "SHA1";
        byte[] bytes = new byte[]{ -16, -111, 92, 95, 70, -72, -49, -94, -125, -27, -83, 103, -96, -101, 55, -109 };
        String expResult = "89268a389a97f0bfba13d3ff2370d8ad436e36f6";
        String result = Checksum.getChecksum(algorithm, bytes);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getMD5Checksum method, of class Checksum.
     */
    @Test
    public void testGetMD5Checksum_String() {
        String text = "test string";
        String expResult = "6f8db599de986fab7a21625b7916589c";
        String result = Checksum.getMD5Checksum(text);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getSHA1Checksum method, of class Checksum.
     */
    @Test
    public void testGetSHA1Checksum_String() {
        String text = "test string";
        String expResult = "661295c9cbf9d6b2f6428414504a8deed3020641";
        String result = Checksum.getSHA1Checksum(text);
        Assert.assertEquals(expResult, result);
    }
}

