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
package com.google.cloud.bigtable.data.v2.internal;


import ByteStringComparator.INSTANCE;
import com.google.bigtable.v2.RowRange;
import com.google.bigtable.v2.RowSet;
import com.google.cloud.bigtable.data.v2.models.Range.ByteStringRange;
import com.google.common.collect.ImmutableSortedSet;
import com.google.protobuf.ByteString;
import java.util.SortedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RowSetUtilTest {
    @Test
    public void noSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("a")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("p")).setEndKeyOpen(ByteString.copyFromUtf8("q"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, rowSet);
    }

    @Test
    public void splitEmptyTest() {
        RowSet rowSet = RowSet.newBuilder().build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("a")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("a"))).build(), RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("a"))).build());
    }

    @Test
    public void splitMultipleKeysTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("1-beforeSplit")).addRowKeys(ByteString.copyFromUtf8("2-onSplit")).addRowKeys(ByteString.copyFromUtf8("3-afterSplit")).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("2-onSplit")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("1-beforeSplit")).addRowKeys(ByteString.copyFromUtf8("2-onSplit")).build(), RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("3-afterSplit")).build());
    }

    @Test
    public void splitKeysEmptyLeft() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("5-test")).addRowKeys(ByteString.copyFromUtf8("8-test")).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("0-split")).add(ByteString.copyFromUtf8("6-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, null, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("5-test")).build(), RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("8-test")).build());
    }

    @Test
    public void splitKeysEmptyRight() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-test")).addRowKeys(ByteString.copyFromUtf8("2-test")).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("1-split")).add(ByteString.copyFromUtf8("5-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-test")).build(), RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("2-test")).build(), null);
    }

    @Test
    public void rangeLeftOfSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-key")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("1-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("2-range-end"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("3-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-key")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("1-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("2-range-end"))).build(), null);
    }

    @Test
    public void unboundedRangeLeftOfSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setEndKeyOpen(ByteString.copyFromUtf8("1-range-end"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("5-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setEndKeyOpen(ByteString.copyFromUtf8("1-range-end"))).build(), null);
    }

    @Test
    public void rangeImmediatelyLeftOfSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-key")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("1-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("2-range-end"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("2-range-end")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0-key")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("1-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("2-range-end"))).build(), null);
    }

    @Test
    public void rangeRightOfSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("9-row-key")).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("5-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("7-range-end"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("3-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, null, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("9-row-key")).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("5-range-start")).setEndKeyOpen(ByteString.copyFromUtf8("7-range-end"))).build());
    }

    @Test
    public void unboundedRightOfSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("5-range-start"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("3-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, null, RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("5-range-start"))).build());
    }

    @Test
    public void rangeExactlyFitsSplitTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("5-split")).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("3-split")).setEndKeyClosed(ByteString.copyFromUtf8("5-split"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("3-split")).add(ByteString.copyFromUtf8("5-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, null, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("5-split")).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("3-split")).setEndKeyClosed(ByteString.copyFromUtf8("5-split"))).build(), null);
    }

    @Test
    public void startOnSplitPointTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("3-split"))).build();
        // Inclusive start on a split point should generate 2 segments
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("3-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("3-split")).setEndKeyClosed(ByteString.copyFromUtf8("3-split"))).build(), RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("3-split"))).build());
    }

    @Test
    public void mixedSplitTest() {
        RowSet rowSet = // Range 5: fully in "j"
        // Range 4: fully in "j"
        // Range 3: split between segment "d" & "j"
        // Range 2: split between segment "a" & "d"
        // Range 1: fully in "a" segment
        RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0")).addRowKeys(ByteString.copyFromUtf8("a")).addRowKeys(ByteString.copyFromUtf8("c")).addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("a"))).addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("b"))).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("c")).setEndKeyClosed(ByteString.copyFromUtf8("e"))).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("d")).setEndKeyClosed(ByteString.copyFromUtf8("f"))).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("m"))).build();
        SortedSet<ByteString> splitPoints = // Split the unbounded
        ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("a")).add(ByteString.copyFromUtf8("d")).add(ByteString.copyFromUtf8("j")).add(ByteString.copyFromUtf8("o")).build();
        // Split "a"
        // Split "d"
        // Split "j"
        // Split "o"
        // Remainder
        RowSetUtilTest.verifySplit(rowSet, splitPoints, // Range 2: part1
        // Range 1
        RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("0")).addRowKeys(ByteString.copyFromUtf8("a")).addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("a"))).addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("a"))).build(), // Range 3: part 1
        // Range 2: part 2
        RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("c")).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("a")).setEndKeyClosed(ByteString.copyFromUtf8("b"))).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("c")).setEndKeyClosed(ByteString.copyFromUtf8("d"))).build(), // Range 4
        // Range 3: part 2
        RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("d")).setEndKeyClosed(ByteString.copyFromUtf8("e"))).addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("d")).setEndKeyClosed(ByteString.copyFromUtf8("f"))).build(), // Range 5: part1
        RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("m")).setEndKeyClosed(ByteString.copyFromUtf8("o"))).build(), // Range 5: part2
        RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("o"))).build());
    }

    @Test
    public void unsortedRequestTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("7-row-key-1")).addRowKeys(ByteString.copyFromUtf8("2-row-key-2")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("8-range-1-start")).setEndKeyOpen(ByteString.copyFromUtf8("9-range-1-end"))).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("3-range-2-start")).setEndKeyOpen(ByteString.copyFromUtf8("4-range-2-end"))).build();
        SortedSet<ByteString> splitPoints = ImmutableSortedSet.orderedBy(INSTANCE).add(ByteString.copyFromUtf8("5-split")).build();
        RowSetUtilTest.verifySplit(rowSet, splitPoints, RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("2-row-key-2")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("3-range-2-start")).setEndKeyOpen(ByteString.copyFromUtf8("4-range-2-end"))).build(), RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("7-row-key-1")).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("8-range-1-start")).setEndKeyOpen(ByteString.copyFromUtf8("9-range-1-end"))).build());
    }

    @Test
    public void emptyBoundTest() {
        RowSet rowSet = RowSet.getDefaultInstance();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded());
    }

    @Test
    public void singleKeyBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("a")).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startClosed("a").endClosed("a"));
    }

    @Test
    public void multiKeyBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowKeys(ByteString.copyFromUtf8("a")).addRowKeys(ByteString.copyFromUtf8("d")).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startClosed("a").endClosed("d"));
    }

    @Test
    public void singleClosedClosedRangeBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("a")).setEndKeyClosed(ByteString.copyFromUtf8("b"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startClosed("a").endClosed("b"));
    }

    @Test
    public void singleClosedOpenRangeBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("a")).setEndKeyOpen(ByteString.copyFromUtf8("b"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startClosed("a").endOpen("b"));
    }

    @Test
    public void singleOpenOpenRangeBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("a")).setEndKeyOpen(ByteString.copyFromUtf8("b"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startOpen("a").endOpen("b"));
    }

    @Test
    public void singleRangeOpenClosedBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyOpen(ByteString.copyFromUtf8("a")).setEndKeyClosed(ByteString.copyFromUtf8("b"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startOpen("a").endClosed("b"));
    }

    @Test
    public void singleRangeUnbounded1BoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("a"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().startClosed("a"));
    }

    @Test
    public void singleRangeUnbounded2BoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setEndKeyClosed(ByteString.copyFromUtf8("z"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.unbounded().endClosed("z"));
    }

    @Test
    public void multipleRangeBoundTest() {
        RowSet rowSet = RowSet.newBuilder().addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("a")).setEndKeyOpen(ByteString.copyFromUtf8("m"))).addRowRanges(RowRange.newBuilder().setStartKeyClosed(ByteString.copyFromUtf8("q")).setEndKeyOpen(ByteString.copyFromUtf8("z"))).build();
        ByteStringRange actual = RowSetUtil.getBound(rowSet);
        assertThat(actual).isEqualTo(ByteStringRange.create("a", "z"));
    }
}

