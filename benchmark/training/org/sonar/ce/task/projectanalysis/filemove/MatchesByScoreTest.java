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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.filemove.ScoreMatrix.ScoreFile;


public class MatchesByScoreTest {
    private static final List<Match> NO_MATCH = null;

    @Test
    public void creates_returns_always_the_same_instance_of_maxScore_is_less_than_min_required_score() {
        ScoreFile[] doesNotMatterRemovedFiles = new ScoreFile[0];
        ScoreFile[] doesNotMatterNewFiles = new ScoreFile[0];
        int[][] doesNotMatterScores = new int[0][0];
        ScoreMatrix scoreMatrix1 = new ScoreMatrix(doesNotMatterRemovedFiles, doesNotMatterNewFiles, doesNotMatterScores, ((FileMoveDetectionStep.MIN_REQUIRED_SCORE) - 1));
        MatchesByScore matchesByScore = MatchesByScore.create(scoreMatrix1);
        assertThat(matchesByScore.getSize()).isEqualTo(0);
        assertThat(matchesByScore).isEmpty();
        ScoreMatrix scoreMatrix2 = new ScoreMatrix(doesNotMatterRemovedFiles, doesNotMatterNewFiles, doesNotMatterScores, ((FileMoveDetectionStep.MIN_REQUIRED_SCORE) - 5));
        assertThat(MatchesByScore.create(scoreMatrix2)).isSameAs(matchesByScore);
    }

    @Test
    public void creates_supports_score_with_same_value_as_min_required_score() {
        int maxScore = 92;
        int[][] scores = new int[][]{ new int[]{ maxScore }, new int[]{ 8 }, new int[]{ 85 } };
        MatchesByScore matchesByScore = MatchesByScore.create(new ScoreMatrix(of("A", "B", "C"), of("1"), scores, maxScore));
        assertThat(matchesByScore.getSize()).isEqualTo(2);
        assertThat(Lists.newArrayList(matchesByScore)).isEqualTo(// 92
        // 85
        Arrays.asList(ImmutableList.of(new Match("A", "1")), MatchesByScoreTest.NO_MATCH, MatchesByScoreTest.NO_MATCH, MatchesByScoreTest.NO_MATCH, MatchesByScoreTest.NO_MATCH, MatchesByScoreTest.NO_MATCH, MatchesByScoreTest.NO_MATCH, ImmutableList.of(new Match("C", "1"))));
    }
}

