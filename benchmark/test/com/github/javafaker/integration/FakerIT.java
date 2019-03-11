package com.github.javafaker.integration;


import com.github.javafaker.Faker;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The purpose of these tests is to ensure that the Locales have been properly configured
 * and that methods return values. The unit tests should ensure what the values returned
 * are correct. These tests just ensure that the methods can be invoked.
 */
@RunWith(Parameterized.class)
public class FakerIT {
    private static final Logger logger = LoggerFactory.getLogger(FakerIT.class);

    private final Locale locale;

    private final Faker faker;

    /**
     * a collection of Locales -> Exceptions.
     * In the case of 'pt', city_prefix is '' by design. This test fails because it's testing that all string returning
     * methods return a non blank string. But pt city_prefix is blank ,but the test shouldn't fail. So we add put
     * exceptions like this into this collection.
     */
    private static final Map<Locale, List<String>> exceptions = Maps.newHashMap();

    static {
        // 'it' has an empty suffix list so it never returns a value
        FakerIT.exceptions.put(new Locale("it"), Arrays.asList("Name.suffix"));
        FakerIT.exceptions.put(new Locale("es-mx"), Arrays.asList("Address.cityPrefix", "Address.citySuffix"));
        FakerIT.exceptions.put(new Locale("pt"), Arrays.asList("Address.cityPrefix", "Address.citySuffix"));
        FakerIT.exceptions.put(new Locale("uk"), Arrays.asList("Address.stateAbbr", "Address.streetSuffix", "Address.cityPrefix", "Address.citySuffix"));
    }

    public FakerIT(Locale locale, Random random) {
        this.locale = locale;
        if ((locale != null) && (random != null)) {
            faker = new Faker(locale, random);
        } else
            if (locale != null) {
                faker = new Faker(locale);
            } else
                if (random != null) {
                    faker = new Faker(random);
                } else {
                    faker = new Faker();
                }


    }

    @Test
    public void testAllFakerMethodsThatReturnStrings() throws Exception {
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker);
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.ancient());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.address());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.app());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.artist());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.business());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.book());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.chuckNorris());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.color());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.commerce());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.company());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.country());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.crypto());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.demographic());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.dragonBall());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.dog());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.educator());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.funnyName());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.hitchhikersGuideToTheGalaxy());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.hobbit());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.howIMetYourMother());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.internet());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.leagueOfLegends());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.lorem());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.overwatch());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.phoneNumber());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.pokemon());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.robin());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.starTrek());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.music());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.name());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.file());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.finance());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.food());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.gameOfThrones());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.hacker());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.idNumber());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.shakespeare());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.slackEmoji());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.space());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.stock());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.superhero());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.team());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.beer());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.university());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.cat());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.lordOfTheRings());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.zelda());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.harryPotter());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.rockBand());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.esports());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.friends());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.hipster());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.job());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.twinPeaks());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.rickAndMorty());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.yoda());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.matz());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.witcher());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.weather());
        testAllMethodsThatReturnStringsActuallyReturnStrings(faker.lebowski());
    }

    @Test
    public void testExceptionsNotCoveredInAboveTest() {
        Assert.assertThat(faker.bothify("####???"), is(IsNull.notNullValue()));
        Assert.assertThat(faker.letterify("????"), is(IsNull.notNullValue()));
        Assert.assertThat(faker.numerify("####"), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().paragraph(1), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().paragraphs(1), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().sentence(1), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().sentences(1), is(IsNull.notNullValue()));
        Assert.assertThat(faker.address().streetAddress(), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().words(), is(IsNull.notNullValue()));
        Assert.assertThat(faker.lorem().words(1), is(IsNull.notNullValue()));
    }
}

