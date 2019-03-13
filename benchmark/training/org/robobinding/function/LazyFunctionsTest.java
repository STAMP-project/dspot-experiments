package org.robobinding.function;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class LazyFunctionsTest {
    private LazyFunctions lazyFunctions;

    @Mock
    private FunctionSupply functionSupply;

    @Test
    public void whenFindExistingFunctionWithMatchedParameterTypes_thenFunctionFound() {
        Function function = lazyFunctions.find(LazyFunctionsTest.FunctionsBean.FUNCTION1, new Class<?>[]{ boolean.class });
        Assert.assertNotNull(function);
    }

    @Test
    public void whenFindExistingFunctioWithUnmatchedParameterTypes_thenReturnNull() {
        Function function = lazyFunctions.find(LazyFunctionsTest.FunctionsBean.FUNCTION1, new Class<?>[]{ Boolean.class, Object.class });
        Assert.assertNull(function);
    }

    @Test
    public void whenFindNonExistingFunction_thenReturnNull() {
        Function function = lazyFunctions.find("nonExistingFunction", new Class<?>[0]);
        Assert.assertNull(function);
    }

    @Test
    public void givenFindExistingFunctionOnce_whenFindAgain_thenReturnSameInstance() {
        Function function = lazyFunctions.find(LazyFunctionsTest.FunctionsBean.FUNCTION2, new Class<?>[0]);
        Function cachedFunction = lazyFunctions.find(LazyFunctionsTest.FunctionsBean.FUNCTION2, new Class<?>[0]);
        Assert.assertNotNull(function);
        Assert.assertTrue((function == cachedFunction));
    }

    public static class FunctionsBean {
        public static final String FUNCTION1 = "function1";

        public static final String FUNCTION2 = "function2";

        public void function1(boolean b) {
        }

        public void function2() {
        }
    }
}

