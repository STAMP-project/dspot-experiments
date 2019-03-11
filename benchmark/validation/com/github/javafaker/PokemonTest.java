package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class PokemonTest extends AbstractFakerTest {
    @Test
    public void name() {
        Assert.assertThat(faker.pokemon().name(), MatchesRegularExpression.matchesRegularExpression("[\\w\']+.?( \\w+)?"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.pokemon().location(), MatchesRegularExpression.matchesRegularExpression("\\w+( \\w+)?( \\w+)?"));
    }
}

