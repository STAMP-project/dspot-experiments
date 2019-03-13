package com.baeldung.java8;


import com.baeldung.Foo;
import com.baeldung.FooExtended;
import com.baeldung.UseFoo;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class Java8FunctionalInteracesLambdasUnitTest {
    private UseFoo useFoo;

    @Test
    public void functionalInterfaceInstantiation_whenReturnDefiniteString_thenCorrect() {
        final Foo foo = ( parameter) -> parameter + "from lambda";
        final String result = useFoo.add("Message ", foo);
        Assert.assertEquals("Message from lambda", result);
    }

    @Test
    public void standardFIParameter_whenReturnDefiniteString_thenCorrect() {
        final Function<String, String> fn = ( parameter) -> parameter + "from lambda";
        final String result = useFoo.addWithStandardFI("Message ", fn);
        Assert.assertEquals("Message from lambda", result);
    }

    @Test
    public void defaultMethodFromExtendedInterface_whenReturnDefiniteString_thenCorrect() {
        final FooExtended fooExtended = ( string) -> string;
        final String result = fooExtended.defaultMethod();
        Assert.assertEquals("String from Bar", result);
    }

    @Test
    public void lambdaAndInnerClassInstantiation_whenReturnSameString_thenCorrect() {
        final Foo foo = ( parameter) -> parameter + "from Foo";
        final Foo fooByIC = new Foo() {
            @Override
            public String method(final String string) {
                return string + "from Foo";
            }
        };
        Assert.assertEquals(foo.method("Something "), fooByIC.method("Something "));
    }

    @Test
    public void accessVariablesFromDifferentScopes_whenReturnPredefinedString_thenCorrect() {
        Assert.assertEquals("Results: resultIC = Inner class value, resultLambda = Enclosing scope value", useFoo.scopeExperiment());
    }

    @Test
    public void shorteningLambdas_whenReturnEqualsResults_thenCorrect() {
        final Foo foo = ( parameter) -> buildString(parameter);
        final Foo fooHuge = ( parameter) -> {
            final String result = "Something " + parameter;
            // many lines of code
            return result;
        };
        Assert.assertEquals(foo.method("Something"), fooHuge.method("Something"));
    }

    @Test
    public void mutatingOfEffectivelyFinalVariable_whenNotEquals_thenCorrect() {
        final int[] total = new int[1];
        final Runnable r = () -> (total[0])++;
        r.run();
        Assert.assertNotEquals(0, total[0]);
    }
}

