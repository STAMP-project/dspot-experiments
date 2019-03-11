/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.heuristic.selector.move.composite;


import org.junit.Test;


public class CartesianProductMoveSelectorTest {
    @Test
    public void originSelectionNotIgnoringEmpty() {
        originSelection(false);
    }

    @Test
    public void originSelectionIgnoringEmpty() {
        originSelection(true);
    }

    @Test
    public void emptyFirstOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, true, false);
    }

    @Test
    public void emptyFirstOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, true, false);
    }

    @Test
    public void emptySecondOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, false, true);
    }

    @Test
    public void emptySecondOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, false, true);
    }

    @Test
    public void emptyAllOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, true, true);
    }

    @Test
    public void emptyAllOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, true, true);
    }

    @Test
    public void originSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        originSelection3ChildMoveSelectors(false);
    }

    @Test
    public void originSelection3ChildMoveSelectorsIgnoringEmpty() {
        originSelection3ChildMoveSelectors(true);
    }

    @Test
    public void emptyOriginSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        emptyOriginSelection3ChildMoveSelectors(false);
    }

    @Test
    public void emptyOriginSelection3ChildMoveSelectorsIgnoringEmpty() {
        emptyOriginSelection3ChildMoveSelectors(true);
    }

    @Test
    public void classicRandomSelectionNotIgnoringEmpty() {
        classicRandomSelection(false);
    }

    @Test
    public void classicRandomSelectionIgnoringEmpty() {
        classicRandomSelection(true);
    }

    @Test
    public void emptyRandomSelectionNotIgnoringEmpty() {
        emptyRandomSelection(false);
    }

    @Test
    public void emptyRandomSelectionIgnoringEmpty() {
        emptyRandomSelection(true);
    }

    @Test
    public void randomSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        randomSelection3ChildMoveSelectors(false);
    }

    @Test
    public void randomSelection3ChildMoveSelectorsIgnoringEmpty() {
        randomSelection3ChildMoveSelectors(true);
    }

    @Test
    public void emptyRandomSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        emptyRandomSelection3ChildMoveSelectors(false);
    }

    @Test
    public void emptyRandomSelection3ChildMoveSelectorsIgnoringEmpty() {
        emptyRandomSelection3ChildMoveSelectors(true);
    }

    // ************************************************************************
    // Integration with mimic
    // ************************************************************************
    @Test
    public void originalMimicNotIgnoringEmpty() {
        originalMimic(false);
    }

    @Test
    public void originalMimicIgnoringEmpty() {
        originalMimic(true);
    }

    @Test
    public void randomMimicNotIgnoringEmpty() {
        randomMimic(false);
    }

    @Test
    public void randomMimicIgnoringEmpty() {
        randomMimic(true);
    }
}

