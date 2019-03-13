/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.security.apitoken;


import ApiTokenStats.SingleTokenStats;
import hudson.XmlFile;
import java.lang.reflect.Field;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static ApiTokenStats.SingleTokenStats;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ApiTokenPropertyConfiguration.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ApiTokenStatsTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void regularUsage() throws Exception {
        final String ID_1 = UUID.randomUUID().toString();
        final String ID_2 = "other-uuid";
        {
            // empty stats can be saved
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            // can remove an id that does not exist
            tokenStats.removeId(ID_1);
            tokenStats.save();
        }
        {
            // and then loaded, empty stats is empty
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            Assert.assertNotNull(tokenStats);
            ApiTokenStats.SingleTokenStats stats = tokenStats.findTokenStatsById(ID_1);
            Assert.assertEquals(0, stats.getUseCounter());
            Assert.assertNull(stats.getLastUseDate());
            Assert.assertEquals(0L, stats.getNumDaysUse());
        }
        Date lastUsage;
        {
            // then re-notify the same token
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            ApiTokenStats.SingleTokenStats stats = tokenStats.updateUsageForId(ID_1);
            Assert.assertEquals(1, stats.getUseCounter());
            lastUsage = stats.getLastUseDate();
            Assert.assertNotNull(lastUsage);
            // to avoid flaky test in case the test is run at midnight, normally it's 0
            Assert.assertThat(stats.getNumDaysUse(), lessThanOrEqualTo(1L));
        }
        // to enforce a difference in the lastUseDate
        Thread.sleep(10);
        {
            // then re-notify the same token
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            ApiTokenStats.SingleTokenStats stats = tokenStats.updateUsageForId(ID_1);
            Assert.assertEquals(2, stats.getUseCounter());
            Assert.assertThat(lastUsage, lessThan(stats.getLastUseDate()));
            // to avoid flaky test in case the test is run at midnight, normally it's 0
            Assert.assertThat(stats.getNumDaysUse(), lessThanOrEqualTo(1L));
        }
        {
            // check all tokens have separate stats, try with another ID
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            {
                ApiTokenStats.SingleTokenStats stats = tokenStats.findTokenStatsById(ID_2);
                Assert.assertEquals(0, stats.getUseCounter());
                Assert.assertNull(stats.getLastUseDate());
                Assert.assertEquals(0L, stats.getNumDaysUse());
            }
            {
                ApiTokenStats.SingleTokenStats stats = tokenStats.updateUsageForId(ID_2);
                Assert.assertEquals(1, stats.getUseCounter());
                Assert.assertNotNull(lastUsage);
                Assert.assertThat(stats.getNumDaysUse(), lessThanOrEqualTo(1L));
            }
        }
        {
            // reload the stats, check the counter are correct
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            ApiTokenStats.SingleTokenStats stats_1 = tokenStats.findTokenStatsById(ID_1);
            Assert.assertEquals(2, stats_1.getUseCounter());
            ApiTokenStats.SingleTokenStats stats_2 = tokenStats.findTokenStatsById(ID_2);
            Assert.assertEquals(1, stats_2.getUseCounter());
            tokenStats.removeId(ID_1);
        }
        {
            // after a removal, the existing must keep its value
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            ApiTokenStats.SingleTokenStats stats_1 = tokenStats.findTokenStatsById(ID_1);
            Assert.assertEquals(0, stats_1.getUseCounter());
            ApiTokenStats.SingleTokenStats stats_2 = tokenStats.findTokenStatsById(ID_2);
            Assert.assertEquals(1, stats_2.getUseCounter());
        }
    }

    @Test
    public void testResilientIfFileDoesNotExist() throws Exception {
        ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
        Assert.assertNotNull(tokenStats);
    }

    @Test
    public void resistantToDuplicatedUuid() throws Exception {
        final String ID_1 = UUID.randomUUID().toString();
        final String ID_2 = UUID.randomUUID().toString();
        final String ID_3 = UUID.randomUUID().toString();
        {
            // put counter to 4 for ID_1 and to 2 for ID_2 and 1 for ID_3
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            tokenStats.updateUsageForId(ID_1);
            tokenStats.updateUsageForId(ID_1);
            tokenStats.updateUsageForId(ID_1);
            tokenStats.updateUsageForId(ID_1);
            tokenStats.updateUsageForId(ID_2);
            tokenStats.updateUsageForId(ID_3);
            // only the most recent information is kept
            tokenStats.updateUsageForId(ID_2);
        }
        {
            // replace the ID_1 with ID_2 in the file
            XmlFile statsFile = ApiTokenStats.getConfigFile(tmp.getRoot());
            String content = FileUtils.readFileToString(statsFile.getFile());
            // now there are multiple times the same id in the file with different stats
            String newContentWithDuplicatedId = content.replace(ID_1, ID_2).replace(ID_3, ID_2);
            FileUtils.write(statsFile.getFile(), newContentWithDuplicatedId);
        }
        {
            ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
            Assert.assertNotNull(tokenStats);
            ApiTokenStats.SingleTokenStats stats_1 = tokenStats.findTokenStatsById(ID_1);
            Assert.assertEquals(0, stats_1.getUseCounter());
            // the most recent information is kept
            ApiTokenStats.SingleTokenStats stats_2 = tokenStats.findTokenStatsById(ID_2);
            Assert.assertEquals(2, stats_2.getUseCounter());
            ApiTokenStats.SingleTokenStats stats_3 = tokenStats.findTokenStatsById(ID_3);
            Assert.assertEquals(0, stats_3.getUseCounter());
        }
    }

    @Test
    public void resistantToDuplicatedUuid_withNull() throws Exception {
        final String ID = "ID";
        {
            // prepare
            List<ApiTokenStats.SingleTokenStats> tokenStatsList = /* A */
            /* B */
            /* C */
            /* D */
            Arrays.asList(createSingleTokenStatsByReflection(ID, null, 0), createSingleTokenStatsByReflection(ID, "2018-05-01 09:10:59.234", 2), createSingleTokenStatsByReflection(ID, "2018-05-01 09:10:59.234", 3), createSingleTokenStatsByReflection(ID, "2018-05-01 09:10:59.235", 1));
            ApiTokenStats stats = createFromFile(tmp.getRoot());
            Field field = ApiTokenStats.class.getDeclaredField("tokenStats");
            field.setAccessible(true);
            field.set(stats, tokenStatsList);
            stats.save();
        }
        {
            // reload to see the effect
            ApiTokenStats stats = createFromFile(tmp.getRoot());
            ApiTokenStats.SingleTokenStats tokenStats = stats.findTokenStatsById(ID);
            // must be D (as it was the last updated one)
            Assert.assertThat(tokenStats.getUseCounter(), equalTo(1));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInternalComparator() throws Exception {
        List<ApiTokenStats.SingleTokenStats> tokenStatsList = Arrays.asList(createSingleTokenStatsByReflection("A", null, 0), createSingleTokenStatsByReflection("B", "2018-05-01 09:10:59.234", 2), createSingleTokenStatsByReflection("C", "2018-05-01 09:10:59.234", 3), createSingleTokenStatsByReflection("D", "2018-05-01 09:10:59.235", 1));
        Field field = SingleTokenStats.class.getDeclaredField("COMP_BY_LAST_USE_THEN_COUNTER");
        field.setAccessible(true);
        Comparator<ApiTokenStats.SingleTokenStats> comparator = ((Comparator<ApiTokenStats.SingleTokenStats>) (field.get(null)));
        // to be not impacted by the declaration order
        Collections.shuffle(tokenStatsList, new Random(42));
        tokenStatsList.sort(comparator);
        List<String> idList = tokenStatsList.stream().map(SingleTokenStats::getTokenUuid).collect(Collectors.toList());
        Assert.assertThat(idList, contains("A", "B", "C", "D"));
    }

    @Test
    public void testDayDifference() throws Exception {
        final String ID = UUID.randomUUID().toString();
        ApiTokenStats tokenStats = createFromFile(tmp.getRoot());
        ApiTokenStats.SingleTokenStats stats = tokenStats.updateUsageForId(ID);
        Assert.assertThat(stats.getNumDaysUse(), lessThan(1L));
        Field field = SingleTokenStats.class.getDeclaredField("lastUseDate");
        field.setAccessible(true);
        field.set(stats, new Date(// to ensure we have more than 2 days
        new Date().toInstant().minus(2, ChronoUnit.DAYS).minus(5, ChronoUnit.MINUTES).toEpochMilli()));
        Assert.assertThat(stats.getNumDaysUse(), greaterThanOrEqualTo(2L));
    }
}

