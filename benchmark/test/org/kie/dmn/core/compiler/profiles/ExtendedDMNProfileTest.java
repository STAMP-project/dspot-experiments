package org.kie.dmn.core.compiler.profiles;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.extended.AbsFunction;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.runtime.functions.extended.EvenFunction;
import org.kie.dmn.feel.runtime.functions.extended.ExpFunction;
import org.kie.dmn.feel.runtime.functions.extended.LogFunction;
import org.kie.dmn.feel.runtime.functions.extended.MedianFunction;
import org.kie.dmn.feel.runtime.functions.extended.ModeFunction;
import org.kie.dmn.feel.runtime.functions.extended.ModuloFunction;
import org.kie.dmn.feel.runtime.functions.extended.OddFunction;
import org.kie.dmn.feel.runtime.functions.extended.ProductFunction;
import org.kie.dmn.feel.runtime.functions.extended.SplitFunction;
import org.kie.dmn.feel.runtime.functions.extended.SqrtFunction;
import org.kie.dmn.feel.runtime.functions.extended.StddevFunction;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;


public class ExtendedDMNProfileTest {
    private final DateFunction dateFunction = DateFunction.INSTANCE;

    private final TimeFunction timeFunction = TimeFunction.INSTANCE;

    private final SplitFunction splitFunction = SplitFunction.INSTANCE;

    private final ProductFunction productFunction = ProductFunction.INSTANCE;

    private final MedianFunction medianFunction = MedianFunction.INSTANCE;

    private final StddevFunction stddevFunction = StddevFunction.INSTANCE;

    private final ModeFunction modeFunction = ModeFunction.INSTANCE;

    private final AbsFunction absFunction = AbsFunction.INSTANCE;

    private final ModuloFunction moduloFunction = ModuloFunction.INSTANCE;

    private final SqrtFunction sqrtFunction = SqrtFunction.INSTANCE;

    private final LogFunction logFunction = LogFunction.INSTANCE;

    private final ExpFunction expFunction = ExpFunction.INSTANCE;

    private final EvenFunction evenFunction = EvenFunction.INSTANCE;

    private final OddFunction oddFunction = OddFunction.INSTANCE;

    @Test
    public void testDateFunction_invokeParamStringDateTime() {
        ExtendedDMNProfileTest.assertResult(dateFunction.invoke("2017-09-07T10:20:30"), LocalDate.of(2017, 9, 7));
    }

    @Test
    public void testDateFunction_invokeExtended() {
        ExtendedDMNProfileTest.assertResult(dateFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_DATE.parse("2016-12-20", LocalDate::from));
        ExtendedDMNProfileTest.assertResult(dateFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_DATE.parse("2016-12-20", LocalDate::from));
        ExtendedDMNProfileTest.assertResult(dateFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_DATE.parse("2016-12-20", LocalDate::from));
    }

    @Test
    public void testTimeFunction_invokeStringParamDate() {
        ExtendedDMNProfileTest.assertResult(timeFunction.invoke("2017-10-09"), LocalTime.of(0, 0, 0));
        ExtendedDMNProfileTest.assertResult(timeFunction.invoke("2017-10-09T10:15:06"), LocalTime.of(10, 15, 6));
    }

    @Test
    public void testTimeFunction_invokeExtended() {
        ExtendedDMNProfileTest.assertResult(timeFunction.invoke("2016-12-20T14:30:22"), DateTimeFormatter.ISO_TIME.parse("14:30:22", LocalTime::from));
        ExtendedDMNProfileTest.assertResult(timeFunction.invoke("2016-12-20T14:30:22-05:00"), DateTimeFormatter.ISO_TIME.parse("14:30:22-05:00", OffsetTime::from));
        ExtendedDMNProfileTest.assertResult(timeFunction.invoke("2016-12-20T14:30:22z"), DateTimeFormatter.ISO_TIME.parse("14:30:22z", OffsetTime::from));
    }

    @Test
    public void testSplitFunction() {
        ExtendedDMNProfileTest.assertResult(splitFunction.invoke("John Doe", "\\s"), Arrays.asList("John", "Doe"));
        ExtendedDMNProfileTest.assertResult(splitFunction.invoke("a;b;c;;", ";"), Arrays.asList("a", "b", "c", "", ""));
    }

