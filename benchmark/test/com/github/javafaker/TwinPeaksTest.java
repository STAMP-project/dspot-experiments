package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class TwinPeaksTest extends AbstractFakerTest {
    @Test
    public void character() {
        Assert.assertThat(faker.twinPeaks().character(), MatchesRegularExpression.matchesRegularExpression("^([\\w\']+ ?){2,}$"));
    }

    @Test
    public void location() {
        Assert.assertThat(faker.twinPeaks().location(), MatchesRegularExpression.matchesRegularExpression("^[A-Za-z0-9\'&,\\- ]+$"));
    }

    @Test
    public void quote() {
        Assert.assertThat(faker.twinPeaks().quote(), IsNot.not(Matchers.isEmptyOrNullString()));
    }
}

