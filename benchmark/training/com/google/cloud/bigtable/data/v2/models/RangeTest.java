/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.models;


import BoundType.CLOSED;
import BoundType.OPEN;
import BoundType.UNBOUNDED;
import ByteString.EMPTY;
import com.google.cloud.bigtable.data.v2.models.Range.ByteStringRange;
import com.google.cloud.bigtable.data.v2.models.Range.TimestampRange;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RangeTest {
    @Test
    public void timestampUnboundedTest() {
        TimestampRange range = TimestampRange.unbounded();
        assertThat(range.getStartBound()).isEqualTo(UNBOUNDED);
        assertThat(range.getEndBound()).isEqualTo(UNBOUNDED);
        Throwable actualError = null;
        try {
            // noinspection ResultOfMethodCallIgnored
            range.getStart();
        } catch (Throwable e) {
            actualError = e;
        }
        assertThat(actualError).isInstanceOf(IllegalStateException.class);
        try {
            // noinspection ResultOfMethodCallIgnored
            range.getEnd();
        } catch (Throwable e) {
            actualError = e;
        }
        assertThat(actualError).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void timestampOfTest() {
        TimestampRange range = TimestampRange.create(10, 2000);
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(10);
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(2000);
    }

    @Test
    public void timestampChangeStartTest() {
        TimestampRange range = TimestampRange.create(10, 2000).startOpen(20L);
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(2000);
        assertThat(range.getStartBound()).isEqualTo(OPEN);
        assertThat(range.getStart()).isEqualTo(20);
        range = range.startClosed(30L);
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(30);
    }

    @Test
    public void timestampChangeEndTest() {
        TimestampRange range = TimestampRange.create(10, 2000).endClosed(1000L);
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(10);
        assertThat(range.getEndBound()).isEqualTo(CLOSED);
        assertThat(range.getEnd()).isEqualTo(1000);
        range = range.endOpen(3000L);
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(3000);
    }

    @Test
    public void timestampCloneTest() {
        TimestampRange range = TimestampRange.create(10, 2000);
        TimestampRange rangeSame = range.endClosed(3000L);
        TimestampRange rangeClone = range.clone().endClosed(4000L);
        assertThat(range.getEnd()).isEqualTo(3000);
        assertThat(rangeSame.getEnd()).isEqualTo(3000);
        assertThat(rangeClone.getEnd()).isEqualTo(4000);
    }

    @Test
    public void timestampEqualsTest() {
        TimestampRange r1 = TimestampRange.create(1, 10);
        TimestampRange r2 = TimestampRange.create(1, 10);
        TimestampRange r3 = TimestampRange.create(2, 20);
        assertThat(r1).isEqualTo(r2);
        assertThat(r2).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(r3);
    }

    @Test
    public void timestampSerializationTest() throws IOException, ClassNotFoundException {
        TimestampRange expected = TimestampRange.create(10, 20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(expected);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        TimestampRange actual = ((TimestampRange) (ois.readObject()));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void byteStringUnboundedTest() {
        ByteStringRange range = ByteStringRange.unbounded();
        assertThat(range.getStartBound()).isEqualTo(UNBOUNDED);
        assertThat(range.getEndBound()).isEqualTo(UNBOUNDED);
        Throwable actualError = null;
        try {
            range.getStart();
        } catch (Throwable e) {
            actualError = e;
        }
        assertThat(actualError).isInstanceOf(IllegalStateException.class);
        try {
            range.getEnd();
        } catch (Throwable e) {
            actualError = e;
        }
        assertThat(actualError).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void byteStringOfTest() {
        ByteStringRange range = ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("b"));
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("b"));
    }

    @Test
    public void byteStringOfStringTest() {
        ByteStringRange range = ByteStringRange.create("a", "b");
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("b"));
    }

    @Test
    public void byteStringPrefixTest() {
        assertThat(ByteStringRange.prefix("a")).isEqualTo(ByteStringRange.create("a", "b"));
        assertThat(ByteStringRange.prefix("ab")).isEqualTo(ByteStringRange.create("ab", "ac"));
        ByteString prefix2 = ByteString.copyFrom(new byte[]{ 'a', ((byte) (255)) });
        assertThat(ByteStringRange.prefix(prefix2)).isEqualTo(ByteStringRange.create(prefix2, ByteString.copyFromUtf8("b")));
        ByteString prefix3 = ByteString.copyFrom(new byte[]{ ((byte) (255)), ((byte) (255)) });
        assertThat(ByteStringRange.prefix(prefix3)).isEqualTo(ByteStringRange.unbounded().startClosed(prefix3));
        assertThat(ByteStringRange.prefix(EMPTY)).isEqualTo(ByteStringRange.unbounded());
    }

    @Test
    public void byteStringChangeStartTest() {
        ByteStringRange range = ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("z")).startOpen(ByteString.copyFromUtf8("b"));
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("z"));
        assertThat(range.getStartBound()).isEqualTo(OPEN);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("b"));
        range = range.startClosed(ByteString.copyFromUtf8("c"));
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("c"));
        assertThat(range.startOpen(EMPTY).getStartBound()).isEqualTo(UNBOUNDED);
        assertThat(range.startClosed(EMPTY).getStartBound()).isEqualTo(UNBOUNDED);
    }

    @Test
    public void byteStringChangeStartStringTest() {
        ByteStringRange range = ByteStringRange.create("a", "z").startOpen("b");
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("z"));
        assertThat(range.getStartBound()).isEqualTo(OPEN);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("b"));
        range = range.startClosed("c");
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("c"));
        assertThat(range.startOpen("").getStartBound()).isEqualTo(UNBOUNDED);
        assertThat(range.startClosed("").getStartBound()).isEqualTo(UNBOUNDED);
    }

    @Test
    public void byteStringChangeEndTest() {
        ByteStringRange range = ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("z")).endClosed(ByteString.copyFromUtf8("y"));
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
        assertThat(range.getEndBound()).isEqualTo(CLOSED);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("y"));
        range = range.endOpen(ByteString.copyFromUtf8("x"));
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("x"));
        assertThat(range.endOpen(EMPTY).getEndBound()).isEqualTo(UNBOUNDED);
        assertThat(range.endClosed(EMPTY).getEndBound()).isEqualTo(UNBOUNDED);
    }

    @Test
    public void byteStringChangeEndStringTest() {
        ByteStringRange range = ByteStringRange.create("a", "z").endClosed("y");
        assertThat(range.getStartBound()).isEqualTo(CLOSED);
        assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
        assertThat(range.getEndBound()).isEqualTo(CLOSED);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("y"));
        range = range.endOpen("x");
        assertThat(range.getEndBound()).isEqualTo(OPEN);
        assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("x"));
        assertThat(range.endOpen("").getEndBound()).isEqualTo(UNBOUNDED);
        assertThat(range.endClosed("").getEndBound()).isEqualTo(UNBOUNDED);
    }

    @Test
    public void byteStringCloneTest() {
        ByteStringRange range = ByteStringRange.create("a", "original");
        ByteStringRange rangeSame = range.endClosed("sameInstance");
        ByteStringRange rangeClone = range.clone().endClosed("cloneInstance");
        assertThat(range.getEnd().toStringUtf8()).isEqualTo("sameInstance");
        assertThat(rangeSame.getEnd().toStringUtf8()).isEqualTo("sameInstance");
        assertThat(rangeClone.getEnd().toStringUtf8()).isEqualTo("cloneInstance");
    }

    @Test
    public void byteStringEqualsTest() {
        ByteStringRange r1 = ByteStringRange.create("a", "c");
        ByteStringRange r2 = ByteStringRange.create("a", "c");
        ByteStringRange r3 = ByteStringRange.create("q", "z");
        assertThat(r1).isEqualTo(r2);
        assertThat(r2).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(r3);
    }

    @Test
    public void byteStringSerializationTest() throws IOException, ClassNotFoundException {
        ByteStringRange expected = ByteStringRange.create("a", "z");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(expected);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ByteStringRange actual = ((ByteStringRange) (ois.readObject()));
        assertThat(actual).isEqualTo(expected);
    }
}

