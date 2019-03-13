/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona;


import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AsciiSequenceViewTest {
    private static final int INDEX = 2;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[128]);

    private final AsciiSequenceView asciiSequenceView = new AsciiSequenceView();

    @Test
    public void shouldBeAbleToGetChars() {
        final String data = "stringy";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        Assert.assertThat(asciiSequenceView.charAt(0), CoreMatchers.is('s'));
        Assert.assertThat(asciiSequenceView.charAt(1), CoreMatchers.is('t'));
        Assert.assertThat(asciiSequenceView.charAt(2), CoreMatchers.is('r'));
        Assert.assertThat(asciiSequenceView.charAt(3), CoreMatchers.is('i'));
        Assert.assertThat(asciiSequenceView.charAt(4), CoreMatchers.is('n'));
        Assert.assertThat(asciiSequenceView.charAt(5), CoreMatchers.is('g'));
        Assert.assertThat(asciiSequenceView.charAt(6), CoreMatchers.is('y'));
    }

    @Test
    public void shouldToString() {
        final String data = "a little bit of ascii";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        Assert.assertThat(asciiSequenceView.toString(), CoreMatchers.is(data));
    }

    @Test
    public void shouldReturnCorrectLength() {
        final String data = "a little bit of ascii";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        Assert.assertThat(asciiSequenceView.length(), CoreMatchers.is(data.length()));
    }

    @Test
    public void shouldCopyDataUnderTheView() {
        final String data = "a little bit of ascii";
        final int targetBufferOffset = 56;
        final MutableDirectBuffer targetBuffer = new UnsafeBuffer(new byte[128]);
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        asciiSequenceView.getBytes(targetBuffer, targetBufferOffset);
        Assert.assertThat(targetBuffer.getStringWithoutLengthAscii(targetBufferOffset, data.length()), CoreMatchers.is(data));
    }

    @Test
    public void shouldSubSequence() {
        final String data = "a little bit of ascii";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        final AsciiSequenceView subSequenceView = asciiSequenceView.subSequence(2, 8);
        Assert.assertThat(subSequenceView.toString(), CoreMatchers.is("little"));
    }

    @Test
    public void shouldReturnEmptyStringWhenBufferIsNull() {
        Assert.assertEquals(0, asciiSequenceView.length());
        Assert.assertEquals("", asciiSequenceView.toString());
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowIndexOutOfBoundsExceptionWhenCharNotPresentAtGivenPosition() {
        final String data = "foo";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        asciiSequenceView.charAt(4);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowExceptionWhenCharAtCalledWithNoBuffer() {
        asciiSequenceView.charAt(0);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowExceptionWhenCharAtCalledWithNegativeIndex() {
        final String data = "foo";
        buffer.putStringWithoutLengthAscii(AsciiSequenceViewTest.INDEX, data);
        asciiSequenceView.wrap(buffer, AsciiSequenceViewTest.INDEX, data.length());
        asciiSequenceView.charAt((-1));
    }
}

