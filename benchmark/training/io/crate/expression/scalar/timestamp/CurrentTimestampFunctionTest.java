package io.crate.expression.scalar.timestamp;


import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CurrentTimestampFunctionTest extends AbstractScalarFunctionsTest {
    private static final long EXPECTED_TIMESTAMP = 1422294644581L;

    @Test
    public void timestampIsCreatedCorrectly() {
        assertEvaluate("current_timestamp", CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP);
    }

    @Test
    public void precisionOfZeroDropsAllFractionsOfSeconds() {
        assertEvaluate("current_timestamp(0)", ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) - ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) % 1000)));
    }

    @Test
    public void precisionOfOneDropsLastTwoDigitsOfFractionsOfSecond() {
        assertEvaluate("current_timestamp(1)", ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) - ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) % 100)));
    }

    @Test
    public void precisionOfTwoDropsLastDigitOfFractionsOfSecond() {
        assertEvaluate("current_timestamp(2)", ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) - ((CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP) % 10)));
    }

    @Test
    public void precisionOfThreeKeepsAllFractionsOfSeconds() {
        assertEvaluate("current_timestamp(3)", CurrentTimestampFunctionTest.EXPECTED_TIMESTAMP);
    }

    @Test
    public void precisionLargerThan3RaisesException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Precision must be between 0 and 3");
        assertEvaluate("current_timestamp(4)", null);
    }

    @Test
    public void integerIsNormalizedToLiteral() {
        assertNormalize("current_timestamp(1)", Matchers.instanceOf(Literal.class));
    }
}

