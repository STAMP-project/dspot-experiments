/**
 * The MIT License
 *
 * Copyright 2015 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.debatty.java.stringsimilarity;


import info.debatty.java.stringsimilarity.testutil.NullEmptyTests;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thibault Debatty
 */
public class CosineTest {
    /**
     * Test of similarity method, of class Cosine.
     */
    @Test
    public final void testSimilarity() {
        System.out.println("similarity");
        Cosine instance = new Cosine();
        double result = instance.similarity("ABC", "ABCE");
        Assert.assertEquals(0.71, result, 0.01);
        NullEmptyTests.testSimilarity(instance);
    }

    /**
     * If one of the strings is smaller than k, the similarity should be 0.
     */
    @Test
    public final void testSmallString() {
        System.out.println("test small string");
        Cosine instance = new Cosine(3);
        double result = instance.similarity("AB", "ABCE");
        Assert.assertEquals(0.0, result, 1.0E-5);
    }

    @Test
    public final void testLargeString() throws IOException {
        System.out.println("Test with large strings");
        Cosine cos = new Cosine();
        // read from 2 text files
        String string1 = CosineTest.readResourceFile("71816-2.txt");
        String string2 = CosineTest.readResourceFile("11328-1.txt");
        double similarity = cos.similarity(string1, string2);
        Assert.assertEquals(0.8115, similarity, 0.001);
    }

    @Test
    public final void testDistance() {
        Cosine instance = new Cosine();
        NullEmptyTests.testDistance(instance);
        // TODO: regular (non-null/empty) distance tests
    }
}

