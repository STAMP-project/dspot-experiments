package com.github.javafaker;


import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RandomFakerTest extends AbstractFakerTest {
    private static final int CONSTANT_SEED_VALUE = 10;

    private Faker faker;

    private Random random;

    @Test
    public void testNumerifyRandomnessCanBeControlled() {
        resetRandomSeed();
        final String firstInvocation = faker.numerify("###");
        resetRandomSeed();
        final String secondInvocation = faker.numerify("###");
        MatcherAssert.assertThat(firstInvocation, Matchers.is(secondInvocation));
    }

    @Test
    public void testLetterifyRandomnessCanBeControlled() {
        resetRandomSeed();
        final String firstInvocation = faker.letterify("???");
        resetRandomSeed();
        final String secondInvocation = faker.letterify("???");
        MatcherAssert.assertThat(firstInvocation, Matchers.is(secondInvocation));
    }

    @Test
    public void testNameRandomnessCanBeControlled() {
        resetRandomSeed();
        final String firstInvocation = faker.name().name();
        resetRandomSeed();
        final String secondInvocation = faker.name().name();
        MatcherAssert.assertThat(firstInvocation, Matchers.is(secondInvocation));
    }

    @Test
    public void testEmailRandomnessCanBeControlled() {
        resetRandomSeed();
        final String firstInvocation = faker.internet().emailAddress();
        resetRandomSeed();
        final String secondInvocation = faker.internet().emailAddress();
        MatcherAssert.assertThat(firstInvocation, Matchers.is(secondInvocation));
    }
}

