package org.jivesoftware.util;


import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CacheableOptionalTest {
    @Test
    public void willCorrectlyRecordPresenceAndAbsence() {
        MatcherAssert.assertThat(CacheableOptional.of("my-test").isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(CacheableOptional.of(null).isAbsent(), CoreMatchers.is(true));
    }

    @Test
    public void willConvertToAndFromJavaOptional() {
        final Optional<String> value = Optional.of("my-test");
        final Optional<String> value2 = CacheableOptional.from(value).toOptional();
        MatcherAssert.assertThat(value, CoreMatchers.is(value2));
    }

    @Test
    public void equalsIsAppropriate() {
        MatcherAssert.assertThat(CacheableOptional.of("my-test"), CoreMatchers.is(CacheableOptional.of("my-test")));
        MatcherAssert.assertThat(CacheableOptional.of("my-test"), CoreMatchers.is(CoreMatchers.not(CacheableOptional.of("not-my-test"))));
    }

    @Test
    public void hashCodeIsAppropriate() {
        MatcherAssert.assertThat(CacheableOptional.of("my-test").hashCode(), CoreMatchers.is(CacheableOptional.of("my-test").hashCode()));
        MatcherAssert.assertThat(CacheableOptional.of("my-test").hashCode(), CoreMatchers.is(CoreMatchers.not(CacheableOptional.of("not-my-test").hashCode())));
    }

    @Test
    public void cacheSizeOfAbsentCacheableOptionalStringIsCorrect() throws Exception {
        final CacheableOptional<String> co = CacheableOptional.of(null);
        final int actualCachedSize = calculateCachedSize(co);
        MatcherAssert.assertThat(co.getCachedSize(), CoreMatchers.is(actualCachedSize));
    }

    @Test
    public void cacheSizeOfPresentCacheableOptionalStringIsCorrect() throws Exception {
        final CacheableOptional<String> co = CacheableOptional.of("my-test");
        final int actualCachedSize = calculateCachedSize(co);
        MatcherAssert.assertThat(co.getCachedSize(), CoreMatchers.is(actualCachedSize));
    }

    @Test
    public void cacheSizeOfAbsentCacheableOptionalBooleanIsCorrect() throws Exception {
        final CacheableOptional<Boolean> co = CacheableOptional.of(null);
        final int actualCachedSize = calculateCachedSize(co);
        MatcherAssert.assertThat(co.getCachedSize(), CoreMatchers.is(actualCachedSize));
    }
}

