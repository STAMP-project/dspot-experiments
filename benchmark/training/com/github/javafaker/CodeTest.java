package com.github.javafaker;


import EAN13CheckDigit.EAN13_CHECK_DIGIT;
import LuhnCheckDigit.LUHN_CHECK_DIGIT;
import com.github.javafaker.matchers.MatchesRegularExpression;
import com.github.javafaker.repeating.Repeat;
import java.util.Locale;
import org.apache.commons.validator.routines.ISBNValidator;
import org.junit.Assert;
import org.junit.Test;


public class CodeTest extends AbstractFakerTest {
    private static final ISBNValidator ISBN_VALIDATOR = ISBNValidator.getInstance(false);

    @Test
    @Repeat(times = 1000)
    public void isbn10DefaultIsNoSeparator() {
        String isbn10 = faker.code().isbn10();
        assertIsValidISBN10(isbn10);
        Assert.assertThat(isbn10, not(containsString("-")));
    }

    @Test
    @Repeat(times = 1000)
    public void isbn13DefaultIsNoSeparator() {
        String isbn13 = faker.code().isbn13();
        assertIsValidISBN13(isbn13);
        Assert.assertThat(isbn13, not(containsString("-")));
    }

    @Test
    @Repeat(times = 1000)
    public void testIsbn10() {
        final String isbn10NoSep = faker.code().isbn10(false);
        final String isbn10Sep = faker.code().isbn10(true);
        Assert.assertThat((isbn10NoSep + " is not null"), isbn10NoSep, is(not(nullValue())));
        Assert.assertThat((isbn10NoSep + " has length of 10"), isbn10NoSep.length(), is(10));
        assertIsValidISBN10(isbn10NoSep);
        Assert.assertThat((isbn10Sep + " is not null"), isbn10Sep, is(not(nullValue())));
        Assert.assertThat((isbn10Sep + " has length of 13"), isbn10Sep.length(), is(13));
        assertIsValidISBN10(isbn10Sep);
    }

    @Test
    @Repeat(times = 1000)
    public void testIsbn13() {
        final String isbn13NoSep = faker.code().isbn13(false);
        final String isbn13Sep = faker.code().isbn13(true);
        Assert.assertThat((isbn13NoSep + " is not null"), isbn13NoSep, is(not(nullValue())));
        Assert.assertThat((isbn13NoSep + " has length of 13"), isbn13NoSep.length(), is(13));
        assertIsValidISBN13(isbn13NoSep);
        Assert.assertThat((isbn13Sep + " is not null"), isbn13Sep, is(not(nullValue())));
        Assert.assertThat((isbn13Sep + " has length of 17"), isbn13Sep.length(), is(17));
        assertIsValidISBN13(isbn13Sep);
    }

    @Test
    @Repeat(times = 100)
    public void testOverrides() {
        Faker faker = new Faker(new Locale("test"));
        final String isbn10Sep = faker.code().isbn10(true);
        final String isbn13Sep = faker.code().isbn13(true);
        Assert.assertThat("Uses overridden expressions from test.yml", isbn10Sep, MatchesRegularExpression.matchesRegularExpression("9971-\\d-\\d{4}-(\\d|X)"));
        Assert.assertThat("Uses overridden expressions from test.yml", isbn13Sep, MatchesRegularExpression.matchesRegularExpression("(333|444)-9971-\\d-\\d{4}-\\d"));
    }

    @Test
    public void asin() {
        Assert.assertThat(faker.code().asin(), MatchesRegularExpression.matchesRegularExpression("B000([A-Z]|\\d){6}"));
    }

    @Test
    public void imei() {
        String imei = faker.code().imei();
        Assert.assertThat(imei, MatchesRegularExpression.matchesRegularExpression("\\A[\\d\\.\\:\\-\\s]+\\z"));
        Assert.assertThat(LUHN_CHECK_DIGIT.isValid(imei), is(true));
    }

    @Test
    public void ean8() {
        Assert.assertThat(faker.code().ean8(), MatchesRegularExpression.matchesRegularExpression("\\d{8}"));
    }

    @Test
    public void gtin8() {
        Assert.assertThat(faker.code().gtin8(), MatchesRegularExpression.matchesRegularExpression("\\d{8}"));
    }

    @Test
    public void ean13() {
        String ean13 = faker.code().ean13();
        Assert.assertThat(ean13, MatchesRegularExpression.matchesRegularExpression("\\d{13}"));
        Assert.assertThat(EAN13_CHECK_DIGIT.isValid(ean13), is(true));
    }

    @Test
    public void gtin13() {
        String gtin13 = faker.code().gtin13();
        Assert.assertThat(gtin13, MatchesRegularExpression.matchesRegularExpression("\\d{13}"));
        Assert.assertThat(EAN13_CHECK_DIGIT.isValid(gtin13), is(true));
    }
}

