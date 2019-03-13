package fj.data;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 11/06/2015.
 */
public class LazyStringTest {
    @Test
    public void testToString() {
        Stream<LazyString> s = Stream.repeat(LazyString.str("abc"));
        // FJ 4.3 blows the stack when printing infinite streams of lazy strings.
        Assert.assertThat(s.toString(), CoreMatchers.is(CoreMatchers.equalTo("Cons(LazyString(a, ?), ?)")));
    }
}

