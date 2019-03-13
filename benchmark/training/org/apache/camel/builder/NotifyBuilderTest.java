/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.builder;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class NotifyBuilderTest extends ContextTestSupport {
    @Test
    public void testMustBeCreated() throws Exception {
        NotifyBuilder notify = whenDone(1);
        try {
            notify.matches();
            Assert.fail("Should have thrown an exception");
        } catch (IllegalStateException e) {
            Assert.assertEquals("NotifyBuilder has not been created. Invoke the create() method before matching.", e.getMessage());
        }
    }

    @Test
    public void testDestroyUnregistersBuilder() throws Exception {
        // Given:
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
        // When:
        int withReg = context.getManagementStrategy().getEventNotifiers().size();
        notify.destroy();
        int afterDestroy = context.getManagementStrategy().getEventNotifiers().size();
        // Then:
        Assert.assertEquals((withReg - afterDestroy), 1);
    }

    @Test
    public void testDestroyResetsBuilder() throws Exception {
        // Given:
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
        // When:
        notify.destroy();
        // Then:
        try {
            notify.matches();
            Assert.fail("Should have thrown an exception");
        } catch (IllegalStateException e) {
            Assert.assertEquals("NotifyBuilder has not been created. Invoke the create() method before matching.", e.getMessage());
        }
    }

    @Test
    public void testDestroyedBuilderCannotBeRecreated() throws Exception {
        // Given:
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();
        // When:
        notify.destroy();
        // Then:
        try {
            notify.create();
            Assert.fail("Should have thrown an exception");
        } catch (IllegalStateException e) {
            Assert.assertEquals("A destroyed NotifyBuilder cannot be re-created.", e.getMessage());
        }
    }

    @Test
    public void testDirectWhenExchangeDoneSimple() throws Exception {
        NotifyBuilder notify = from("direct:foo").whenDone(1).create();
        Assert.assertEquals("from(direct:foo).whenDone(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testDirectBeerWhenExchangeDoneSimple() throws Exception {
        NotifyBuilder notify = from("direct:beer").whenDone(1).create();
        Assert.assertEquals("from(direct:beer).whenDone(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "A");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testDirectFromRoute() throws Exception {
        NotifyBuilder notify = fromRoute("foo").whenDone(1).create();
        Assert.assertEquals("fromRoute(foo).whenDone(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "B");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testDirectFromRouteReceived() throws Exception {
        NotifyBuilder notify = fromRoute("foo").whenReceived(1).create();
        Assert.assertEquals("fromRoute(foo).whenReceived(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "B");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeDone() throws Exception {
        NotifyBuilder notify = from("direct:foo").whenDone(5).create();
        Assert.assertEquals("from(direct:foo).whenDone(5)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "F");
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "H");
        template.sendBody("direct:bar", "I");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeDoneAnd() throws Exception {
        NotifyBuilder notify = from("direct:bar").whenDone(7).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "F");
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "H");
        template.sendBody("direct:bar", "I");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "J");
        template.sendBody("direct:bar", "K");
        template.sendBody("direct:bar", "L");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testFromRouteWhenExchangeDoneAnd() throws Exception {
        NotifyBuilder notify = fromRoute("bar").whenDone(7).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "F");
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "H");
        template.sendBody("direct:bar", "I");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "J");
        template.sendBody("direct:bar", "K");
        template.sendBody("direct:bar", "L");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testFromRouteAndNot() throws Exception {
        NotifyBuilder notify = fromRoute("cake").whenDone(1).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "C");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "D");
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(true, notify.matches());
        // and now the cake to make it false
        template.sendBody("direct:cake", "F");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenExchangeDoneOr() throws Exception {
        NotifyBuilder notify = from("direct:bar").whenDone(7).create();
        Assert.assertEquals("from(direct:foo).whenDone(5).or().from(direct:bar).whenDone(7)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "I");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "J");
        template.sendBody("direct:bar", "K");
        template.sendBody("direct:bar", "L");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeDoneNot() throws Exception {
        NotifyBuilder notify = from("direct:bar").whenDone(1).create();
        Assert.assertEquals("from(direct:foo).whenDone(5).not().from(direct:bar).whenDone(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:foo", "D");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "E");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "F");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenExchangeDoneOrFailure() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(5).or().whenFailed(1).create();
        Assert.assertEquals("whenDone(5).or().whenFailed(1)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "D");
        Assert.assertEquals(false, notify.matches());
        try {
            template.sendBody("direct:fail", "E");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeDoneNotFailure() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(5).not().whenFailed(1).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "D");
        template.sendBody("direct:bar", "E");
        template.sendBody("direct:bar", "F");
        Assert.assertEquals(true, notify.matches());
        try {
            template.sendBody("direct:fail", "G");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testFilterWhenExchangeDone() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).filter(TestSupport.body().contains("World")).whenDone(3).create();
        Assert.assertEquals("filter(simple{${body}} contains World).whenDone(3)", notify.toString());
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        template.sendBody("direct:foo", "Hi World");
        template.sendBody("direct:foo", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "B");
        template.sendBody("direct:bar", "C");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "Bye World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "D");
        template.sendBody("direct:bar", "Hey World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testFromFilterWhenExchangeDone() throws Exception {
        NotifyBuilder notify = from("direct:foo").filter(TestSupport.body().contains("World")).whenDone(3).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        template.sendBody("direct:foo", "Hi World");
        template.sendBody("direct:foo", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "B");
        template.sendBody("direct:foo", "C");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "D");
        template.sendBody("direct:foo", "Hey World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:bar", "E");
        template.sendBody("direct:foo", "Hi Again World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testFromFilterBuilderWhenExchangeDone() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).filter().xpath("/person[@name='James']").whenDone(1).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "<person name='Claus'/>");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "<person name='Jonathan'/>");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "<person name='James'/>");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "<person name='Hadrian'/>");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeCompleted() throws Exception {
        NotifyBuilder notify = whenCompleted(5).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:bar", "C");
        try {
            template.sendBody("direct:fail", "D");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        try {
            template.sendBody("direct:fail", "E");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        // should NOT be completed as it only counts successful exchanges
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "F");
        template.sendBody("direct:foo", "G");
        template.sendBody("direct:bar", "H");
        // now it should match
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExchangeExactlyDone() throws Exception {
        NotifyBuilder notify = whenExactlyDone(5).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "F");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenExchangeExactlyComplete() throws Exception {
        NotifyBuilder notify = whenExactlyCompleted(5).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        template.sendBody("direct:bar", "D");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "F");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenExchangeExactlyFailed() throws Exception {
        NotifyBuilder notify = whenExactlyFailed(2).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "A");
        template.sendBody("direct:foo", "B");
        template.sendBody("direct:foo", "C");
        try {
            template.sendBody("direct:fail", "D");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        template.sendBody("direct:bar", "E");
        Assert.assertEquals(false, notify.matches());
        try {
            template.sendBody("direct:fail", "F");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:bar", "G");
        Assert.assertEquals(true, notify.matches());
        try {
            template.sendBody("direct:fail", "H");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // ignore
        }
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenAnyReceivedMatches() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenAnyReceivedMatches(TestSupport.body().contains("Camel")).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "Hello Camel");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenAllReceivedMatches() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenAllReceivedMatches(TestSupport.body().contains("Camel")).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello Camel");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Bye Camel");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenAnyDoneMatches() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenAnyDoneMatches(TestSupport.body().contains("Bye")).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "Camel");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Damn World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenAllDoneMatches() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenAllDoneMatches(TestSupport.body().contains("Bye")).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "Camel");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:cake", "World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenBodiesReceived() throws Exception {
        NotifyBuilder notify = whenBodiesReceived("Hi World", "Hello World").create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(true, notify.matches());
        // should keep being true
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Damn World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenBodiesDone() throws Exception {
        NotifyBuilder notify = whenBodiesDone("Bye World", "Bye Camel").create();
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "Camel");
        Assert.assertEquals(true, notify.matches());
        // should keep being true
        template.sendBody("direct:foo", "Damn World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenExactBodiesReceived() throws Exception {
        NotifyBuilder notify = whenExactBodiesReceived("Hi World", "Hello World").create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(true, notify.matches());
        // should not keep being true
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Damn World");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenExactBodiesDone() throws Exception {
        NotifyBuilder notify = whenExactBodiesDone("Bye World", "Bye Camel").create();
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "Camel");
        Assert.assertEquals(true, notify.matches());
        // should NOT keep being true
        template.sendBody("direct:foo", "Damn World");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenReceivedSatisfied() throws Exception {
        // lets use a mock to set the expressions as it got many great assertions for that
        // notice we use mock:assert which does NOT exist in the route, its just a pseudo name
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedBodiesReceivedInAnyOrder("Hello World", "Bye World", "Hi World");
        NotifyBuilder notify = from("direct:foo").whenDoneSatisfied(mock).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(false, notify.matches());
        // the notify  is based on direct:foo so sending to bar should not trigger match
        template.sendBody("direct:bar", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenReceivedNotSatisfied() throws Exception {
        // lets use a mock to set the expressions as it got many great assertions for that
        // notice we use mock:assert which does NOT exist in the route, its just a pseudo name
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedMessageCount(2);
        mock.message(1).body().contains("Camel");
        NotifyBuilder notify = from("direct:foo").whenReceivedNotSatisfied(mock).create();
        // is always false to start with
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Hello Camel");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testWhenNotSatisfiedUsingSatisfied() throws Exception {
        // lets use a mock to set the expressions as it got many great assertions for that
        // notice we use mock:assert which does NOT exist in the route, its just a pseudo name
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedMessageCount(2);
        mock.message(1).body().contains("Camel");
        NotifyBuilder notify = from("direct:foo").whenReceivedSatisfied(mock).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello Camel");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testComplexOrCamel() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedBodiesReceivedInAnyOrder("Hello World", "Bye World", "Hi World");
        NotifyBuilder notify = whenExactlyDone(5).whenAnyReceivedMatches(TestSupport.body().contains("Camel")).create();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(false, notify.matches());
        // the notify  is based on direct:foo so sending to bar should not trigger match
        template.sendBody("direct:bar", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hi World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "Hi Camel");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "A");
        template.sendBody("direct:bar", "B");
        template.sendBody("direct:bar", "C");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenDoneSatisfied() throws Exception {
        // lets use a mock to set the expressions as it got many great assertions for that
        // notice we use mock:assert which does NOT exist in the route, its just a pseudo name
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedBodiesReceived("Bye World", "Bye Camel");
        NotifyBuilder notify = new NotifyBuilder(context).whenDoneSatisfied(mock).create();
        // is always false to start with
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "World");
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "Camel");
        Assert.assertEquals(true, notify.matches());
        template.requestBody("direct:cake", "Damn");
        // will still be true as the mock has been completed
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenDoneNotSatisfied() throws Exception {
        // lets use a mock to set the expressions as it got many great assertions for that
        // notice we use mock:assert which does NOT exist in the route, its just a pseudo name
        MockEndpoint mock = getMockEndpoint("mock:assert");
        mock.expectedBodiesReceived("Bye World", "Bye Camel");
        NotifyBuilder notify = new NotifyBuilder(context).whenDoneNotSatisfied(mock).create();
        // is always false to start with
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "World");
        Assert.assertEquals(true, notify.matches());
        template.requestBody("direct:cake", "Camel");
        Assert.assertEquals(false, notify.matches());
        template.requestBody("direct:cake", "Damn");
        // will still be false as the mock has been completed
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testReset() throws Exception {
        NotifyBuilder notify = whenExactlyDone(1).create();
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
        // reset
        notify.reset();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(true, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(false, notify.matches());
    }

    @Test
    public void testResetBodiesReceived() throws Exception {
        NotifyBuilder notify = whenBodiesReceived("Hello World", "Bye World").create();
        template.sendBody("direct:foo", "Hello World");
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(true, notify.matches());
        // reset
        notify.reset();
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testOneNonAbstractPredicate() throws Exception {
        try {
            wereSentTo("mock:foo").create();
            Assert.fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("NotifyBuilder must contain at least one non-abstract predicate (such as whenDone)", e.getMessage());
        }
    }

    @Test
    public void testWereSentTo() throws Exception {
        NotifyBuilder notify = wereSentTo("mock:foo").whenDone(1).create();
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Bye World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testTwoWereSentTo() throws Exception {
        // sent to both endpoints
        NotifyBuilder notify = wereSentTo("mock:beer").whenDone(1).create();
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "Bye World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWhenDoneWereSentTo() throws Exception {
        // only match when two are done and were sent to mock:beer
        NotifyBuilder notify = wereSentTo("mock:beer").create();
        template.sendBody("direct:bar", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "B");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "C");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "D");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "F");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testWereSentToWhenDone() throws Exception {
        // like the other test, but ordering of wereSentTo does not matter
        NotifyBuilder notify = wereSentTo("mock:beer").whenDone(2).create();
        template.sendBody("direct:bar", "A");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "B");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "C");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "D");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:cake", "E");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "F");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testTwoWereSentToRegExp() throws Exception {
        // send to any endpoint with beer in the uri
        NotifyBuilder notify = wereSentTo(".*beer.*").whenDone(1).create();
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:beer", "Bye World");
        Assert.assertEquals(true, notify.matches());
    }

    @Test
    public void testTwoWereSentToDoneAndFailed() throws Exception {
        // we expect 2+ done messages which were sent to mock:bar
        // and 1+ failed message which were sent to mock:fail
        NotifyBuilder notify = wereSentTo("mock:fail").create();
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:bar", "Hello World");
        Assert.assertEquals(false, notify.matches());
        template.sendBody("direct:foo", "Hello World");
        Assert.assertEquals(false, notify.matches());
        try {
            template.sendBody("direct:fail", "Bye World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            // expected
        }
        Assert.assertEquals(true, notify.matches());
    }
}

