package net.bytebuddy.utility;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RandomStringTest {
    @Test
    public void testRandomStringLength() throws Exception {
        MatcherAssert.assertThat(new RandomString().nextString().length(), CoreMatchers.is(RandomString.DEFAULT_LENGTH));
        MatcherAssert.assertThat(RandomString.make().length(), CoreMatchers.is(RandomString.DEFAULT_LENGTH));
        MatcherAssert.assertThat(new RandomString(((RandomString.DEFAULT_LENGTH) * 2)).nextString().length(), CoreMatchers.is(((RandomString.DEFAULT_LENGTH) * 2)));
        MatcherAssert.assertThat(RandomString.make(((RandomString.DEFAULT_LENGTH) * 2)).length(), CoreMatchers.is(((RandomString.DEFAULT_LENGTH) * 2)));
    }

    @Test
    public void testRandom() throws Exception {
        RandomString randomString = new RandomString();
        MatcherAssert.assertThat(randomString.nextString(), CoreMatchers.not(randomString.nextString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeLengthThrowsException() throws Exception {
        new RandomString((-1));
    }

    @Test
    public void testHashValueOnlyOneBits() throws Exception {
        MatcherAssert.assertThat(RandomString.hashOf((-1)).length(), CoreMatchers.not(0));
    }

    @Test
    public void testHashValueOnlyZeroBits() throws Exception {
        MatcherAssert.assertThat(RandomString.hashOf(0).length(), CoreMatchers.not(0));
    }

    @Test
    public void testHashValueInequality() throws Exception {
        MatcherAssert.assertThat(RandomString.hashOf(0), CoreMatchers.is(RandomString.hashOf(0)));
        MatcherAssert.assertThat(RandomString.hashOf(0), CoreMatchers.not(RandomString.hashOf((-1))));
        MatcherAssert.assertThat(RandomString.hashOf(0), CoreMatchers.not(RandomString.hashOf(1)));
    }
}

