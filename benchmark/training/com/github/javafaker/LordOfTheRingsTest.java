package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luka Obradovic (luka@vast.com)
 */
public class LordOfTheRingsTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.lordOfTheRings().character(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w ]+ ?)+"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.lordOfTheRings().location(), MatchesRegularExpression.matchesRegularExpression("(?U)([\\w\'\\- ]+ ?)+"));
    }
}

