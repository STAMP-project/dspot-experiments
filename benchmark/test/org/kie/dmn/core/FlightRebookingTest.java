/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core;


import DMNDecisionResult.DecisionEvaluationStatus.SKIPPED;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;


public class FlightRebookingTest extends BaseInterpretedVsCompiledTest {
    public FlightRebookingTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testSolution1() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));// need proper type support to enable this

        final DMNContext context = DMNFactory.newContext();
        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();
        context.set("Passenger List", passengerList);
        context.set("Flight List", flightList);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Rebooked Passengers"), CoreMatchers.is(loadExpectedResult()));
    }

    @Test
    public void testSolutionAlternate() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-alternative.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();
        context.set("Passenger List", passengerList);
        context.set("Flight List", flightList);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Rebooked Passengers"), CoreMatchers.is(loadExpectedResult()));
    }

    @Test
    public void testSolutionSingletonLists() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-singleton-lists.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();
        context.set("Passenger List", passengerList);
        context.set("Flight List", flightList);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Rebooked Passengers"), CoreMatchers.is(loadExpectedResult()));
    }

    @Test
    public void testSolutionBadExample() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-bad-example.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0019-flight-rebooking");
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();
        context.set("Passenger List", passengerList);
        context.set("Flight List", flightList);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        Assert.assertThat(result.get("Rebooked Passengers"), CoreMatchers.is(loadExpectedResult()));
    }

    @Test
    public void testUninterpreted() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0019-flight-rebooking-uninterpreted.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_188d6caf-a355-49b5-a692-bd6ce713da08", "0019-flight-rebooking");
        runtime.addListener(DMNRuntimeUtil.createListener());
        Assert.assertThat(dmnModel, CoreMatchers.notNullValue());
        Assert.assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), CoreMatchers.is(false));
        final DMNContext context = DMNFactory.newContext();
        final List passengerList = loadPassengerList();
        final List flightList = loadFlightList();
        context.set("Passenger List", passengerList);
        context.set("Flight List", flightList);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        Assert.assertThat(dmnResult.getDecisionResultByName("Rebooked Passengers").getEvaluationStatus(), CoreMatchers.is(SKIPPED));
    }
}

