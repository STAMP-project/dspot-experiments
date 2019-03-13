/**
 * Copyright 2012 the original author or authors.
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
package org.springframework.batch.core.configuration.support;


import java.util.Collection;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.StepRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;


/**
 *
 *
 * @author Sebastien Gerard
 */
public class MapStepRegistryTests {
    private static final String EXCEPTION_NOT_THROWN_MSG = "An exception should have been thrown";

    @Test
    public void registerStepEmptyCollection() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        launchRegisterGetRegistered(stepRegistry, "myJob", getStepCollection());
    }

    @Test
    public void registerStepNullJobName() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        try {
            stepRegistry.register(null, new HashSet());
            Assert.fail(MapStepRegistryTests.EXCEPTION_NOT_THROWN_MSG);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void registerStepNullSteps() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        try {
            stepRegistry.register("fdsfsd", null);
            Assert.fail(MapStepRegistryTests.EXCEPTION_NOT_THROWN_MSG);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void registerStepGetStep() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        launchRegisterGetRegistered(stepRegistry, "myJob", getStepCollection(createStep("myStep"), createStep("myOtherStep"), createStep("myThirdStep")));
    }

    @Test
    public void getJobNotRegistered() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        final String aStepName = "myStep";
        launchRegisterGetRegistered(stepRegistry, "myJob", getStepCollection(createStep(aStepName), createStep("myOtherStep"), createStep("myThirdStep")));
        assertJobNotRegistered(stepRegistry, "a ghost");
    }

    @Test
    public void getJobNotRegisteredNoRegistration() {
        final StepRegistry stepRegistry = createRegistry();
        assertJobNotRegistered(stepRegistry, "a ghost");
    }

    @Test
    public void getStepNotRegistered() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        final String jobName = "myJob";
        launchRegisterGetRegistered(stepRegistry, jobName, getStepCollection(createStep("myStep"), createStep("myOtherStep"), createStep("myThirdStep")));
        assertStepNameNotRegistered(stepRegistry, jobName, "fsdfsdfsdfsd");
    }

    @Test
    public void registerTwice() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        final String jobName = "myJob";
        final Collection<Step> stepsFirstRegistration = getStepCollection(createStep("myStep"), createStep("myOtherStep"), createStep("myThirdStep"));
        // first registration
        launchRegisterGetRegistered(stepRegistry, jobName, stepsFirstRegistration);
        // Second registration with same name should fail
        try {
            stepRegistry.register(jobName, getStepCollection(createStep("myFourthStep"), createStep("lastOne")));
            Assert.fail(("Should have failed with a " + (DuplicateJobException.class.getSimpleName())));
        } catch (DuplicateJobException e) {
            // OK
        }
    }

    @Test
    public void getStepNullJobName() throws NoSuchJobException {
        final StepRegistry stepRegistry = createRegistry();
        try {
            stepRegistry.getStep(null, "a step");
            Assert.fail(MapStepRegistryTests.EXCEPTION_NOT_THROWN_MSG);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getStepNullStepName() throws DuplicateJobException, NoSuchJobException {
        final StepRegistry stepRegistry = createRegistry();
        final String stepName = "myStep";
        launchRegisterGetRegistered(stepRegistry, "myJob", getStepCollection(createStep(stepName)));
        try {
            stepRegistry.getStep(null, stepName);
            Assert.fail(MapStepRegistryTests.EXCEPTION_NOT_THROWN_MSG);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void registerStepUnregisterJob() throws DuplicateJobException {
        final StepRegistry stepRegistry = createRegistry();
        final Collection<Step> steps = getStepCollection(createStep("myStep"), createStep("myOtherStep"), createStep("myThirdStep"));
        final String jobName = "myJob";
        launchRegisterGetRegistered(stepRegistry, jobName, steps);
        stepRegistry.unregisterStepsFromJob(jobName);
        assertJobNotRegistered(stepRegistry, jobName);
    }

    @Test
    public void unregisterJobNameNull() {
        final StepRegistry stepRegistry = createRegistry();
        try {
            stepRegistry.unregisterStepsFromJob(null);
            Assert.fail(MapStepRegistryTests.EXCEPTION_NOT_THROWN_MSG);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void unregisterNoRegistration() {
        final StepRegistry stepRegistry = createRegistry();
        assertJobNotRegistered(stepRegistry, "a job");
    }
}

