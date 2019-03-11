package com.insightfullogic.java8.examples.chapter2;


import java.util.function.BinaryOperator;
import org.junit.Test;


/**
 *
 *
 * @author richard
 */
public class LambdaExpressions {
    @Test
    public void mostSpecific() {
        // BEGIN most_specific_overload_call
        overloadedMethod("abc");
        // END most_specific_overload_call
    }

    // END most_specific_overload
    @Test
    public void mostSpecificBiFunction() {
        overloadedMethod(( x, y) -> x + y);
    }

    // BEGIN most_specific_bifunction
    private interface IntegerBiFunction extends BinaryOperator<Integer> {}
}

