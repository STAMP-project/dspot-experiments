package com.baeldung.random;


import java.nio.charset.Charset;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaRandomUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(JavaRandomUnitTest.class);

    // tests - random long
    @Test
    public void givenUsingPlainJava_whenGeneratingRandomLongUnbounded_thenCorrect() {
        final long generatedLong = new Random().nextLong();
        JavaRandomUnitTest.LOG.debug("{}", generatedLong);
    }

    @Test
    public void givenUsingApacheCommons_whenGeneratingRandomLongUnbounded_thenCorrect() {
        final long generatedLong = new RandomDataGenerator().getRandomGenerator().nextLong();
        JavaRandomUnitTest.LOG.debug("{}", generatedLong);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomLongBounded_thenCorrect() {
        final long leftLimit = 1L;
        final long rightLimit = 10L;
        final long generatedLong = leftLimit + ((long) ((Math.random()) * (rightLimit - leftLimit)));
        JavaRandomUnitTest.LOG.debug("{}", generatedLong);
    }

    @Test
    public void givenUsingApacheCommons_whenGeneratingRandomLongBounded_thenCorrect() {
        final long leftLimit = 10L;
        final long rightLimit = 100L;
        final long generatedLong = new RandomDataGenerator().nextLong(leftLimit, rightLimit);
        JavaRandomUnitTest.LOG.debug("{}", generatedLong);
    }

    // tests - random int
    @Test
    public void givenUsingPlainJava_whenGeneratingRandomIntegerUnbounded_thenCorrect() {
        final int generatedInteger = new Random().nextInt();
        JavaRandomUnitTest.LOG.debug("{}", generatedInteger);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomIntegerBounded_thenCorrect() {
        final int leftLimit = 1;
        final int rightLimit = 10;
        final int generatedInteger = leftLimit + ((int) ((new Random().nextFloat()) * (rightLimit - leftLimit)));
        JavaRandomUnitTest.LOG.debug("{}", generatedInteger);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomIntegerUnbounded_thenCorrect() {
        final Integer generatedInteger = new RandomDataGenerator().getRandomGenerator().nextInt();
        JavaRandomUnitTest.LOG.debug("{}", generatedInteger);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomIntegerBounded_thenCorrect() {
        final int leftLimit = 1;
        final int rightLimit = 10;
        final int generatedInteger = new RandomDataGenerator().nextInt(leftLimit, rightLimit);
        JavaRandomUnitTest.LOG.debug("{}", generatedInteger);
    }

    // tests - random float
    @Test
    public void givenUsingPlainJava_whenGeneratingRandomFloatUnbouned_thenCorrect() {
        final float generatedFloat = new Random().nextFloat();
        JavaRandomUnitTest.LOG.debug("{}", generatedFloat);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomFloatUnbounded_thenCorrect() {
        final float generatedFloat = new RandomDataGenerator().getRandomGenerator().nextFloat();
        JavaRandomUnitTest.LOG.debug("{}", generatedFloat);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomFloatBouned_thenCorrect() {
        final float leftLimit = 1.0F;
        final float rightLimit = 10.0F;
        final float generatedFloat = leftLimit + ((new Random().nextFloat()) * (rightLimit - leftLimit));
        JavaRandomUnitTest.LOG.debug("{}", generatedFloat);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomFloatBounded_thenCorrect() {
        final float leftLimit = 1.0F;
        final float rightLimit = 10.0F;
        final float randomFloat = new RandomDataGenerator().getRandomGenerator().nextFloat();
        final float generatedFloat = leftLimit + (randomFloat * (rightLimit - leftLimit));
        JavaRandomUnitTest.LOG.debug("{}", generatedFloat);
    }

    // tests - random double
    @Test
    public void givenUsingPlainJava_whenGeneratingRandomDoubleUnbounded_thenCorrect() {
        final double generatedDouble = Math.random();
        JavaRandomUnitTest.LOG.debug("{}", generatedDouble);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomDoubleUnbounded_thenCorrect() {
        final double generatedDouble = new RandomDataGenerator().getRandomGenerator().nextDouble();
        JavaRandomUnitTest.LOG.debug("{}", generatedDouble);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomDoubleBounded_thenCorrect() {
        final double leftLimit = 1.0;
        final double rightLimit = 10.0;
        final double generatedDouble = leftLimit + ((new Random().nextDouble()) * (rightLimit - leftLimit));
        JavaRandomUnitTest.LOG.debug("{}", generatedDouble);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomDoubleBounded_thenCorrect() {
        final double leftLimit = 1.0;
        final double rightLimit = 100.0;
        final double generatedDouble = new RandomDataGenerator().nextUniform(leftLimit, rightLimit);
        JavaRandomUnitTest.LOG.debug("{}", generatedDouble);
    }

    // tests - random String
    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        final byte[] array = new byte[7];// length is bounded by 7

        new Random().nextBytes(array);
        final String generatedString = new String(array, Charset.forName("UTF-8"));
        JavaRandomUnitTest.LOG.debug(generatedString);
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringBounded_thenCorrect() {
        final int leftLimit = 97;// letter 'a'

        final int rightLimit = 122;// letter 'z'

        final int targetStringLength = 10;
        final Random random = new Random();
        final StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            final int randomLimitedInt = leftLimit + ((int) ((random.nextFloat()) * ((rightLimit - leftLimit) + 1)));
            buffer.append(((char) (randomLimitedInt)));
        }
        final String generatedString = buffer.toString();
        JavaRandomUnitTest.LOG.debug(generatedString);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomString_thenCorrect() {
        final String generatedString = RandomStringUtils.random(10);
        JavaRandomUnitTest.LOG.debug(generatedString);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomAlphabeticString_thenCorrect() {
        final String generatedString = RandomStringUtils.randomAlphabetic(10);
        JavaRandomUnitTest.LOG.debug(generatedString);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
        final String generatedString = RandomStringUtils.randomAlphanumeric(10);
        JavaRandomUnitTest.LOG.debug(generatedString);
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomStringBounded_thenCorrect() {
        final int length = 10;
        final boolean useLetters = true;
        final boolean useNumbers = false;
        final String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        JavaRandomUnitTest.LOG.debug(generatedString);
    }
}

