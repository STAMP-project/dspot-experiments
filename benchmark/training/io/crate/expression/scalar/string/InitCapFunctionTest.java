package io.crate.expression.scalar.string;


import DataTypes.STRING;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.testing.SymbolMatchers;
import org.junit.Test;


public class InitCapFunctionTest extends AbstractScalarFunctionsTest {
    @Test
    public void testNormalizeCapInitFuncForAllLowerCase() {
        assertNormalize("initcap('hello world!')", SymbolMatchers.isLiteral("Hello World!"));
    }

    @Test
    public void testNormalizeCapInitFuncForAllUpperCase() {
        assertNormalize("initcap('HELLO WORLD!')", SymbolMatchers.isLiteral("Hello World!"));
    }

    @Test
    public void testNormalizeCapInitFuncForMixedCase() {
        assertNormalize("initcap('HellO 1WORLD !')", SymbolMatchers.isLiteral("Hello 1world !"));
    }

    @Test
    public void testNormalizeCapInitFuncForEmptyString() {
        assertNormalize("initcap('')", SymbolMatchers.isLiteral(""));
    }

    @Test
    public void testNormalizeCapInitFuncForNonEnglishLatinChars() {
        assertNormalize("initcap('??? ?? ?bc ?')", SymbolMatchers.isLiteral("??? ?? ?bc ?"));
    }

    @Test
    public void testNormalizeCapInitFuncForNonLatinChars() {
        assertNormalize("initcap('?? this is chinese!')", SymbolMatchers.isLiteral("?? This Is Chinese!"));
    }

    @Test
    public void testEvaluateWithNull() {
        assertEvaluate("initcap(name)", null, Literal.of(STRING, null));
    }
}

