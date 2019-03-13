/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udf.string;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SplitKudfTest {
    private static final SplitKudf splitUdf = new SplitKudf();

    @Test
    public void shouldReturnNullOnAnyNullParameters() {
        Assert.assertThat(SplitKudfTest.splitUdf.split(null, ""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SplitKudfTest.splitUdf.split("", null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SplitKudfTest.splitUdf.split(null, null), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnOriginalStringOnNotFoundDelimiter() {
        Assert.assertThat(SplitKudfTest.splitUdf.split("", "."), Matchers.contains(""));
        Assert.assertThat(SplitKudfTest.splitUdf.split("x-y", "."), Matchers.contains("x-y"));
    }

    @Test
    public void shouldSplitAllCharactersByGivenAnEmptyDelimiter() {
        Assert.assertThat(SplitKudfTest.splitUdf.split("", ""), Matchers.contains(""));
        Assert.assertThat(SplitKudfTest.splitUdf.split("x-y", ""), Matchers.contains("x", "-", "y"));
    }

    @Test
    public void shouldSplitStringByGivenDelimiter() {
        Assert.assertThat(SplitKudfTest.splitUdf.split("x-y", "-"), Matchers.contains("x", "y"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("x-y", "x"), Matchers.contains("", "-y"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("x-y", "y"), Matchers.contains("x-", ""));
        Assert.assertThat(SplitKudfTest.splitUdf.split("a.b.c.d", "."), Matchers.contains("a", "b", "c", "d"));
    }

    @Test
    public void shouldSplitAndAddEmptySpacesIfDelimiterIsFoundAtTheBeginningOrEnd() {
        Assert.assertThat(SplitKudfTest.splitUdf.split("$A", "$"), Matchers.contains("", "A"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("$A$B", "$"), Matchers.contains("", "A", "B"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("A$", "$"), Matchers.contains("A", ""));
        Assert.assertThat(SplitKudfTest.splitUdf.split("A$B$", "$"), Matchers.contains("A", "B", ""));
        Assert.assertThat(SplitKudfTest.splitUdf.split("$A$B$", "$"), Matchers.contains("", "A", "B", ""));
    }

    @Test
    public void shouldSplitAndAddEmptySpacesIfDelimiterIsFoundInContiguousPositions() {
        Assert.assertThat(SplitKudfTest.splitUdf.split("A||A", "|"), Matchers.contains("A", "", "A"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("z||A||z", "|"), Matchers.contains("z", "", "A", "", "z"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("||A||A", "|"), Matchers.contains("", "", "A", "", "A"));
        Assert.assertThat(SplitKudfTest.splitUdf.split("A||A||", "|"), Matchers.contains("A", "", "A", "", ""));
    }
}

