/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.command.runtime.rule;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;


@SuppressWarnings("unchecked")
public class GetFactHandlesCommandTest {
    private KieSession ksession;

    private ExecutableRunner runner;

    private Context context;

    private Random random = new Random();

    @Test
    public void getEmptyFactHandlesTest() {
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        if (result instanceof Collection<?>) {
            Assert.assertNotNull(result);
            Assert.assertTrue(((Collection<?>) (result)).isEmpty());
        } else {
            Assert.fail("result of command was NOT a collection of FactHandles");
        }
    }

    @Test
    public void getOneFactHandleTest() {
        String randomFact = "" + (random.nextLong());
        ksession.insert(randomFact);
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
    }

    @Test
    public void getMultipleFactHandleTest() {
        HashSet<String> factSet = new HashSet<String>();
        int numFacts = 4;
        for (int i = 0; i < numFacts; ++i) {
            factSet.add(("" + (random.nextInt())));
        }
        for (String fact : factSet) {
            ksession.insert(fact);
        }
        GetFactHandlesCommand command = new GetFactHandlesCommand();
        Object result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
    }

    @Test
    public void getEmptyDisconnectedFactHandlesTest() {
        GetFactHandlesCommand command = new GetFactHandlesCommand(true);
        Object result = runner.execute(command, context);
        if (result instanceof Collection<?>) {
            Assert.assertNotNull(result);
            Assert.assertTrue(((Collection<?>) (result)).isEmpty());
        } else {
            Assert.fail("result of command was NOT a collection of FactHandles");
        }
    }

    @Test
    public void getOneDisconnectedFactHandleTest() {
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        String randomFact = "" + (random.nextLong());
        ksession.insert(randomFact);
        // Retrieve and verify fact handle collections
        GetFactHandlesCommand command = new GetFactHandlesCommand(false);
        Object result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle factHandle = ((FactHandle) (((Collection<FactHandle>) (result)).toArray()[0]));
        command = new GetFactHandlesCommand(false);
        result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle connectedFactHandle = ((FactHandle) (((Collection<FactHandle>) (result)).toArray()[0]));
        command = new GetFactHandlesCommand(true);
        result = runner.execute(command, context);
        verifyThatCollectionContains1FactHandleWithThisFact(randomFact, result);
        FactHandle disconnectedFactHandle = ((FactHandle) (((Collection<FactHandle>) (result)).toArray()[0]));
        // Test fact handle collections
        Assert.assertTrue((factHandle == connectedFactHandle));
        Assert.assertTrue((!(factHandle == disconnectedFactHandle)));
    }

    @Test
    public void getMultipleDisconnectedFactHandleTest() {
        System.out.println(("\nTest: " + (Thread.currentThread().getStackTrace()[1].getMethodName())));
        HashSet<String> factSet = new HashSet<String>();
        int numFacts = 4;
        for (int i = 0; i < numFacts; ++i) {
            factSet.add(("" + (random.nextInt())));
        }
        for (String fact : factSet) {
            ksession.insert(fact);
        }
        GetFactHandlesCommand command = new GetFactHandlesCommand(false);
        Object result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> factHandles = ((Collection<FactHandle>) (result));
        command = new GetFactHandlesCommand(false);
        result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> connectedFactHandles = ((Collection<FactHandle>) (result));
        command = new GetFactHandlesCommand(true);
        result = runner.execute(command, context);
        verifyThatCollectionContainsTheseFactHandle(factSet, result);
        Collection<FactHandle> disconnectedFactHandles = ((Collection<FactHandle>) (result));
        // Test fact handle collections
        HashSet<FactHandle> factHandlesCopy = new HashSet<FactHandle>(factHandles);
        for (int i = 0; i < (connectedFactHandles.size()); ++i) {
            for (Object connectedFact : connectedFactHandles) {
                Iterator<FactHandle> iter = factHandlesCopy.iterator();
                while (iter.hasNext()) {
                    Object fact = iter.next();
                    if (fact == connectedFact) {
                        iter.remove();
                    }
                } 
            }
        }
        Assert.assertTrue(factHandlesCopy.isEmpty());
        for (int i = 0; i < (disconnectedFactHandles.size()); ++i) {
            for (Object disconnectedFact : disconnectedFactHandles) {
                for (Object fact : factHandles) {
                    Assert.assertTrue((!(fact == disconnectedFact)));
                }
            }
        }
        Assert.assertTrue(((factHandles.size()) == (disconnectedFactHandles.size())));
    }
}

