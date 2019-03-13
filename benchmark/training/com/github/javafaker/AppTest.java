package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class AppTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.app().name(), MatchesRegularExpression.matchesRegularExpression("([\\w-]+ ?)+"));
    }

    @Test
    public void testVersion() {
        Assert.assertThat(faker.app().version(), MatchesRegularExpression.matchesRegularExpression("\\d\\.(\\d){1,2}(\\.\\d)?"));
    }

    @Test
    public void testAuthor() {
        Assert.assertThat(faker.app().author(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+[-&,\\.]? ?){2,9}"));
    }
}

