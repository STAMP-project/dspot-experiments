package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class CryptoTest extends AbstractFakerTest {
    @Test
    public void testMd5() {
        Assert.assertThat(faker.crypto().md5(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]+"));
    }

    @Test
    public void testSha1() {
        Assert.assertThat(faker.crypto().sha1(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]+"));
    }

    @Test
    public void testSha256() {
        Assert.assertThat(faker.crypto().sha256(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]+"));
    }

    @Test
    public void testSha512() {
        Assert.assertThat(faker.crypto().sha512(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]+"));
    }
}

