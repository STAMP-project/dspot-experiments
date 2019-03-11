package org.axonframework.test.saga;


import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.utils.RecordingCommandBus;
import org.junit.Test;
import org.mockito.Mockito;


public class CommandValidatorTest {
    private CommandValidator testSubject;

    private RecordingCommandBus commandBus;

    @Test
    public void testAssertEmptyDispatchedEqualTo() {
        Mockito.when(commandBus.getDispatchedCommands()).thenReturn(emptyCommandMessageList());
        testSubject.assertDispatchedEqualTo();
    }

    @Test
    public void testAssertNonEmptyDispatchedEqualTo() {
        Mockito.when(commandBus.getDispatchedCommands()).thenReturn(listOfOneCommandMessage("command"));
        testSubject.assertDispatchedEqualTo("command");
    }

    @Test(expected = AxonAssertionError.class)
    public void testMatchWithUnexpectedNullValue() {
        Mockito.when(commandBus.getDispatchedCommands()).thenReturn(listOfOneCommandMessage(new CommandValidatorTest.SomeCommand(null)));
        testSubject.assertDispatchedEqualTo(new CommandValidatorTest.SomeCommand("test"));
    }

    private class SomeCommand {
        private final Object value;

        public SomeCommand(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}

