/**
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.characters;


import MovementMode.CLIMBING;
import MovementMode.CROUCHING;
import MovementMode.DIVING;
import MovementMode.FLYING;
import MovementMode.GHOSTING;
import MovementMode.SWIMMING;
import MovementMode.WALKING;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class KinematicCharacterMoverTest {
    @Test
    public void testUpdateMode() {
        CharacterStateEvent state = new CharacterStateEvent();
        state.setMode(WALKING);
        KinematicCharacterMover.updateMode(state, false, true, true, false);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, true, false, true, false);
        Assert.assertSame(SWIMMING, state.getMode());
        state.setMode(FLYING);
        KinematicCharacterMover.updateMode(state, false, true, true, false);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, false);
        Assert.assertSame(WALKING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, true, false);
        Assert.assertSame(CLIMBING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, false);
        Assert.assertSame(WALKING, state.getMode());
        state.setMode(GHOSTING);
        KinematicCharacterMover.updateMode(state, false, true, false, false);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, true, false);
        Assert.assertSame(CLIMBING, state.getMode());
        state.setMode(WALKING);
        KinematicCharacterMover.updateMode(state, false, true, true, true);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, true, false, true, true);
        Assert.assertSame(SWIMMING, state.getMode());
        state.setMode(FLYING);
        KinematicCharacterMover.updateMode(state, false, true, true, true);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, true);
        Assert.assertSame(CROUCHING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, true, true);
        Assert.assertSame(CLIMBING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, true);
        Assert.assertSame(CROUCHING, state.getMode());
        state.setMode(GHOSTING);
        KinematicCharacterMover.updateMode(state, false, true, false, true);
        Assert.assertSame(DIVING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, true, true);
        Assert.assertSame(CLIMBING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, false);
        Assert.assertSame(WALKING, state.getMode());
        KinematicCharacterMover.updateMode(state, false, false, false, true);
        Assert.assertSame(CROUCHING, state.getMode());
    }
}

