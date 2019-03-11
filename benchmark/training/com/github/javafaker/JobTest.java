package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class JobTest extends AbstractFakerTest {
    @Test
    public void field() {
        Assert.assertThat(faker.job().field(), MatchesRegularExpression.matchesRegularExpression("^[A-Z][A-Za-z-]+$"));
    }

    @Test
    public void seniority() {
        Assert.assertThat(faker.job().seniority(), MatchesRegularExpression.matchesRegularExpression("^[A-Z][a-z]+$"));
    }

    @Test
    public void position() {
        Assert.assertThat(faker.job().position(), MatchesRegularExpression.matchesRegularExpression("^[A-Z][a-z]+$"));
    }

    @Test
    public void keySkills() {
        Assert.assertThat(faker.job().keySkills(), MatchesRegularExpression.matchesRegularExpression("([A-Za-z-]+ ?){1,3}"));
    }

    @Test
    public void title() {
        Assert.assertThat(faker.job().title(), MatchesRegularExpression.matchesRegularExpression("([A-Za-z-]+ ?){2,3}"));
    }
}

