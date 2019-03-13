package org.osmdroid.tileprovider.modules;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Fabrice Fontaine
 * @since 6.0.3
 */
public class TileDownloaderTest {
    private final long mCacheControlValue = 172800;// in seconds


    private final String[] mCacheControlStringOK = new String[]{ "max-age=172800, public", "public, max-age=172800", "max-age=172800" };

    private final String[] mCacheControlStringKO = new String[]{ "max-age=, public", "public" };

    private final long mExpiresValue = 1539971220000L;

    private final String[] mExpiresStringOK = new String[]{ "Fri, 19 Oct 2018 17:47:00 GMT" };

    private final String[] mExpiresStringKO = new String[]{ "Frfgi, 19 Oct 2018 17:47:00 GMT" };

    @Test
    public void testGetHttpExpiresTime() {
        final TileDownloader tileDownloader = new TileDownloader();
        for (final String string : mExpiresStringOK) {
            Assert.assertEquals(mExpiresValue, ((long) (tileDownloader.getHttpExpiresTime(string))));
        }
        for (final String string : mExpiresStringKO) {
            Assert.assertNull(tileDownloader.getHttpExpiresTime(string));
        }
    }

    @Test
    public void testGetHttpCacheControlDuration() {
        final TileDownloader tileDownloader = new TileDownloader();
        for (final String string : mCacheControlStringOK) {
            Assert.assertEquals(mCacheControlValue, ((long) (tileDownloader.getHttpCacheControlDuration(string))));
        }
        for (final String string : mCacheControlStringKO) {
            Assert.assertNull(tileDownloader.getHttpCacheControlDuration(string));
        }
    }

    @Test
    public void testComputeExpirationTime() {
        final Random random = new Random();
        final int oneWeek = ((7 * 24) * 3600) * 1000;// 7 days in milliseconds

        testComputeExpirationTimeHelper(null, random.nextInt(oneWeek));
        testComputeExpirationTimeHelper(((long) (random.nextInt(oneWeek))), random.nextInt(oneWeek));
    }
}

