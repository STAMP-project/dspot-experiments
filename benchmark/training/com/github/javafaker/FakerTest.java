package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import java.util.Locale;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class FakerTest extends AbstractFakerTest {
    @Test
    public void bothifyShouldGenerateLettersAndNumbers() {
        Assert.assertThat(faker.bothify("????##@gmail.com"), MatchesRegularExpression.matchesRegularExpression("\\w{4}\\d{2}@gmail.com"));
    }

    @Test
    public void letterifyShouldGenerateLetters() {
        Assert.assertThat(faker.bothify("????"), MatchesRegularExpression.matchesRegularExpression("\\w{4}"));
    }

    @Test
    public void letterifyShouldGenerateUpperCaseLetters() {
        Assert.assertThat(faker.bothify("????", true), MatchesRegularExpression.matchesRegularExpression("[A-Z]{4}"));
    }

    @Test
    public void letterifyShouldLeaveNonSpecialCharactersAlone() {
        Assert.assertThat(faker.bothify("ABC????DEF"), MatchesRegularExpression.matchesRegularExpression("ABC\\w{4}DEF"));
    }

    @Test
    public void numerifyShouldGenerateNumbers() {
        Assert.assertThat(faker.numerify("####"), MatchesRegularExpression.matchesRegularExpression("\\d{4}"));
    }

    @Test
    public void numerifyShouldLeaveNonSpecialCharactersAlone() {
        Assert.assertThat(faker.numerify("####123"), MatchesRegularExpression.matchesRegularExpression("\\d{4}123"));
    }

    @Test
    public void regexifyShouldGenerateNumbers() {
        Assert.assertThat(faker.regexify("\\d"), MatchesRegularExpression.matchesRegularExpression("\\d"));
    }

    @Test
    public void regexifyShouldGenerateLetters() {
        Assert.assertThat(faker.regexify("\\w"), MatchesRegularExpression.matchesRegularExpression("\\w"));
    }

    @Test
    public void regexifyShouldGenerateAlternations() {
        Assert.assertThat(faker.regexify("(a|b)"), MatchesRegularExpression.matchesRegularExpression("(a|b)"));
    }

    @Test
    public void regexifyShouldGenerateBasicCharacterClasses() {
        Assert.assertThat(faker.regexify("(aeiou)"), MatchesRegularExpression.matchesRegularExpression("(aeiou)"));
    }

    @Test
    public void regexifyShouldGenerateCharacterClassRanges() {
        Assert.assertThat(faker.regexify("[a-z]"), MatchesRegularExpression.matchesRegularExpression("[a-z]"));
    }

    @Test
    public void regexifyShouldGenerateMultipleCharacterClassRanges() {
        Assert.assertThat(faker.regexify("[a-z1-9]"), MatchesRegularExpression.matchesRegularExpression("[a-z1-9]"));
    }

    @Test
    public void regexifyShouldGenerateSingleCharacterQuantifiers() {
        Assert.assertThat(faker.regexify("a*b+c?"), MatchesRegularExpression.matchesRegularExpression("a*b+c?"));
    }

    @Test
    public void regexifyShouldGenerateBracketsQuantifiers() {
        Assert.assertThat(faker.regexify("a{2}"), MatchesRegularExpression.matchesRegularExpression("a{2}"));
    }

    @Test
    public void regexifyShouldGenerateMinMaxQuantifiers() {
        Assert.assertThat(faker.regexify("a{2,3}"), MatchesRegularExpression.matchesRegularExpression("a{2,3}"));
    }

    @Test
    public void regexifyShouldGenerateBracketsQuantifiersOnBasicCharacterClasses() {
        Assert.assertThat(faker.regexify("[aeiou]{2,3}"), MatchesRegularExpression.matchesRegularExpression("[aeiou]{2,3}"));
    }

    @Test
    public void regexifyShouldGenerateBracketsQuantifiersOnCharacterClassRanges() {
        Assert.assertThat(faker.regexify("[a-z]{2,3}"), MatchesRegularExpression.matchesRegularExpression("[a-z]{2,3}"));
    }

    @Test
    public void regexifyShouldGenerateBracketsQuantifiersOnAlternations() {
        Assert.assertThat(faker.regexify("(a|b){2,3}"), MatchesRegularExpression.matchesRegularExpression("(a|b){2,3}"));
    }

    @Test
    public void regexifyShouldGenerateEscapedCharacters() {
        Assert.assertThat(faker.regexify("\\.\\*\\?\\+"), MatchesRegularExpression.matchesRegularExpression("\\.\\*\\?\\+"));
    }

    @Test(expected = RuntimeException.class)
    public void badExpressionTooManyArgs() {
        faker.expression("#{regexify 'a','a'}");
    }

    @Test(expected = RuntimeException.class)
    public void badExpressionTooFewArgs() {
        faker.expression("#{regexify}");
    }

    @Test(expected = RuntimeException.class)
    public void badExpressionCouldntCoerce() {
        faker.expression("#{number.number_between 'x','10'}");
    }

    @Test
    public void expression() {
        Assert.assertThat(faker.expression("#{regexify '(a|b){2,3}'}"), MatchesRegularExpression.matchesRegularExpression("(a|b){2,3}"));
        Assert.assertThat(faker.expression("#{regexify \'\\.\\*\\?\\+\'}"), MatchesRegularExpression.matchesRegularExpression("\\.\\*\\?\\+"));
        Assert.assertThat(faker.expression("#{bothify '????','true'}"), MatchesRegularExpression.matchesRegularExpression("[A-Z]{4}"));
        Assert.assertThat(faker.expression("#{bothify '????','false'}"), MatchesRegularExpression.matchesRegularExpression("[a-z]{4}"));
        Assert.assertThat(faker.expression("#{letterify '????','true'}"), MatchesRegularExpression.matchesRegularExpression("[A-Z]{4}"));
        Assert.assertThat(faker.expression("#{Name.first_name} #{Name.first_name} #{Name.last_name}"), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z']+ [a-zA-Z']+ [a-zA-Z']+"));
        Assert.assertThat(faker.expression("#{number.number_between '1','10'}"), MatchesRegularExpression.matchesRegularExpression("[1-9]"));
        Assert.assertThat(faker.expression("#{color.name}"), MatchesRegularExpression.matchesRegularExpression("[a-z\\s]+"));
    }

    @Test
    public void regexifyShouldGenerateSameValueForFakerWithSameSeed() {
        long seed = 1L;
        String regex = "\\d";
        String firstResult = new Faker(new Random(seed)).regexify(regex);
        String secondResult = new Faker(new Random(seed)).regexify(regex);
        Assert.assertThat(secondResult, is(firstResult));
    }

    @Test
    public void resolveShouldReturnValueThatExists() {
        Assert.assertThat(faker.resolve("address.city_prefix"), not(isEmptyString()));
    }

    @Test(expected = RuntimeException.class)
    public void resolveShouldThrowExceptionWhenPropertyDoesntExist() {
        final String resolve = faker.resolve("address.nothing");
        Assert.assertThat(resolve, is(nullValue()));
    }

    @Test
    public void fakerInstanceCanBeAcquiredViaUtilityMethods() {
        Assert.assertThat(Faker.instance(), is(instanceOf(Faker.class)));
        Assert.assertThat(Faker.instance(Locale.CANADA), is(instanceOf(Faker.class)));
        Assert.assertThat(Faker.instance(new Random(1)), is(instanceOf(Faker.class)));
        Assert.assertThat(Faker.instance(Locale.CHINA, new Random(2)), is(instanceOf(Faker.class)));
    }
}

