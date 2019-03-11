package fj;


import Equal.charEqual;
import Equal.intEqual;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class EqualTest {
    @Test
    public void contramapShouldWork() {
        Equal<String> equalByLength = Equal.contramap(String::length, intEqual);
        MatcherAssert.assertThat(equalByLength.eq("str1", "str2"), Is.is(true));
        MatcherAssert.assertThat(equalByLength.eq("str1", "str11"), Is.is(false));
    }

    @Test
    public void thenShouldWork() {
        Equal<String> equalByLengthThenLastDigit = Equal.on(String::length, intEqual).then(( s) -> s.charAt(((s.length()) - 1)), charEqual).equal();
        MatcherAssert.assertThat(equalByLengthThenLastDigit.eq("str1", "spr1"), Is.is(true));
        MatcherAssert.assertThat(equalByLengthThenLastDigit.eq("str1", "str2"), Is.is(false));
        MatcherAssert.assertThat(equalByLengthThenLastDigit.eq("str1", "strr1"), Is.is(false));
    }
}

