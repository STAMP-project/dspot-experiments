/**
 * Copyright (c) 2010-2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.test.aggregate;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.test.AxonAssertionError;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 * @since 0.7
 */
public class FixtureTest_MatcherParams {
    private FixtureConfiguration<StandardAggregate> fixture;

    @Test
    public void testFirstFixture() {
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus())).given(new MyEvent("aggregateId", 1)).when(new TestCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesMatch()).expectEventsMatching(sequenceOf(matches(( i) -> true)));
    }

    @Test
    public void testPayloadsMatch() {
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus())).given(new MyEvent("aggregateId", 1)).when(new TestCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesMatch()).expectEventsMatching(payloadsMatching(sequenceOf(matches(( i) -> true))));
    }

    @Test
    public void testPayloadsMatchExact() {
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus())).given(new MyEvent("aggregateId", 1)).when(new TestCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesMatch()).expectEventsMatching(payloadsMatching(exactSequenceOf(matches(( i) -> true))));
    }

    @Test
    public void testPayloadsMatchPredicate() {
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus())).given(new MyEvent("aggregateId", 1)).when(new TestCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesMatch()).expectEventsMatching(payloadsMatching(predicate(( ml) -> !(ml.isEmpty()))));
    }

    @Test
    public void testFixture_UnexpectedException() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new StrangeCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesMatch());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(e.getMessage().contains("but got <exception of type [StrangeCommandReceivedException]>"));
        }
    }

    @Test
    public void testFixture_UnexpectedReturnValue() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new TestCommand("aggregateId")).expectException(new FixtureTest_MatcherParams.DoesMatch());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(e.getMessage().contains("The command handler returned normally, but an exception was expected"));
            Assert.assertTrue(e.getMessage().contains("<anything> but returned with <null>"));
        }
    }

    @Test
    public void testFixture_WrongReturnValue() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new TestCommand("aggregateId")).expectResultMessageMatching(new FixtureTest_MatcherParams.DoesNotMatch());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(e.getMessage().contains("<something you can never give me> but got <GenericCommandResultMessage{payload={null}"));
        }
    }

    @Test
    public void testFixture_WrongExceptionType() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new StrangeCommand("aggregateId")).expectException(new FixtureTest_MatcherParams.DoesNotMatch());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(e.getMessage().contains("<something you can never give me> but got <exception of type [StrangeCommandReceivedException]>"));
        }
    }

    @Test
    public void testFixture_ExpectedPublishedSameAsStored() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new StrangeCommand("aggregateId")).expectEvents(new FixtureTest_MatcherParams.DoesMatch<List<? extends org.axonframework.eventhandling.EventMessage>>());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(e.getMessage().contains("The published events do not match the expected events"));
            Assert.assertTrue(e.getMessage().contains("FixtureTest_MatcherParams$DoesMatch <|> "));
            Assert.assertTrue(e.getMessage().contains("probable cause"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFixture_DispatchMetaDataInCommand() throws Exception {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MessageHandler<CommandMessage<?>> mockCommandHandler = Mockito.mock(MessageHandler.class);
        fixture.registerCommandHandler(StrangeCommand.class, mockCommandHandler);
        fixture.given(givenEvents).when(new StrangeCommand("aggregateId"), Collections.singletonMap("meta", "value"));
        final ArgumentCaptor<CommandMessage> captor = ArgumentCaptor.forClass(CommandMessage.class);
        Mockito.verify(mockCommandHandler).handle(captor.capture());
        List<CommandMessage> dispatched = captor.getAllValues();
        Assert.assertEquals(1, dispatched.size());
        Assert.assertEquals(1, dispatched.get(0).getMetaData().size());
        Assert.assertEquals("value", dispatched.get(0).getMetaData().get("meta"));
    }

    @Test
    public void testFixture_EventDoesNotMatch() {
        List<?> givenEvents = Arrays.asList(new MyEvent("aggregateId", 1), new MyEvent("aggregateId", 2), new MyEvent("aggregateId", 3));
        MyCommandHandler commandHandler = new MyCommandHandler(fixture.getRepository(), fixture.getEventBus());
        try {
            fixture.registerAnnotatedCommandHandler(commandHandler).given(givenEvents).when(new TestCommand("aggregateId")).expectEventsMatching(new FixtureTest_MatcherParams.DoesNotMatch());
            Assert.fail("Expected an AxonAssertionError");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("something you can never give me"));
        }
    }

    private static class DoesMatch<T> extends BaseMatcher<T> {
        @Override
        public boolean matches(Object o) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("anything");
        }
    }

    private static class DoesNotMatch<T> extends BaseMatcher<T> {
        @Override
        public boolean matches(Object o) {
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("something you can never give me");
        }
    }
}

