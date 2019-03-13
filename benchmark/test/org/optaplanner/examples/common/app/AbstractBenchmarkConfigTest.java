/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.examples.common.app;


import CommonBenchmarkApp.ArgOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;


@RunWith(Parameterized.class)
public abstract class AbstractBenchmarkConfigTest {
    protected ArgOption argOption;

    protected AbstractBenchmarkConfigTest(CommonBenchmarkApp.ArgOption argOption) {
        this.argOption = argOption;
    }

    @Test
    public void buildPlannerBenchmark() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = argOption.buildPlannerBenchmarkFactory();
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        buildEverySolver(plannerBenchmark);
    }
}

