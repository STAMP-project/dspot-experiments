/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.commandhandling;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class AnnotationCommandHandlerAdapterTest {
    private AnnotationCommandHandlerAdapter testSubject;

    private CommandBus mockBus;

    private AnnotationCommandHandlerAdapterTest.MyCommandHandler mockTarget;

    private UnitOfWork<CommandMessage<?>> mockUnitOfWork;

    private ParameterResolverFactory parameterResolverFactory;

    @Test
    public void testHandlerDispatching_VoidReturnType() throws Exception {
        Object actualReturnValue = testSubject.handle(GenericCommandMessage.asCommandMessage(""));
        Assert.assertEquals(null, actualReturnValue);
        Assert.assertEquals(1, mockTarget.voidHandlerInvoked);
        Assert.assertEquals(0, mockTarget.returningHandlerInvoked);
    }

    @Test
    public void testHandlerDispatching_WithReturnType() throws Exception {
        Object actualReturnValue = testSubject.handle(GenericCommandMessage.asCommandMessage(1L));
        Assert.assertEquals(1L, actualReturnValue);
        Assert.assertEquals(0, mockTarget.voidHandlerInvoked);
        Assert.assertEquals(1, mockTarget.returningHandlerInvoked);
    }

    @Test
    public void testHandlerDispatching_WithCustomCommandName() throws Exception {
        Object actualReturnValue = testSubject.handle(new GenericCommandMessage(new org.axonframework.messaging.GenericMessage(1L), "almostLong"));
        Assert.assertEquals(1L, actualReturnValue);
        Assert.assertEquals(0, mockTarget.voidHandlerInvoked);
        Assert.assertEquals(0, mockTarget.returningHandlerInvoked);
        Assert.assertEquals(1, mockTarget.almostDuplicateReturningHandlerInvoked);
    }

    @Test
    public void testHandlerDispatching_ThrowingException() {
        try {
            testSubject.handle(GenericCommandMessage.asCommandMessage(new HashSet()));
            Assert.fail("Expected exception");
        } catch (Exception ex) {
            Assert.assertEquals(Exception.class, ex.getClass());
            return;
        }
        Assert.fail("Shouldn't make it till here");
    }

    @Test
    public void testSubscribe() {
        testSubject.subscribe(mockBus);
        Mockito.verify(mockBus).subscribe(Long.class.getName(), testSubject);
        Mockito.verify(mockBus).subscribe(String.class.getName(), testSubject);
        Mockito.verify(mockBus).subscribe(HashSet.class.getName(), testSubject);
        Mockito.verify(mockBus).subscribe(ArrayList.class.getName(), testSubject);
        Mockito.verify(mockBus).subscribe("almostLong", testSubject);
        Mockito.verifyNoMoreInteractions(mockBus);
    }

    @Test(expected = NoHandlerForCommandException.class)
    public void testHandle_NoHandlerForCommand() throws Exception {
        testSubject.handle(GenericCommandMessage.asCommandMessage(new LinkedList()));
        Mockito.verify(mockUnitOfWork.resources(), Mockito.never()).put(ParameterResolverFactory.class.getName(), parameterResolverFactory);
    }

    private static class MyCommandHandler {
        private int voidHandlerInvoked;

        private int returningHandlerInvoked;

        private int almostDuplicateReturningHandlerInvoked;

        @SuppressWarnings({ "UnusedDeclaration" })
        @CommandHandler
        public void myVoidHandler(String stringCommand, UnitOfWork<CommandMessage<?>> unitOfWork) {
            (voidHandlerInvoked)++;
        }

        @CommandHandler(commandName = "almostLong")
        public Long myAlmostDuplicateReturningHandler(Long longCommand, UnitOfWork<CommandMessage<?>> unitOfWork) {
            Assert.assertNotNull("The UnitOfWork was not passed to the command handler", unitOfWork);
            (almostDuplicateReturningHandlerInvoked)++;
            return longCommand;
        }

        @CommandHandler
        public Long myReturningHandler(Long longCommand, UnitOfWork<CommandMessage<?>> unitOfWork) {
            Assert.assertNotNull("The UnitOfWork was not passed to the command handler", unitOfWork);
            (returningHandlerInvoked)++;
            return longCommand;
        }

        @SuppressWarnings({ "UnusedDeclaration" })
        @CommandHandler
        public void exceptionThrowingHandler(HashSet o) throws Exception {
            throw new Exception("Some exception");
        }

        @SuppressWarnings({ "UnusedDeclaration" })
        @CommandHandler
        public void exceptionThrowingHandler(ArrayList o) {
            throw new RuntimeException("Some exception");
        }
    }
}

