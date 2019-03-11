/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.indexer.rotation.strategies;


import java.util.Optional;
import org.graylog2.audit.AuditEventSender;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.plugin.InstantMillisProvider;
import org.graylog2.plugin.system.NodeId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TimeBasedRotationStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Mock
    private Indices indices;

    @Mock
    private NodeId nodeId;

    @Mock
    private AuditEventSender auditEventSender;

    private TimeBasedRotationStrategy rotationStrategy;

    @Test
    public void determineAnchor() {
        final DateTime initialTime = new DateTime(2014, 3, 15, 14, 48, 35, 0, DateTimeZone.UTC);
        final InstantMillisProvider clock = new InstantMillisProvider(initialTime);
        DateTimeUtils.setCurrentMillisProvider(clock);
        Period period;
        // should snap to 14:00:00
        period = Period.hours(1);
        final DateTime hourAnchor = TimeBasedRotationStrategy.determineRotationPeriodAnchor(null, period);
        Assert.assertEquals(14, hourAnchor.getHourOfDay());
        Assert.assertEquals(0, hourAnchor.getMinuteOfHour());
        Assert.assertEquals(0, hourAnchor.getSecondOfMinute());
        // should snap to 14:45:00
        period = Period.minutes(5);
        final DateTime fiveMins = TimeBasedRotationStrategy.determineRotationPeriodAnchor(null, period);
        Assert.assertEquals(14, fiveMins.getHourOfDay());
        Assert.assertEquals(45, fiveMins.getMinuteOfHour());
        Assert.assertEquals(0, fiveMins.getSecondOfMinute());
        // should snap to 2014-3-15 00:00:00
        period = Period.days(1).withHours(6);
        final DateTime dayAnd6Hours = TimeBasedRotationStrategy.determineRotationPeriodAnchor(null, period);
        Assert.assertEquals(2014, dayAnd6Hours.getYear());
        Assert.assertEquals(3, dayAnd6Hours.getMonthOfYear());
        Assert.assertEquals(15, dayAnd6Hours.getDayOfMonth());
        Assert.assertEquals(0, dayAnd6Hours.getHourOfDay());
        Assert.assertEquals(0, dayAnd6Hours.getMinuteOfHour());
        Assert.assertEquals(0, dayAnd6Hours.getSecondOfMinute());
        period = Period.days(30);
        final DateTime thirtyDays = TimeBasedRotationStrategy.determineRotationPeriodAnchor(null, period);
        Assert.assertEquals(2014, thirtyDays.getYear());
        Assert.assertEquals(2, thirtyDays.getMonthOfYear());
        Assert.assertEquals(17, thirtyDays.getDayOfMonth());
        Assert.assertEquals(0, thirtyDays.getHourOfDay());
        Assert.assertEquals(0, thirtyDays.getMinuteOfHour());
        Assert.assertEquals(0, thirtyDays.getSecondOfMinute());
        period = Period.hours(1);
        final DateTime diffAnchor = TimeBasedRotationStrategy.determineRotationPeriodAnchor(initialTime.minusMinutes(61), period);
        Assert.assertEquals(2014, diffAnchor.getYear());
        Assert.assertEquals(3, diffAnchor.getMonthOfYear());
        Assert.assertEquals(15, diffAnchor.getDayOfMonth());
        Assert.assertEquals(13, diffAnchor.getHourOfDay());
        Assert.assertEquals(0, diffAnchor.getMinuteOfHour());
        Assert.assertEquals(0, diffAnchor.getSecondOfMinute());
    }

    @Test
    public void shouldRotateHourly() throws Exception {
        final DateTime initialTime = new DateTime(2014, 1, 1, 1, 59, 59, 0, DateTimeZone.UTC);
        final Period period = Period.hours(1);
        final InstantMillisProvider clock = new InstantMillisProvider(initialTime);
        DateTimeUtils.setCurrentMillisProvider(clock);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        Mockito.when(indices.indexCreationDate(ArgumentMatchers.anyString())).thenReturn(Optional.of(initialTime.minus(Period.minutes(5))));
        // Should not rotate the first index.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
        clock.tick(Period.seconds(2));
        // Crossed rotation period.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet);
        clock.tick(Period.seconds(2));
        // Did not cross rotation period.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
    }

    @Test
    public void shouldRotateNonIntegralPeriod() throws Exception {
        // start 5 minutes before full hour
        final DateTime initialTime = new DateTime(2014, 1, 1, 1, 55, 0, 0, DateTimeZone.UTC);
        final Period period = Period.minutes(10);
        final InstantMillisProvider clock = new InstantMillisProvider(initialTime);
        DateTimeUtils.setCurrentMillisProvider(clock);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        Mockito.when(indices.indexCreationDate(ArgumentMatchers.anyString())).thenReturn(Optional.of(initialTime.minus(Period.minutes(11))));
        // Should rotate the first index.
        // time is 01:55:00, index was created at 01:44:00, so we missed one period, and should rotate
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet);
        // advance time to 01:55:01
        clock.tick(Period.seconds(1));
        // Did not cross rotation period.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
        // advance time to 02:00:00
        clock.tick(Period.minutes(4).withSeconds(59));
        // Crossed rotation period.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet);
        // advance time multiple rotation periods into the future
        // to time 02:51:00
        clock.tick(Period.minutes(51));
        // Crossed multiple rotation periods.
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet);
        // move time to 2:52:00
        // this should not cycle again, because next valid rotation time is 3:00:00
        clock.tick(Period.minutes(1));
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet);
        Mockito.verify(indexSet, Mockito.never()).cycle();
        Mockito.reset(indexSet);
    }

    @Test
    public void shouldRotateThrowsNPEIfIndexSetConfigIsNull() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(null);
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Index set configuration must not be null");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateThrowsISEIfIndexIsNull() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSet.getNewestIndex()).thenReturn(null);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Index name must not be null or empty");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateThrowsISEIfIndexIsEmpty() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSet.getNewestIndex()).thenReturn("");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Index name must not be null or empty");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateThrowsISEIfIndexSetIdIsNull() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.id()).thenReturn(null);
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Index set ID must not be null or empty");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateThrowsISEIfIndexSetIdIsEmpty() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetConfig.id()).thenReturn("");
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Index set ID must not be null or empty");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateThrowsISEIfRotationStrategyHasIncorrectType() throws Exception {
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSet.getNewestIndex()).thenReturn("ignored");
        Mockito.when(indexSetConfig.rotationStrategy()).thenReturn(MessageCountRotationStrategyConfig.createDefault());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Invalid rotation strategy config");
        rotationStrategy.rotate(indexSet);
    }

    @Test
    public void shouldRotateConcurrently() throws Exception {
        final DateTime initialTime = new DateTime(2014, 1, 1, 1, 59, 59, 0, DateTimeZone.UTC);
        final Period period = Period.hours(1);
        final InstantMillisProvider clock = new InstantMillisProvider(initialTime);
        DateTimeUtils.setCurrentMillisProvider(clock);
        final IndexSet indexSet1 = Mockito.mock(IndexSet.class);
        final IndexSet indexSet2 = Mockito.mock(IndexSet.class);
        final IndexSetConfig indexSetConfig1 = Mockito.mock(IndexSetConfig.class);
        final IndexSetConfig indexSetConfig2 = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSetConfig1.id()).thenReturn("id1");
        Mockito.when(indexSetConfig2.id()).thenReturn("id2");
        Mockito.when(indexSet1.getConfig()).thenReturn(indexSetConfig1);
        Mockito.when(indexSet2.getConfig()).thenReturn(indexSetConfig2);
        Mockito.when(indexSetConfig1.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        Mockito.when(indexSetConfig2.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        Mockito.when(indices.indexCreationDate(ArgumentMatchers.anyString())).thenReturn(Optional.of(initialTime.minus(Period.minutes(5))));
        // Should not rotate the initial index.
        Mockito.when(indexSet1.getNewestIndex()).thenReturn("index1");
        rotationStrategy.rotate(indexSet1);
        Mockito.verify(indexSet1, Mockito.never()).cycle();
        Mockito.reset(indexSet1);
        Mockito.when(indexSet2.getNewestIndex()).thenReturn("index2");
        rotationStrategy.rotate(indexSet2);
        Mockito.verify(indexSet2, Mockito.never()).cycle();
        Mockito.reset(indexSet2);
        clock.tick(Period.seconds(2));
        // Crossed rotation period.
        Mockito.when(indexSet1.getNewestIndex()).thenReturn("index1");
        Mockito.when(indexSet1.getConfig()).thenReturn(indexSetConfig1);
        Mockito.when(indexSetConfig1.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet1);
        Mockito.verify(indexSet1, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet1);
        Mockito.when(indexSet2.getNewestIndex()).thenReturn("index2");
        Mockito.when(indexSet2.getConfig()).thenReturn(indexSetConfig2);
        Mockito.when(indexSetConfig2.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet2);
        Mockito.verify(indexSet2, VerificationModeFactory.times(1)).cycle();
        Mockito.reset(indexSet2);
        clock.tick(Period.seconds(2));
        // Did not cross rotation period.
        Mockito.when(indexSet1.getNewestIndex()).thenReturn("index1");
        Mockito.when(indexSet1.getConfig()).thenReturn(indexSetConfig1);
        Mockito.when(indexSetConfig1.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet1);
        Mockito.verify(indexSet1, Mockito.never()).cycle();
        Mockito.reset(indexSet1);
        Mockito.when(indexSet2.getNewestIndex()).thenReturn("index2");
        Mockito.when(indexSet2.getConfig()).thenReturn(indexSetConfig2);
        Mockito.when(indexSetConfig2.rotationStrategy()).thenReturn(TimeBasedRotationStrategyConfig.create(period));
        rotationStrategy.rotate(indexSet2);
        Mockito.verify(indexSet2, Mockito.never()).cycle();
        Mockito.reset(indexSet2);
    }
}

