package io.crate.expression.operator;


import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class AndOperatorTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeBooleanTrueAndNonLiteral() throws Exception {
        assertNormalize("is_awesome and true", SymbolMatchers.isField("is_awesome"));
    }

    @Test
    public void testNormalizeBooleanFalseAndNonLiteral() throws Exception {
        assertNormalize("is_awesome and false", SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testNormalizeLiteralAndLiteral() throws Exception {
        assertNormalize("true and true", SymbolMatchers.isLiteral(true));
    }

    @Test
    public void testNormalizeLiteralAndLiteralFalse() throws Exception {
        assertNormalize("true and false", SymbolMatchers.isLiteral(false));
    }

    @Test
    public void testEvaluateAndOperator() {
        assertEvaluate("true and true", true);
        assertEvaluate("false and false", false);
        assertEvaluate("true and false", false);
        assertEvaluate("false and true", false);
        assertEvaluate("true and null", null);
        assertEvaluate("null and true", null);
        assertEvaluate("false and null", false);
        assertEvaluate("null and false", false);
        assertEvaluate("null and null", null);
    }
}

