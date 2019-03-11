package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class SlackEmojiTest extends AbstractFakerTest {
    private static final String EMOTICON_REGEX = ":([\\w-]+):";

    @Test
    public void people() {
        Assert.assertThat(faker.slackEmoji().people(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void nature() {
        Assert.assertThat(faker.slackEmoji().nature(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void food_and_drink() {
        Assert.assertThat(faker.slackEmoji().foodAndDrink(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void celebration() {
        Assert.assertThat(faker.slackEmoji().celebration(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void activity() {
        Assert.assertThat(faker.slackEmoji().activity(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void travel_and_places() {
        Assert.assertThat(faker.slackEmoji().travelAndPlaces(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void objects_and_symbols() {
        Assert.assertThat(faker.slackEmoji().objectsAndSymbols(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void custom() {
        Assert.assertThat(faker.slackEmoji().custom(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }

    @Test
    public void emoji() {
        Assert.assertThat(faker.slackEmoji().emoji(), MatchesRegularExpression.matchesRegularExpression(SlackEmojiTest.EMOTICON_REGEX));
    }
}

