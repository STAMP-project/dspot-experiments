package fj;


import Ord.charOrd;
import Ord.intOrd;
import Ord.longOrd;
import Ordering.EQ;
import Ordering.GT;
import Ordering.LT;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class OrdTest {
    @Test
    public void isGreaterThan() {
        F<Long, Boolean> pred = longOrd.isGreaterThan(1L);
        MatcherAssert.assertThat(pred.f(0L), CoreMatchers.is(false));
        MatcherAssert.assertThat(pred.f(1L), CoreMatchers.is(false));
        MatcherAssert.assertThat(pred.f(2L), CoreMatchers.is(true));
    }

    @Test
    public void isLessThan() {
        F<Long, Boolean> pred = longOrd.isLessThan(1L);
        MatcherAssert.assertThat(pred.f(0L), CoreMatchers.is(true));
        MatcherAssert.assertThat(pred.f(1L), CoreMatchers.is(false));
        MatcherAssert.assertThat(pred.f(2L), CoreMatchers.is(false));
    }

    @Test
    public void contramapShouldWork() {
        Ord<String> lengthOrd = Ord.contramap(String::length, intOrd);
        MatcherAssert.assertThat(lengthOrd.compare("str", "rts"), CoreMatchers.is(EQ));
        MatcherAssert.assertThat(lengthOrd.compare("strlong", "str"), CoreMatchers.is(GT));
    }

    @Test
    public void thenShouldWork() {
        Ord<String> lengthThenLastDigitOrd = Ord.on(String::length, intOrd).then(( s) -> s.charAt(((s.length()) - 1)), charOrd).ord();
        MatcherAssert.assertThat(lengthThenLastDigitOrd.compare("str", "dyr"), CoreMatchers.is(EQ));
        MatcherAssert.assertThat(lengthThenLastDigitOrd.compare("stt", "str"), CoreMatchers.is(GT));
        MatcherAssert.assertThat(lengthThenLastDigitOrd.compare("str", "strr"), CoreMatchers.is(LT));
    }
}

