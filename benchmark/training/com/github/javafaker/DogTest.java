package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class DogTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.dog().name(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void breed() {
        Assert.assertThat(faker.dog().breed(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void sound() {
        Assert.assertThat(faker.dog().sound(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void memePhrase() {
        Assert.assertThat(faker.dog().memePhrase(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z0-9\'\\/ ]+"));
    }

    @Test
    public void age() {
        Assert.assertThat(faker.dog().age(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void gender() {
        Assert.assertThat(faker.dog().gender(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void coatLength() {
        Assert.assertThat(faker.dog().coatLength(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void size() {
        Assert.assertThat(faker.dog().size(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }
}

