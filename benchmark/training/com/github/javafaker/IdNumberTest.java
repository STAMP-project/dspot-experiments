package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class IdNumberTest extends AbstractFakerTest {
    @Test
    public void testValid() {
        Assert.assertThat(faker.idNumber().valid(), MatchesRegularExpression.matchesRegularExpression("[0-8]\\d{2}-\\d{2}-\\d{4}"));
    }

    @Test
    public void testInvalid() {
        Assert.assertThat(faker.idNumber().invalid(), MatchesRegularExpression.matchesRegularExpression("[0-9]\\d{2}-\\d{2}-\\d{4}"));
    }

    @Test
    public void testSsnValid() {
        Assert.assertThat(faker.idNumber().valid(), MatchesRegularExpression.matchesRegularExpression("[0-8]\\d{2}-\\d{2}-\\d{4}"));
    }

    @Test
    public void testValidSwedishSsn() {
        final Faker f = new Faker(new Locale("sv_SE"));
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(f.idNumber().valid(), MatchesRegularExpression.matchesRegularExpression("\\d{6}[-+]\\d{4}"));
        }
    }

    @Test
    public void testInvalidSwedishSsn() {
        final Faker f = new Faker(new Locale("sv_SE"));
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(f.idNumber().invalid(), MatchesRegularExpression.matchesRegularExpression("\\d{6}[-+]\\d{4}"));
        }
    }
}

