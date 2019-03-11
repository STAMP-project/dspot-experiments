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
package org.graylog2.alarmcallbacks;


import com.google.inject.Injector;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AlarmCallbackFactoryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlarmCallbackFactory alarmCallbackFactory;

    @Mock
    private Injector injector;

    @Mock
    private AlarmCallbackFactoryTest.DummyAlarmCallback dummyAlarmCallback;

    public interface DummyAlarmCallback extends AlarmCallback {}

    @Test
    public void testCreateByAlarmCallbackConfiguration() throws Exception {
        AlarmCallbackConfiguration configuration = Mockito.mock(AlarmCallbackConfiguration.class);
        Mockito.when(configuration.getType()).thenReturn(AlarmCallbackFactoryTest.DummyAlarmCallback.class.getCanonicalName());
        AlarmCallback alarmCallback = alarmCallbackFactory.create(configuration);
        Assert.assertNotNull(alarmCallback);
        Assert.assertTrue((alarmCallback instanceof AlarmCallbackFactoryTest.DummyAlarmCallback));
        Assert.assertEquals(dummyAlarmCallback, alarmCallback);
    }

    @Test
    public void testCreateByClassName() throws Exception {
        String className = AlarmCallbackFactoryTest.DummyAlarmCallback.class.getCanonicalName();
        AlarmCallback alarmCallback = alarmCallbackFactory.create(className);
        Assert.assertNotNull(alarmCallback);
        Assert.assertTrue((alarmCallback instanceof AlarmCallbackFactoryTest.DummyAlarmCallback));
        Assert.assertEquals(dummyAlarmCallback, alarmCallback);
    }

    @Test
    public void testCreateByClass() throws Exception {
        AlarmCallback alarmCallback = alarmCallbackFactory.create(AlarmCallbackFactoryTest.DummyAlarmCallback.class);
        Assert.assertTrue((alarmCallback instanceof AlarmCallbackFactoryTest.DummyAlarmCallback));
        Assert.assertEquals(dummyAlarmCallback, alarmCallback);
    }
}

