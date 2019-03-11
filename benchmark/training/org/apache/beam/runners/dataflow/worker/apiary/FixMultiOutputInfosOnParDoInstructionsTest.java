/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.apiary;


import com.google.api.services.dataflow.model.MapTask;
import org.apache.beam.sdk.fn.IdGenerators;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FixMultiOutputInfosOnParDoInstructions}.
 */
@RunWith(JUnit4.class)
public class FixMultiOutputInfosOnParDoInstructionsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExistingMultiOutputInfosAreUnmodified() {
        FixMultiOutputInfosOnParDoInstructions function = new FixMultiOutputInfosOnParDoInstructions(IdGenerators.decrementingLongs());
        MapTask output = function.apply(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(2, "5", "6"));
        Assert.assertEquals(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(2, "5", "6"), output);
    }

    @Test
    public void testDefaultOutputIsAddedIfOnlySingleOutput() {
        FixMultiOutputInfosOnParDoInstructions function = new FixMultiOutputInfosOnParDoInstructions(IdGenerators.decrementingLongs());
        MapTask output = function.apply(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1));
        Assert.assertEquals(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1, "-1"), output);
    }

    @Test
    public void testDefaultOutputHasDifferentIdsForEachMapTask() {
        FixMultiOutputInfosOnParDoInstructions function = new FixMultiOutputInfosOnParDoInstructions(IdGenerators.decrementingLongs());
        MapTask output = function.apply(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1));
        Assert.assertEquals(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1, "-1"), output);
        output = function.apply(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1));
        Assert.assertEquals(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(1, "-2"), output);
    }

    @Test
    public void testMissingTagsForMultipleOutputsThrows() {
        FixMultiOutputInfosOnParDoInstructions function = new FixMultiOutputInfosOnParDoInstructions(IdGenerators.decrementingLongs());
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid ParDoInstruction");
        thrown.expectMessage("2 outputs specified");
        function.apply(FixMultiOutputInfosOnParDoInstructionsTest.createMapTaskWithParDo(2));
    }
}

