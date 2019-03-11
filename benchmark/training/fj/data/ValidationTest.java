package fj.data;


import fj.P2;
import fj.Semigroup;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ValidationTest {
    @Test
    public void testParseShort() {
        final List<Validation.Validation<NumberFormatException, Short>> l = List.list(parseShort("10"), parseShort("x"), parseShort("20"));
        Assert.assertThat(successes(l).foldLeft1(( s, a) -> ((short) (s + a))), Is.is(((short) (30))));
    }

    @Test
    public void testParseLong() {
        final List<Validation.Validation<NumberFormatException, Long>> l = List.list(parseLong("10"), parseLong("x"), parseLong("20"));
        P2<List<NumberFormatException>, List<Long>> p2 = partition(l);
        Assert.assertThat(p2._1().length(), Is.is(1));
        Assert.assertThat(p2._2().length(), Is.is(2));
    }

    @Test
    public void testParseInt() {
        final List<Validation.Validation<NumberFormatException, Integer>> l = List.list(parseInt("10"), parseInt("x"), parseInt("20"));
        Assert.assertThat(l.map(( v) -> v.validation(( e) -> 0, ( i) -> 1)).foldLeft1(( s, a) -> s + a), Is.is(2));
    }

    @Test
    public void testParseFloat() {
        final List<Validation.Validation<NumberFormatException, Float>> l = List.list(parseFloat("2.0"), parseFloat("x"), parseFloat("3.0"));
        Assert.assertThat(l.map(( v) -> v.validation(( e) -> 0, ( i) -> 1)).foldLeft1(( s, a) -> s + a), Is.is(2));
    }

    @Test
    public void testParseByte() {
        final List<Validation.Validation<NumberFormatException, Byte>> l = List.list(parseByte("10"), parseByte("x"), parseByte("-10"));
        Assert.assertThat(l.map(( v) -> v.validation(( e) -> 0, ( i) -> 1)).foldLeft1(( s, a) -> s + a), Is.is(2));
    }

    @Test
    public void testAccumulate1() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("10.0").accumulate(( f1) -> f1);
        Assert.assertThat(v.success(), Is.is(10.0));
    }

    @Test
    public void testAccumulate1Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(( f1) -> f1);
        Assert.assertThat(v.fail().length(), Is.is(1));
    }

    @Test
    public void testAccumulate2() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), ( f1, f2) -> f1 + f2);
        Assert.assertThat(v.success(), Is.is(3.0));
    }

    @Test
    public void testAccumulate2Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("y"), ( f1, f2) -> f1 + f2);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate3() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), ( f1, f2, f3) -> (f1 + f2) + f3);
        Assert.assertThat(v.success(), Is.is(6.0));
    }

    @Test
    public void testAccumulate3Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("y"), ( f1, f2, f3) -> (f1 + f2) + f3);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate4() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), ( f1, f2, f3, f4) -> ((f1 + f2) + f3) + f4);
        Assert.assertThat(v.success(), Is.is(10.0));
    }

    @Test
    public void testAccumulate4Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("y"), ( f1, f2, f3, f4) -> ((f1 + f2) + f3) + f4);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate5() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), ( f1, f2, f3, f4, f5) -> (((f1 + f2) + f3) + f4) + f5);
        Assert.assertThat(v.success(), Is.is(15.0));
    }

    @Test
    public void testAccumulate5Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("y"), ( f1, f2, f3, f4, f5) -> (((f1 + f2) + f3) + f4) + f5);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate6() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("6.0"), ( f1, f2, f3, f4, f5, f6) -> ((((f1 + f2) + f3) + f4) + f5) + f6);
        Assert.assertThat(v.success(), Is.is(21.0));
    }

    @Test
    public void testAccumulate6Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("y"), ( f1, f2, f3, f4, f5, f6) -> ((((f1 + f2) + f3) + f4) + f5) + f6);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate7() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("6.0"), parseDouble("7.0"), ( f1, f2, f3, f4, f5, f6, f7) -> (((((f1 + f2) + f3) + f4) + f5) + f6) + f7);
        Assert.assertThat(v.success(), Is.is(28.0));
    }

    @Test
    public void testAccumulate7Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("6.0"), parseDouble("y"), ( f1, f2, f3, f4, f5, f6, f7) -> (((((f1 + f2) + f3) + f4) + f5) + f6) + f7);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate8() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("1.0").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("6.0"), parseDouble("7.0"), parseDouble("8.0"), ( f1, f2, f3, f4, f5, f6, f7, f8) -> ((((((f1 + f2) + f3) + f4) + f5) + f6) + f7) + f8);
        Assert.assertThat(v.success(), Is.is(36.0));
    }

    @Test
    public void testAccumulate8Fail() {
        final Validation.Validation<List<NumberFormatException>, Double> v = parseDouble("x").accumulate(parseDouble("2.0"), parseDouble("3.0"), parseDouble("4.0"), parseDouble("5.0"), parseDouble("6.0"), parseDouble("7.0"), parseDouble("y"), ( f1, f2, f3, f4, f5, f6, f7, f8) -> ((((((f1 + f2) + f3) + f4) + f5) + f6) + f7) + f8);
        Assert.assertThat(v.fail().length(), Is.is(2));
    }

    @Test
    public void testAccumulate8s() {
        final Validation.Validation<NumberFormatException, Integer> v1 = parseInt("1");
        final Validation.Validation<NumberFormatException, Integer> v2 = parseInt("2");
        final Validation.Validation<NumberFormatException, Integer> v3 = parseInt("3");
        final Validation.Validation<NumberFormatException, Integer> v4 = parseInt("4");
        final Validation.Validation<NumberFormatException, Integer> v5 = parseInt("5");
        final Validation.Validation<NumberFormatException, Integer> v6 = parseInt("6");
        final Validation.Validation<NumberFormatException, Integer> v7 = parseInt("7");
        final Validation.Validation<NumberFormatException, Integer> v8 = parseInt("8");
        final Option<NumberFormatException> on2 = v1.accumulate(Semigroup.firstSemigroup(), v2);
        Assert.assertThat(on2, Is.is(Option.none()));
        final Option<NumberFormatException> on3 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3);
        Assert.assertThat(on3, Is.is(Option.none()));
        final Option<NumberFormatException> on4 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3, v4);
        Assert.assertThat(on4, Is.is(Option.none()));
        final Option<NumberFormatException> on5 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3, v4, v5);
        Assert.assertThat(on5, Is.is(Option.none()));
        final Option<NumberFormatException> on6 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3, v4, v5, v6);
        Assert.assertThat(on6, Is.is(Option.none()));
        final Option<NumberFormatException> on7 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3, v4, v5, v6, v7);
        Assert.assertThat(on7, Is.is(Option.none()));
        final Option<NumberFormatException> on8 = v1.accumulate(Semigroup.firstSemigroup(), v2, v3, v4, v5, v6, v7, v8);
        Assert.assertThat(on8, Is.is(Option.none()));
    }

    @Test
    public void testAccumulate8sFail() {
        final Option<NumberFormatException> on = parseInt("x").accumulate(Semigroup.firstSemigroup(), parseInt("2"), parseInt("3"), parseInt("4"), parseInt("5"), parseInt("6"), parseInt("7"), parseInt("y"));
        Assert.assertThat(on.some().getMessage(), Is.is("For input string: \"x\""));
    }

    @Test(expected = Error.class)
    public void testSuccess() {
        parseShort("x").success();
    }

    @Test(expected = Error.class)
    public void testFail() {
        parseShort("12").fail();
    }

    @Test
    public void testCondition() {
        final Validation.Validation<String, String> one = condition(true, "not 1", "one");
        Assert.assertThat(one.success(), Is.is("one"));
        final Validation.Validation<String, String> fail = condition(false, "not 1", "one");
        Assert.assertThat(fail.fail(), Is.is("not 1"));
    }

    @Test
    public void testNel() {
        Assert.assertThat(Validation.Validation.success("success").nel().success(), Is.is("success"));
        Assert.assertThat(Validation.Validation.fail("fail").nel().fail().head(), Is.is("fail"));
    }

    @Test
    public void testFailNEL() {
        Validation.Validation<NonEmptyList<Exception>, Integer> v = failNEL(new Exception("failed"));
        Assert.assertThat(v.isFail(), Is.is(true));
    }

    @Test
    public void testEither() {
        Assert.assertThat(either().f(Validation.Validation.success("success")).right().value(), Is.is("success"));
        Assert.assertThat(either().f(Validation.Validation.fail("fail")).left().value(), Is.is("fail"));
    }

    @Test
    public void testValidation() {
        Assert.assertThat(validation().f(Either.right("success")).success(), Is.is("success"));
        Assert.assertThat(validation().f(Either.left("fail")).fail(), Is.is("fail"));
    }
}

