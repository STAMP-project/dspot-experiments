package com.bumptech.glide.load.engine.cache;


import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class SafeKeyGeneratorTest {
    private SafeKeyGenerator keyGenerator;

    private int nextId;

    @Test
    public void testKeysAreValidForDiskCache() {
        final Pattern diskCacheRegex = Pattern.compile("[a-z0-9_-]{64}");
        for (int i = 0; i < 1000; i++) {
            String key = getRandomKeyFromGenerator();
            Matcher matcher = diskCacheRegex.matcher(key);
            Assert.assertTrue(key, matcher.matches());
        }
    }

    private static final class MockKey implements Key {
        private final String id;

        MockKey(String id) {
            this.id = id;
        }

        @Override
        public void updateDiskCacheKey(@NonNull
        MessageDigest messageDigest) {
            messageDigest.update(id.getBytes(CHARSET));
        }
    }
}