    @Test
    public void testProductFunction() {
        ExtendedDMNProfileTest.assertResult(productFunction.invoke(Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4))), BigDecimal.valueOf(24));
    }

    @Test
    public void testMedianFunction() {
        ExtendedDMNProfileTest.assertResult(medianFunction.invoke(new Object[]{ BigDecimal.valueOf(8), BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(3), BigDecimal.valueOf(4) }), BigDecimal.valueOf(4));
        ExtendedDMNProfileTest.assertResult(medianFunction.invoke(Arrays.asList(BigDecimal.valueOf(6), BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))), BigDecimal.valueOf(2.5));
        ExtendedDMNProfileTest.assertNull(medianFunction.invoke(new Object[]{  }));
    }

    @Test
    public void testStddevFunction() {
        ExtendedDMNProfileTest.assertResultDoublePrecision(stddevFunction.invoke(new Object[]{ 2, 4, 7, 5 }), BigDecimal.valueOf(2.0816659994661326));
    }

    @Test
    public void testModeFunction() {
        ExtendedDMNProfileTest.assertResult(modeFunction.invoke(new Object[]{ 6, 3, 9, 6, 6 }), Collections.singletonList(BigDecimal.valueOf(6)));
        ExtendedDMNProfileTest.assertResult(modeFunction.invoke(Arrays.asList(6, 1, 9, 6, 1)), Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(6)));
        ExtendedDMNProfileTest.assertResult(modeFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void testAbsFunction() {
        ExtendedDMNProfileTest.assertResult(absFunction.invoke(BigDecimal.valueOf(10)), BigDecimal.valueOf(10));
        ExtendedDMNProfileTest.assertResult(absFunction.invoke(BigDecimal.valueOf((-10))), BigDecimal.valueOf(10));
    }

    @Test
    public void testModuloFunction() {
        ExtendedDMNProfileTest.assertResult(moduloFunction.invoke(BigDecimal.valueOf(12), BigDecimal.valueOf(5)), BigDecimal.valueOf(2));
    }

    @Test
    public void testSqrtFunction() {
        ExtendedDMNProfileTest.assertResultDoublePrecision(sqrtFunction.invoke(BigDecimal.valueOf(16)), BigDecimal.valueOf(4));
        ExtendedDMNProfileTest.assertResultDoublePrecision(sqrtFunction.invoke(BigDecimal.valueOf(2)), BigDecimal.valueOf(1.4142135623730951));
    }

    @Test
    public void testLogFunction() {
        ExtendedDMNProfileTest.assertResultDoublePrecision(logFunction.invoke(BigDecimal.valueOf(10)), BigDecimal.valueOf(2.302585092994046));
    }

    @Test
    public void testExpFunction() {
        ExtendedDMNProfileTest.assertResultDoublePrecision(expFunction.invoke(BigDecimal.valueOf(5)), BigDecimal.valueOf(148.4131591025766));
    }

    @Test
    public void testOddFunction() {
        ExtendedDMNProfileTest.assertResult(oddFunction.invoke(BigDecimal.valueOf(5)), Boolean.TRUE);
        ExtendedDMNProfileTest.assertResult(oddFunction.invoke(BigDecimal.valueOf(2)), Boolean.FALSE);
    }

    @Test
    public void testOddFunction_fractional() {
        ExtendedDMNProfileTest.assertNull(oddFunction.invoke(BigDecimal.valueOf(5.5)));
        ExtendedDMNProfileTest.assertResult(oddFunction.invoke(BigDecimal.valueOf(5.0)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction() {
        ExtendedDMNProfileTest.assertResult(evenFunction.invoke(BigDecimal.valueOf(5)), Boolean.FALSE);
        ExtendedDMNProfileTest.assertResult(evenFunction.invoke(BigDecimal.valueOf(2)), Boolean.TRUE);
    }

    @Test
    public void testEvenFunction_fractional() {
        ExtendedDMNProfileTest.assertNull(evenFunction.invoke(BigDecimal.valueOf(5.5)));
        ExtendedDMNProfileTest.assertResult(evenFunction.invoke(BigDecimal.valueOf(2.0)), Boolean.TRUE);
    }
}

