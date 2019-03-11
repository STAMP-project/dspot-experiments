/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.filemove;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SourceSimilarityImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SourceSimilarityImpl underTest = new SourceSimilarityImpl();

    @Test
    public void zero_if_fully_different() {
        List<String> left = Arrays.asList("a", "b", "c");
        List<String> right = Arrays.asList("d", "e");
        assertThat(underTest.score(left, right)).isEqualTo(0);
    }

    @Test
    public void one_hundred_if_same() {
        assertThat(underTest.score(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"))).isEqualTo(100);
        assertThat(underTest.score(Arrays.asList(""), Arrays.asList(""))).isEqualTo(100);
    }

    @Test
    public void partially_same() {
        assertThat(underTest.score(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "b", "e", "f"))).isEqualTo(50);
        assertThat(underTest.score(Arrays.asList("a"), Arrays.asList("a", "b", "c"))).isEqualTo(33);
        assertThat(underTest.score(Arrays.asList("a", "b", "c"), Arrays.asList("a"))).isEqualTo(33);
    }

    @Test
    public void finding_threshold_in_line_count_to_go_below_85_score() {
        assertThat(underTest.score(SourceSimilarityImplTest.listOf(100), SourceSimilarityImplTest.listOf(115))).isEqualTo(86);
        assertThat(underTest.score(SourceSimilarityImplTest.listOf(100), SourceSimilarityImplTest.listOf(116))).isEqualTo(86);
        assertThat(underTest.score(SourceSimilarityImplTest.listOf(100), SourceSimilarityImplTest.listOf(117))).isEqualTo(85);// 85.47% - 117%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(100), SourceSimilarityImplTest.listOf(118))).isEqualTo(84);// 84.74% - 118%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(50), SourceSimilarityImplTest.listOf(58))).isEqualTo(86);// 86.20% - 116%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(50), SourceSimilarityImplTest.listOf(59))).isEqualTo(84);// 84.74% - 118%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(25), SourceSimilarityImplTest.listOf(29))).isEqualTo(86);// 86.20% - 116%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(25), SourceSimilarityImplTest.listOf(30))).isEqualTo(83);// 83.33% - 120%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(12), SourceSimilarityImplTest.listOf(14))).isEqualTo(85);// 85.71% - 116.67%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(12), SourceSimilarityImplTest.listOf(15))).isEqualTo(80);// 80.00% - 125%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(10), SourceSimilarityImplTest.listOf(11))).isEqualTo(90);// 90.90% - 110%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(10), SourceSimilarityImplTest.listOf(12))).isEqualTo(83);// 83.33% - 120%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(5), SourceSimilarityImplTest.listOf(5))).isEqualTo(100);// 100% - 100%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(5), SourceSimilarityImplTest.listOf(6))).isEqualTo(83);// 83.33% - 120%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(200), SourceSimilarityImplTest.listOf(234))).isEqualTo(85);// 85.47% - 117%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(200), SourceSimilarityImplTest.listOf(236))).isEqualTo(84);// 84.75% - 118%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(300), SourceSimilarityImplTest.listOf(352))).isEqualTo(85);// 85.23% - 117.33%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(300), SourceSimilarityImplTest.listOf(354))).isEqualTo(84);// 84.74% - 118%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(400), SourceSimilarityImplTest.listOf(470))).isEqualTo(85);// 85.10% - 117.50%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(400), SourceSimilarityImplTest.listOf(471))).isEqualTo(84);// 84.92% - 117.75%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(500), SourceSimilarityImplTest.listOf(588))).isEqualTo(85);// 85.03% - 117.60%

        assertThat(underTest.score(SourceSimilarityImplTest.listOf(500), SourceSimilarityImplTest.listOf(589))).isEqualTo(84);// 84.88% - 117.80%

    }

    @Test
    public void verify_84_percent_ratio_for_lower_bound() {
        IntStream.range(0, 1000).forEach(( ref) -> lowerBoundGivesNonMeaningfulScore(ref, 0.84));
    }

    @Test
    public void verify_118_percent_ratio_for_upper_bound() {
        IntStream.range(0, 1000).forEach(( ref) -> upperBoundGivesNonMeaningfulScore(ref, 1.18));
    }

    @Test
    public void two_empty_lists_are_not_considered_as_equal() {
        assertThat(underTest.score(Collections.emptyList(), Collections.emptyList())).isEqualTo(0);
    }
}

