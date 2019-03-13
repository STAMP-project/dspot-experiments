package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LoremTest extends AbstractFakerTest {
    @Test
    public void shouldCreateFixedLengthString() {
        Assert.assertEquals(10, faker.lorem().fixedString(10).length());
        Assert.assertEquals(50, faker.lorem().fixedString(50).length());
        Assert.assertEquals(0, faker.lorem().fixedString(0).length());
        Assert.assertEquals(0, faker.lorem().fixedString((-1)).length());
    }

    @Test
    public void wordShouldNotBeNullOrEmpty() {
        Assert.assertThat(faker.lorem().word(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testCharacter() {
        Assert.assertThat(String.valueOf(faker.lorem().character()), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{1}"));
    }

    @Test
    public void testCharacterIncludeUpperCase() {
        Assert.assertThat(String.valueOf(faker.lorem().character(false)), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{1}"));
        Assert.assertThat(String.valueOf(faker.lorem().character(true)), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{1}"));
    }

    @Test
    public void testCharacters() {
        Assert.assertThat(faker.lorem().characters(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{255}"));
    }

    @Test
    public void testCharactersIncludeUpperCase() {
        Assert.assertThat(faker.lorem().characters(false), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{255}"));
        Assert.assertThat(faker.lorem().characters(true), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{255}"));
    }

    @Test
    public void testCharactersWithLength() {
        Assert.assertThat(faker.lorem().characters(2), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{2}"));
        Assert.assertThat(faker.lorem().characters(500), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{500}"));
        Assert.assertThat(faker.lorem().characters(0), Matchers.isEmptyString());
        Assert.assertThat(faker.lorem().characters((-1)), Matchers.isEmptyString());
    }

    @Test
    public void testCharactersWithLengthIncludeUppercase() {
        Assert.assertThat(faker.lorem().characters(2, false), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{2}"));
        Assert.assertThat(faker.lorem().characters(500, false), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{500}"));
        Assert.assertThat(faker.lorem().characters(2, true), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{2}"));
        Assert.assertThat(faker.lorem().characters(500, true), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{500}"));
        Assert.assertThat(faker.lorem().characters(0, false), Matchers.isEmptyString());
        Assert.assertThat(faker.lorem().characters((-1), true), Matchers.isEmptyString());
    }

    @Test
    public void testCharactersMinimumMaximumLength() {
        Assert.assertThat(faker.lorem().characters(1, 10), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{1,10}"));
    }

    @Test
    public void testCharactersMinimumMaximumLengthIncludeUppercase() {
        Assert.assertThat(faker.lorem().characters(1, 10), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{1,10}"));
    }

    @Test
    public void testSentence() {
        Assert.assertThat(faker.lorem().sentence(), MatchesRegularExpression.matchesRegularExpression("(\\w+\\s?){4,10}\\."));
    }

    @Test
    public void testSentenceWithWordCount() {
        Assert.assertThat(faker.lorem().sentence(10), MatchesRegularExpression.matchesRegularExpression("(\\w+\\s?){11,17}\\."));
    }

    @Test
    public void testSentenceWithWordCountAndRandomWordsToAdd() {
        Assert.assertThat(faker.lorem().sentence(10, 10), MatchesRegularExpression.matchesRegularExpression("(\\w+\\s?){10,20}\\."));
    }

    @Test
    public void testSentenceFixedNumberOfWords() {
        Assert.assertThat(faker.lorem().sentence(10, 0), MatchesRegularExpression.matchesRegularExpression("(\\w+\\s?){10}\\."));
    }
}

