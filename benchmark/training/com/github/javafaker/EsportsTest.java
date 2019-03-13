package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class EsportsTest extends AbstractFakerTest {
    @Test
    public void player() {
        Assert.assertThat(faker.esports().player(), MatchesRegularExpression.matchesRegularExpression("(\\w|.)+"));
    }

    @Test
    public void team() {
        Assert.assertThat(faker.esports().team(), MatchesRegularExpression.matchesRegularExpression("((\\w|.)+ ?)+"));
    }

    @Test
    public void event() {
        Assert.assertThat(faker.esports().event(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?)+"));
    }

    @Test
    public void league() {
        Assert.assertThat(faker.esports().league(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void game() {
        Assert.assertThat(faker.esports().game(), MatchesRegularExpression.matchesRegularExpression("([\\w:.]+ ?)+"));
    }
}

