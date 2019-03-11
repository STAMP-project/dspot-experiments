package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class BookTest extends AbstractFakerTest {
    @Test
    public void testTitle() {
        Assert.assertThat(faker.book().title(), MatchesRegularExpression.matchesRegularExpression("([\\p{L}\'\\-\\?]+[!,]? ?){2,9}"));
    }

    @Test
    public void testAuthor() {
        Assert.assertThat(faker.book().author(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.? ?){2,3}"));
    }

    @Test
    public void testPublisher() {
        Assert.assertThat(faker.book().publisher(), MatchesRegularExpression.matchesRegularExpression("([\\p{L}\'&\\-]+[,.]? ?){1,5}"));
    }

    @Test
    public void testGenre() {
        Assert.assertThat(faker.book().genre(), MatchesRegularExpression.matchesRegularExpression("([\\w/]+ ?){2,4}"));
    }
}

