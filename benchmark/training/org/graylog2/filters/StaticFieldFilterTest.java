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
package org.graylog2.filters;


import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import java.util.Collections;
import java.util.concurrent.Executors;
import org.graylog2.inputs.Input;
import org.graylog2.inputs.InputService;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.graylog2.shared.SuppressForbidden;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class StaticFieldFilterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private InputService inputService;

    @Mock
    private Input input;

    @Test
    @SuppressForbidden("Executors#newSingleThreadExecutor() is okay for tests")
    public void testFilter() throws Exception {
        Message msg = new Message("hello", "junit", Tools.nowUTC());
        msg.setSourceInputId("someid");
        Mockito.when(input.getId()).thenReturn("someid");
        Mockito.when(inputService.all()).thenReturn(Collections.singletonList(input));
        Mockito.when(inputService.find(ArgumentMatchers.eq("someid"))).thenReturn(input);
        Mockito.when(inputService.getStaticFields(ArgumentMatchers.eq(input))).thenReturn(Collections.singletonList(Maps.immutableEntry("foo", "bar")));
        final StaticFieldFilter filter = new StaticFieldFilter(inputService, new EventBus(), Executors.newSingleThreadScheduledExecutor());
        filter.filter(msg);
        Assert.assertEquals("hello", msg.getMessage());
        Assert.assertEquals("junit", msg.getSource());
        Assert.assertEquals("bar", msg.getField("foo"));
    }

    @Test
    @SuppressForbidden("Executors#newSingleThreadExecutor() is okay for tests")
    public void testFilterIsNotOverwritingExistingKeys() throws Exception {
        Message msg = new Message("hello", "junit", Tools.nowUTC());
        msg.addField("foo", "IWILLSURVIVE");
        final StaticFieldFilter filter = new StaticFieldFilter(inputService, new EventBus(), Executors.newSingleThreadScheduledExecutor());
        filter.filter(msg);
        Assert.assertEquals("hello", msg.getMessage());
        Assert.assertEquals("junit", msg.getSource());
        Assert.assertEquals("IWILLSURVIVE", msg.getField("foo"));
    }
}

