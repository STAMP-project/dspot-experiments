/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import org.junit.Assert;
import org.junit.Test;


public class AppendableCharSequenceTest {
    @Test
    public void testSimpleAppend() {
        AppendableCharSequenceTest.testSimpleAppend0(new AppendableCharSequence(128));
    }

    @Test
    public void testAppendString() {
        AppendableCharSequenceTest.testAppendString0(new AppendableCharSequence(128));
    }

    @Test
    public void testAppendAppendableCharSequence() {
        AppendableCharSequence seq = new AppendableCharSequence(128);
        String text = "testdata";
        AppendableCharSequence seq2 = new AppendableCharSequence(128);
        seq2.append(text);
        seq.append(seq2);
        Assert.assertEquals(text, seq.toString());
        Assert.assertEquals(text.substring(1, ((text.length()) - 2)), seq.substring(1, ((text.length()) - 2)));
        AppendableCharSequenceTest.assertEqualsChars(text, seq);
    }

    @Test
    public void testSimpleAppendWithExpand() {
        AppendableCharSequenceTest.testSimpleAppend0(new AppendableCharSequence(2));
    }

    @Test
    public void testAppendStringWithExpand() {
        AppendableCharSequenceTest.testAppendString0(new AppendableCharSequence(2));
    }

    @Test
    public void testSubSequence() {
        AppendableCharSequence master = new AppendableCharSequence(26);
        master.append("abcdefghijlkmonpqrstuvwxyz");
        Assert.assertEquals("abcdefghij", master.subSequence(0, 10).toString());
    }

    @Test
    public void testEmptySubSequence() {
        AppendableCharSequence master = new AppendableCharSequence(26);
        master.append("abcdefghijlkmonpqrstuvwxyz");
        AppendableCharSequence sub = master.subSequence(0, 0);
        Assert.assertEquals(0, sub.length());
        sub.append('b');
        Assert.assertEquals('b', sub.charAt(0));
    }
}

