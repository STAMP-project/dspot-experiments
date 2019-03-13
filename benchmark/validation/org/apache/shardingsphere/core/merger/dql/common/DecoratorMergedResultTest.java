/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.merger.dql.common;


import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.apache.shardingsphere.core.merger.dql.common.fixture.TestDecoratorMergedResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class DecoratorMergedResultTest {
    @Mock
    private MergedResult mergedResult;

    private TestDecoratorMergedResult decoratorMergedResult;

    @Test
    public void assertGetValueWithColumnIndex() throws SQLException {
        Mockito.when(mergedResult.getValue(1, Object.class)).thenReturn("1");
        Assert.assertThat(decoratorMergedResult.getValue(1, Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnLabel() throws SQLException {
        Mockito.when(mergedResult.getValue("label", Object.class)).thenReturn("1");
        Assert.assertThat(getValue("label", Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetCalenderValueWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergedResult.getCalendarValue(1, Date.class, calendar)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (decoratorMergedResult.getCalendarValue(1, Date.class, calendar))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetCalenderValueWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergedResult.getCalendarValue("label", Date.class, calendar)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (getCalendarValue("label", Date.class, calendar))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetInputStreamWithColumnIndex() throws SQLException {
        Mockito.when(mergedResult.getInputStream(1, "ascii")).thenReturn(null);
        Assert.assertNull(decoratorMergedResult.getInputStream(1, "ascii"));
    }

    @Test
    public void assertGetInputStreamWithColumnLabel() throws SQLException {
        Mockito.when(mergedResult.getInputStream("label", "ascii")).thenReturn(null);
        Assert.assertNull(getInputStream("label", "ascii"));
    }

    @Test
    public void assertWasNull() throws SQLException {
        Mockito.when(mergedResult.wasNull()).thenReturn(true);
        Assert.assertTrue(wasNull());
    }
}

