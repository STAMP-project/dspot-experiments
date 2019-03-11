/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.pipelineprocessor.processors;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.graylog.plugins.pipelineprocessor.ast.Pipeline;
import org.graylog.plugins.pipelineprocessor.ast.Stage;
import org.jooq.lambda.Seq;
import org.junit.Assert;
import org.junit.Test;


public class StageIteratorTest {
    @Test
    public void singleEmptyPipeline() {
        final ImmutableSet<Pipeline> empty = ImmutableSet.of(Pipeline.empty("empty"));
        final StageIterator iterator = new StageIterator(empty);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void singlePipelineNoStage() {
        final ImmutableSet<Pipeline> input = ImmutableSet.of(Pipeline.builder().name("hallo").stages(ImmutableSortedSet.of(Stage.builder().stage(0).matchAll(true).ruleReferences(Collections.emptyList()).build())).build());
        final StageIterator iterator = new StageIterator(input);
        Assert.assertTrue(iterator.hasNext());
        final List<Stage> nextStages = iterator.next();
        Assert.assertEquals(1, nextStages.size());
        final Stage stage = Iterables.getOnlyElement(nextStages);
        Assert.assertEquals(0, stage.ruleReferences().size());
    }

    @Test
    public void singlePipelineTwoStages() {
        final ImmutableSet<Pipeline> input = ImmutableSet.of(Pipeline.builder().name("hallo").stages(ImmutableSortedSet.of(Stage.builder().stage(0).matchAll(true).ruleReferences(Collections.emptyList()).build(), Stage.builder().stage(10).matchAll(true).ruleReferences(Collections.emptyList()).build())).build());
        final StageIterator iterator = new StageIterator(input);
        // noinspection unchecked
        final List<Stage>[] stages = Iterators.toArray(iterator, List.class);
        Assert.assertEquals(2, stages.length);
        Assert.assertEquals(1, stages[0].size());
        Assert.assertEquals("last set of stages are on stage 0", 0, Iterables.getOnlyElement(stages[0]).stage());
        Assert.assertEquals(1, stages[1].size());
        Assert.assertEquals("last set of stages are on stage 1", 10, Iterables.getOnlyElement(stages[1]).stage());
    }

    @Test
    public void multiplePipelines() {
        final ImmutableSortedSet<Stage> stages1 = ImmutableSortedSet.of(Stage.builder().stage(0).matchAll(true).ruleReferences(Collections.emptyList()).build(), Stage.builder().stage(10).matchAll(true).ruleReferences(Collections.emptyList()).build());
        final ImmutableSortedSet<Stage> stages2 = ImmutableSortedSet.of(Stage.builder().stage((-1)).matchAll(true).ruleReferences(Collections.emptyList()).build(), Stage.builder().stage(4).matchAll(true).ruleReferences(Collections.emptyList()).build(), Stage.builder().stage(11).matchAll(true).ruleReferences(Collections.emptyList()).build());
        final ImmutableSortedSet<Stage> stages3 = ImmutableSortedSet.of(Stage.builder().stage(0).matchAll(true).ruleReferences(Collections.emptyList()).build());
        final ImmutableSet<Pipeline> input = ImmutableSet.of(Pipeline.builder().name("p1").stages(stages1).build(), Pipeline.builder().name("p2").stages(stages2).build(), Pipeline.builder().name("p3").stages(stages3).build());
        final StageIterator iterator = new StageIterator(input);
        final List<List<Stage>> stageSets = Lists.newArrayList(iterator);
        Assert.assertEquals("5 different stages to execute", 5, stageSets.size());
        for (List<Stage> stageSet : stageSets) {
            Assert.assertEquals("Each stage set should only contain stages with the same number", 1, Seq.seq(stageSet).groupBy(Stage::stage).keySet().size());
        }
        Assert.assertArrayEquals("Stages must be sorted numerically", new int[]{ -1, 0, 4, 10, 11 }, stageSets.stream().flatMap(Collection::stream).mapToInt(Stage::stage).distinct().toArray());
    }
}

