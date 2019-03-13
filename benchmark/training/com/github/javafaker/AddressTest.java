package com.github.javafaker;


import com.github.javafaker.matchers.IsANumber;
import com.github.javafaker.matchers.MatchesRegularExpression;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class AddressTest extends AbstractFakerTest {
    private static final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();

    @Test
    public void testStreetAddressStartsWithNumber() {
        final String streetAddressNumber = faker.address().streetAddress();
        Assert.assertThat(streetAddressNumber, MatchesRegularExpression.matchesRegularExpression("[0-9]+ .+"));
    }

    @Test
    public void testStreetAddressIsANumber() {
        final String streetAddressNumber = faker.address().streetAddressNumber();
        Assert.assertThat(streetAddressNumber, MatchesRegularExpression.matchesRegularExpression("[0-9]+"));
    }

    @Test
    public void testLatitude() {
        String latStr;
        Double lat;
        for (int i = 0; i < 100; i++) {
            latStr = faker.address().latitude().replace(AddressTest.decimalSeparator, '.');
            Assert.assertThat(latStr, IsANumber.isANumber());
            lat = new Double(latStr);
            Assert.assertThat("Latitude is less then -90", lat, greaterThanOrEqualTo((-90.0)));
            Assert.assertThat("Latitude is greater than 90", lat, lessThanOrEqualTo(90.0));
        }
    }

    @Test
    public void testLongitude() {
        String longStr;
        Double lon;
        for (int i = 0; i < 100; i++) {
            longStr = faker.address().longitude().replace(AddressTest.decimalSeparator, '.');
            Assert.assertThat(longStr, IsANumber.isANumber());
            lon = new Double(longStr);
            Assert.assertThat("Longitude is less then -180", lon, greaterThanOrEqualTo((-180.0)));
            Assert.assertThat("Longitude is greater than 180", lon, lessThanOrEqualTo(180.0));
        }
    }

    @Test
    public void testTimeZone() {
        Assert.assertThat(faker.address().timeZone(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z_]+/[A-Za-z_]+[/A-Za-z_]*"));
    }

    @Test
    public void testState() {
        Assert.assertThat(faker.address().state(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void testCity() {
        Assert.assertThat(faker.address().city(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() ]+"));
    }

    @Test
    public void testCityName() {
        Assert.assertThat(faker.address().cityName(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z'() ]+"));
    }

    @Test
    public void testCountry() {
        Assert.assertThat(faker.address().country(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z\\- &.,\'()\\d]+"));
    }

    @Test
    public void testCountryCode() {
        Assert.assertThat(faker.address().countryCode(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z ]+"));
    }

    @Test
    public void testStreetAddressIncludeSecondary() {
        Assert.assertThat(faker.address().streetAddress(true), not(isEmptyString()));
    }

    @Test
    public void testCityWithLocaleFranceAndSeed() {
        long seed = 1L;
        Faker firstFaker = new Faker(Locale.FRANCE, new Random(seed));
        Faker secondFaker = new Faker(Locale.FRANCE, new Random(seed));
        Assert.assertThat(firstFaker.address().city(), is(secondFaker.address().city()));
    }

    @Test
    public void testFullAddress() {
        Assert.assertThat(faker.address().fullAddress(), not(isEmptyOrNullString()));
    }

    @Test
    public void testZipCodeByState() {
        faker = new Faker(new Locale("en-US"));
        Assert.assertThat(faker.address().zipCodeByState(faker.address().stateAbbr()), MatchesRegularExpression.matchesRegularExpression("[0-9]{5}"));
    }

    @Test
    public void testCountyByZipCode() {
        faker = new Faker(new Locale("en-US"));
        Assert.assertThat(faker.address().countyByZipCode(faker.address().zipCodeByState(faker.address().stateAbbr())), not(isEmptyOrNullString()));
    }
}

