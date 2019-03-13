package net.openhft.chronicle.queue.impl;


import RollCycles.DAILY;
import RollCycles.HOURLY;
import RollCycles.MINUTELY;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.openhft.chronicle.queue.RollCycles;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RollingResourcesCacheTest {
    private static final long SEED = 2983472039423847L;

    private static final long AM_EPOCH = 1523498933145L;// 2018-04-12 02:08:53.145 UTC


    private static final int AM_DAILY_CYCLE_NUMBER = 1;

    private static final int AM_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.AM_DAILY_CYCLE_NUMBER) * 24;

    private static final int AM_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.AM_HOURLY_CYCLE_NUMBER) * 60;

    private static final String AM_DAILY_FILE_NAME = "20180413";

    private static final String AM_HOURLY_FILE_NAME_0 = "20180413-00";

    private static final String AM_HOURLY_FILE_NAME_15 = "20180413-15";

    private static final String AM_MINUTELY_FILE_NAME_0 = "20180413-0000";

    private static final String AM_MINUTELY_FILE_NAME_10 = "20180413-0010";

    private static final long PM_EPOCH = 1284739200000L;// 2010-09-17 16:00:00.000 UTC


    private static final int PM_DAILY_CYCLE_NUMBER = 2484;

    private static final int PM_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.PM_DAILY_CYCLE_NUMBER) * 24;

    private static final int PM_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.PM_HOURLY_CYCLE_NUMBER) * 60;

    private static final String PM_DAILY_FILE_NAME = "20170706";

    private static final String PM_HOURLY_FILE_NAME_0 = "20170706-00";

    private static final String PM_HOURLY_FILE_NAME_15 = "20170706-15";

    private static final String PM_MINUTELY_FILE_NAME_0 = "20170706-0000";

    private static final String PM_MINUTELY_FILE_NAME_10 = "20170706-0010";

    private static final long POSITIVE_RELATIVE_EPOCH = 18000000L;// +5 hours


    private static final int POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER = 2484;

    private static final int POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER) * 24;

    private static final int POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER) * 60;

    private static final String POSITIVE_RELATIVE_DAILY_FILE_NAME = "19761020";

    private static final String POSITIVE_RELATIVE_HOURLY_FILE_NAME_0 = "19761020-00";

    private static final String POSITIVE_RELATIVE_HOURLY_FILE_NAME_15 = "19761020-15";

    private static final String POSITIVE_RELATIVE_MINUTELY_FILE_NAME_0 = "19761020-0000";

    private static final String POSITIVE_RELATIVE_MINUTELY_FILE_NAME_10 = "19761020-0010";

    private static final long BIG_POSITIVE_RELATIVE_EPOCH = 54000000L;// +15 hours


    private static final int BIG_POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER = 2484;

    private static final int BIG_POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER) * 24;

    private static final int BIG_POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER) * 60;

    private static final String BIG_POSITIVE_RELATIVE_DAILY_FILE_NAME = "19761020";

    private static final String BIG_POSITIVE_RELATIVE_HOURLY_FILE_NAME_0 = "19761020-00";

    private static final String BIG_POSITIVE_RELATIVE_HOURLY_FILE_NAME_15 = "19761020-15";

    private static final String BIG_POSITIVE_RELATIVE_MINUTELY_FILE_NAME_0 = "19761020-0000";

    private static final String BIG_POSITIVE_RELATIVE_MINUTELY_FILE_NAME_10 = "19761020-0010";

    private static final long NEGATIVE_RELATIVE_EPOCH = -10800000L;// -3 hours


    private static final int NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER = 2484;

    private static final int NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER) * 24;

    private static final int NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER) * 60;

    private static final String NEGATIVE_RELATIVE_DAILY_FILE_NAME = "19761019";

    private static final String NEGATIVE_RELATIVE_HOURLY_FILE_NAME_0 = "19761019-00";

    private static final String NEGATIVE_RELATIVE_HOURLY_FILE_NAME_15 = "19761019-15";

    private static final String NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_0 = "19761019-0000";

    private static final String NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_10 = "19761019-0010";

    private static final long BIG_NEGATIVE_RELATIVE_EPOCH = -10800000L;// -13 hours


    private static final int BIG_NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER = 2484;

    private static final int BIG_NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER = (RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER) * 24;

    private static final int BIG_NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER = (RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER) * 60;

    private static final String BIG_NEGATIVE_RELATIVE_DAILY_FILE_NAME = "19761019";

    private static final String BIG_NEGATIVE_RELATIVE_HOURLY_FILE_NAME_0 = "19761019-00";

    private static final String BIG_NEGATIVE_RELATIVE_HOURLY_FILE_NAME_15 = "19761019-15";

    private static final String BIG_NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_0 = "19761019-0000";

    private static final String BIG_NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_10 = "19761019-0010";

    private static final long ONE_DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1L);

    private static final boolean LOG_TEST_DEBUG = Boolean.valueOf(((RollingResourcesCacheTest.class.getSimpleName()) + ".debug"));

    @Test
    public void shouldConvertCyclesToResourceNamesWithNoEpoch() throws Exception {
        final int epoch = 0;
        final RollingResourcesCache cache = new RollingResourcesCache(RollCycles.DAILY, epoch, File::new, File::getName);
        final int cycle = DAILY.current(System::currentTimeMillis, 0);
        RollingResourcesCacheTest.assertCorrectConversion(cache, cycle, Instant.now(), DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("GMT")));
    }

    @Test
    public void shouldCorrectlyConvertCyclesToResourceNamesWithEpoch() throws Exception {
        // AM_EPOCH is 2018-04-12 02:08:53.145 UTC
        // cycle 24 should be formatted as:
        // 2018-04-12 00:00:00 UTC (1523491200000) +
        // Timezone offset 02:08:53.145 (7733145) +
        // 24 hourly cycles (24 * 3_600_000) =
        // 1523585333145 = Friday, 13 April 2018 02:08:53.145 UTC ie. filename is 20180413 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.AM_EPOCH, DAILY, RollingResourcesCacheTest.AM_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.AM_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.AM_EPOCH, HOURLY, RollingResourcesCacheTest.AM_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.AM_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.AM_EPOCH, HOURLY, ((RollingResourcesCacheTest.AM_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.AM_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.AM_EPOCH, MINUTELY, RollingResourcesCacheTest.AM_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.AM_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.AM_EPOCH, MINUTELY, ((RollingResourcesCacheTest.AM_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.AM_MINUTELY_FILE_NAME_10);
        // PM_EPOCH is 2010-09-17 16:00:00.000 UTC
        // cycle 2484 should be formatted as:
        // 2010-09-17 00:00:00 UTC (1284681600000) +
        // Timezone offset 16:00:00.000 (57600000) +
        // 2484 daily cycles (2484 * 86_400_000 = 214617600000)
        // 1499356800000 = Thursday, 6 July 2017 16:00:00 UTC ie. filename is 20170706 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.PM_EPOCH, DAILY, RollingResourcesCacheTest.PM_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.PM_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.PM_EPOCH, HOURLY, RollingResourcesCacheTest.PM_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.PM_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.PM_EPOCH, HOURLY, ((RollingResourcesCacheTest.PM_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.PM_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.PM_EPOCH, MINUTELY, RollingResourcesCacheTest.PM_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.PM_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.PM_EPOCH, MINUTELY, ((RollingResourcesCacheTest.PM_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.PM_MINUTELY_FILE_NAME_10);
        // POSITIVE_RELATIVE_EPOCH is 5 hours (18000000 millis)
        // cycle 2484 should be formatted as:
        // epoch 1970-01-01 00:00:00 (0) +
        // Timezone offset 05:00:00 (18000000) +
        // 2484 daily cycles (2484 * 86_400_000 = 214617600000) =
        // 214635600000 - Wednesday, 20 October 1976 05:00:00 UTC ie. filename is 19761020 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, DAILY, RollingResourcesCacheTest.POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.POSITIVE_RELATIVE_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, HOURLY, RollingResourcesCacheTest.POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.POSITIVE_RELATIVE_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, HOURLY, ((RollingResourcesCacheTest.POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.POSITIVE_RELATIVE_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, MINUTELY, RollingResourcesCacheTest.POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.POSITIVE_RELATIVE_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, MINUTELY, ((RollingResourcesCacheTest.POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.POSITIVE_RELATIVE_MINUTELY_FILE_NAME_10);
        // BIG_POSITIVE_RELATIVE_EPOCH is 15 hours (54000000 millis)
        // cycle 2484 should be formatted as:
        // epoch 1970-01-01 00:00:00 (0) +
        // Timezone offset 05:00:00 (54000000) +
        // 2484 daily cycles (2484 * 86_400_000 = 214617600000) =
        // 214671600000 - Wednesday, 20 October 1976 15:00:00 UTC ie. filename is 19761020 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_EPOCH, DAILY, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_EPOCH, HOURLY, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_EPOCH, HOURLY, ((RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_EPOCH, MINUTELY, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_EPOCH, MINUTELY, ((RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.BIG_POSITIVE_RELATIVE_MINUTELY_FILE_NAME_10);
        // NEGATIVE_RELATIVE_EPOCH is -3 hours (-10800000 millis)
        // cycle 2484 should be formatted as:
        // epoch 1969-12-31 00:00:00 (-86400000) +
        // Timezone offset -03:00:00 (-10800000) +
        // 2484 daily cycles (2484 * 86_400_000 = 214617600000) =
        // 214520400000 - Monday, 18 October 1976 21:00:00 UTC ie. filename is 19761019 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, DAILY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.NEGATIVE_RELATIVE_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, HOURLY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.NEGATIVE_RELATIVE_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, HOURLY, ((RollingResourcesCacheTest.NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.NEGATIVE_RELATIVE_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, MINUTELY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, MINUTELY, ((RollingResourcesCacheTest.NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_10);
        // NEGATIVE_RELATIVE_EPOCH is -13 hours (-46800000 millis)
        // cycle 2484 should be formatted as:
        // epoch 1969-12-31 00:00:00 (-86400000) +
        // Timezone offset -03:00:00 (-46800000) +
        // 2484 daily cycles (2484 * 86_400_000 = 214617600000) =
        // 214484400000 - Monday, 18 October 1976 11:00:00 UTC ie. filename is 19761019 local
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_EPOCH, DAILY, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_DAILY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_DAILY_FILE_NAME);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_EPOCH, HOURLY, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_HOURLY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_EPOCH, HOURLY, ((RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_HOURLY_CYCLE_NUMBER) + 15), RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_HOURLY_FILE_NAME_15);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_EPOCH, MINUTELY, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER, RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_0);
        doTestCycleAndResourceNames(RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_EPOCH, MINUTELY, ((RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_MINUTELY_CYCLE_NUMBER) + 10), RollingResourcesCacheTest.BIG_NEGATIVE_RELATIVE_MINUTELY_FILE_NAME_10);
    }

    @Test(expected = RuntimeException.class)
    public void parseIncorrectlyFormattedName() throws Exception {
        final RollingResourcesCache cache = new RollingResourcesCache(RollCycles.HOURLY, RollingResourcesCacheTest.PM_EPOCH, File::new, File::getName);
        cache.parseCount("foobar-qux");
    }

    @Test
    public void fuzzyConversionTest() throws Exception {
        final int maxAddition = ((int) (ChronoUnit.DECADES.getDuration().toMillis()));
        final Random random = new Random(RollingResourcesCacheTest.SEED);
        for (int i = 0; i < 1000; i++) {
            final long epoch = random.nextInt(maxAddition);
            final RollingResourcesCache cache = new RollingResourcesCache(RollCycles.DAILY, epoch, File::new, File::getName);
            for (int j = 0; j < 200; j++) {
                final long offsetMillisFromEpoch = ((TimeUnit.DAYS.toMillis(random.nextInt(500))) + (TimeUnit.HOURS.toMillis(random.nextInt(50)))) + (TimeUnit.MINUTES.toMillis(random.nextInt(50)));
                final long instantAfterEpoch = epoch + offsetMillisFromEpoch;
                final ZoneId zoneId = ZoneId.of("UTC");
                final int cycle = DAILY.current(() -> instantAfterEpoch, epoch);
                final long daysBetweenEpochAndInstant = (instantAfterEpoch - epoch) / (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS);
                Assert.assertThat(((long) (cycle)), CoreMatchers.is(daysBetweenEpochAndInstant));
                Assert.assertThat((((long) (cycle)) * (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS)), CoreMatchers.is((((long) (cycle)) * (DAILY.length()))));
                if (RollingResourcesCacheTest.LOG_TEST_DEBUG) {
                    System.out.printf("Epoch: %d%n", epoch);
                    System.out.printf("Epoch millis: %d(UTC+%dd), current millis: %d(UTC+%dd)%n", epoch, (epoch / (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS)), instantAfterEpoch, (instantAfterEpoch / (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS)));
                    System.out.printf("Delta days: %d, Delta millis: %d, Delta days in millis: %d%n", daysBetweenEpochAndInstant, (instantAfterEpoch - epoch), (daysBetweenEpochAndInstant * (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS)));
                    System.out.printf("MillisSinceEpoch: %d%n", offsetMillisFromEpoch);
                    System.out.printf("Resource calc of millisSinceEpoch: %d%n", (daysBetweenEpochAndInstant * (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS)));
                }
                long effectiveCycleStartTime = (instantAfterEpoch - epoch) - ((instantAfterEpoch - epoch) % (RollingResourcesCacheTest.ONE_DAY_IN_MILLIS));
                RollingResourcesCacheTest.assertCorrectConversion(cache, cycle, Instant.ofEpochMilli((effectiveCycleStartTime + epoch)), DateTimeFormatter.ofPattern("yyyyMMdd").withZone(zoneId));
            }
        }
    }

    @Test
    public void testToLong() {
        doTestToLong(DAILY, RollingResourcesCacheTest.AM_EPOCH, 0, Long.valueOf("17633"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.AM_EPOCH, 0, Long.valueOf("423192"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.AM_EPOCH, 0, Long.valueOf("25391520"));
        doTestToLong(DAILY, RollingResourcesCacheTest.AM_EPOCH, 100, Long.valueOf("17733"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.AM_EPOCH, 100, Long.valueOf("423292"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.AM_EPOCH, 100, Long.valueOf("25391620"));
        doTestToLong(DAILY, RollingResourcesCacheTest.PM_EPOCH, 0, Long.valueOf("14869"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.PM_EPOCH, 0, Long.valueOf("356856"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.PM_EPOCH, 0, Long.valueOf("21411360"));
        doTestToLong(DAILY, RollingResourcesCacheTest.PM_EPOCH, 100, Long.valueOf("14969"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.PM_EPOCH, 100, Long.valueOf("356956"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.PM_EPOCH, 100, Long.valueOf("21411460"));
        doTestToLong(DAILY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 0, Long.valueOf("0"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 0, Long.valueOf("0"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 0, Long.valueOf("0"));
        doTestToLong(DAILY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 100, Long.valueOf("100"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 100, Long.valueOf("100"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.POSITIVE_RELATIVE_EPOCH, 100, Long.valueOf("100"));
        doTestToLong(DAILY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 0, Long.valueOf("-1"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 0, Long.valueOf("-24"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 0, Long.valueOf("-1440"));
        doTestToLong(DAILY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 100, Long.valueOf("99"));
        doTestToLong(HOURLY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 100, Long.valueOf("76"));
        doTestToLong(MINUTELY, RollingResourcesCacheTest.NEGATIVE_RELATIVE_EPOCH, 100, Long.valueOf("-1340"));
    }
}

