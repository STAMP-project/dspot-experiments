/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.internal.debugging;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.exceptions.stacktrace.StackTraceFilter;
import org.mockitoutil.TestBase;


@SuppressWarnings("serial")
public class LocationImplTest extends TestBase {
    @Test
    public void shouldLocationNotContainGetStackTraceMethod() {
        assertThat(new LocationImpl().toString()).contains("shouldLocationNotContainGetStackTraceMethod");
    }

    @Test
    public void shouldBeSafeInCaseForSomeReasonFilteredStackTraceIsEmpty() {
        // given
        StackTraceFilter filterReturningEmptyArray = new StackTraceFilter() {
            @Override
            public StackTraceElement[] filter(StackTraceElement[] target, boolean keepTop) {
                return new StackTraceElement[0];
            }
        };
        // when
        String loc = new LocationImpl(filterReturningEmptyArray).toString();
        // then
        Assert.assertEquals("-> at <<unknown line>>", loc);
    }

    @Test
    public void provides_location_class() {
        // when
        final List<String> files = new ArrayList<String>();
        new Runnable() {
            // anonymous inner class adds stress to the check
            public void run() {
                files.add(getSourceFile());
            }
        }.run();
        // then
        Assert.assertEquals("LocationImplTest.java", files.get(0));
    }
}

