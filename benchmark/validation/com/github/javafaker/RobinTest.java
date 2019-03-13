package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class RobinTest extends AbstractFakerTest {
    @Test
    public void quote() {
        Assert.assertThat(faker.robin().quote(), MatchesRegularExpression.matchesRegularExpression("^(\\w+\\.?-?\'?\\s?)+(\\(?)?(\\w+\\s?\\.?)+(\\))?$"));
    }
}

