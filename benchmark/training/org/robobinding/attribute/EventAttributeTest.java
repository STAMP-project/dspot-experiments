package org.robobinding.attribute;


import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robobinding.BindingContext;
import org.robobinding.function.Function;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(Theories.class)
public class EventAttributeTest {
    @DataPoints
    public static String[] illegalAttributeValues = new String[]{ "{invalid_command_name}", "{invalid_command_name", "invalid_command_name}" };

    private static final String COMMAND_NAME = "commandName";

    @Mock
    BindingContext bindingContext;

    @Mock
    Function function;

    private EventAttribute attribute = Attributes.anEventAttribute(EventAttributeTest.COMMAND_NAME);

    @Test
    public void givenFunctionWithParameters_whenFind_thenReturnCommandWithParametersSupported() {
        Mockito.when(bindingContext.findFunction(EventAttributeTest.COMMAND_NAME, withParameterTypes())).thenReturn(function);
        CommandImpl command = ((CommandImpl) (attribute.findCommand(bindingContext, withParameterTypes())));
        Assert.assertNotNull(command);
        Assert.assertTrue(command.supportsPreferredParameterType);
    }

    @Test
    public void givenFunctionWithoutParameters_whenFind_thenReturnCommandWithoutParametersSupported() {
        Mockito.when(bindingContext.findFunction(EventAttributeTest.COMMAND_NAME)).thenReturn(function);
        CommandImpl command = ((CommandImpl) (attribute.findCommand(bindingContext)));
        Assert.assertNotNull(command);
        Assert.assertFalse(command.supportsPreferredParameterType);
    }

    @Test
    public void whenFindANonExistingCommand_thenReturnNull() {
        Command command = attribute.findCommand(bindingContext);
        Assert.assertNull(command);
    }
}

