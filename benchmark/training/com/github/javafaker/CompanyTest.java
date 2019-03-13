package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class CompanyTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.company().name(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z\\-&\', ]+"));
    }

    @Test
    public void testSuffix() {
        Assert.assertThat(faker.company().suffix(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void testIndustry() {
        Assert.assertThat(faker.company().industry(), MatchesRegularExpression.matchesRegularExpression("(\\w+([ ,&/-]{1,3})?){1,4}+"));
    }

    @Test
    public void testBuzzword() {
        Assert.assertThat(faker.company().buzzword(), MatchesRegularExpression.matchesRegularExpression("(\\w+[ /-]?){1,3}"));
    }

    @Test
    public void testCatchPhrase() {
        Assert.assertThat(faker.company().catchPhrase(), MatchesRegularExpression.matchesRegularExpression("(\\w+[ /-]?){1,9}"));
    }

    @Test
    public void testBs() {
        Assert.assertThat(faker.company().bs(), MatchesRegularExpression.matchesRegularExpression("(\\w+[ /-]?){1,9}"));
    }

    @Test
    public void testLogo() {
        Assert.assertThat(faker.company().logo(), MatchesRegularExpression.matchesRegularExpression("https://pigment.github.io/fake-logos/logos/medium/color/\\d+\\.png"));
    }

    @Test
    public void testProfession() {
        Assert.assertThat(faker.company().profession(), MatchesRegularExpression.matchesRegularExpression("[a-z ]+"));
    }

    @Test
    public void testUrl() {
        String regexp = "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])";
        Assert.assertThat(faker.company().url(), MatchesRegularExpression.matchesRegularExpression(regexp));
    }
}

