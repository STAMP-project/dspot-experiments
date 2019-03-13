package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class RickAndMortyTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.rickAndMorty().character(), MatchesRegularExpression.matchesRegularExpression("^([\\w\'-.]+ ?){2,}$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.rickAndMorty().location(), MatchesRegularExpression.matchesRegularExpression("^([\\w-.]+ ?){2,}$"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.rickAndMorty().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

