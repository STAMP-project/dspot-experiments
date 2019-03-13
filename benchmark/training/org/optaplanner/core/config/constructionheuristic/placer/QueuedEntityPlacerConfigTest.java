/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.config.constructionheuristic.placer;


import java.util.Arrays;
import org.junit.Test;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;


public class QueuedEntityPlacerConfigTest {
    @Test
    public void unfoldNewSequential() {
        SolutionDescriptor<TestdataMultiVarSolution> solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        ChangeMoveSelectorConfig primaryMoveSelectorConfig = new ChangeMoveSelectorConfig();
        primaryMoveSelectorConfig.setValueSelectorConfig(new ValueSelectorConfig("primaryValue"));
        ChangeMoveSelectorConfig secondaryMoveSelectorConfig = new ChangeMoveSelectorConfig();
        secondaryMoveSelectorConfig.setValueSelectorConfig(new ValueSelectorConfig("secondaryValue"));
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy(solutionDescriptor);
        QueuedEntityPlacerConfig placerConfig = QueuedEntityPlacerConfig.unfoldNew(configPolicy, Arrays.asList(primaryMoveSelectorConfig, secondaryMoveSelectorConfig));
        QueuedEntityPlacer entityPlacer = placerConfig.buildEntityPlacer(configPolicy);
        // TODO assert placements. See also AbstractEntityPlacerTest.assertEntityPlacement()
    }
}

