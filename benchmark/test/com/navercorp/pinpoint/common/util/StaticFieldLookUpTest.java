/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.util;


import DefaultDisplayArgument.UNKNOWN_DB_MATCHER;
import ServiceType.UNDEFINED;
import ServiceType.UNKNOWN_DB;
import com.navercorp.pinpoint.common.trace.AnnotationKeyMatchers;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StaticFieldLookUpTest {
    @Test
    public void testFindServiceType() throws IllegalAccessException {
        StaticFieldLookUp<ServiceType> staticFieldLookUp = new StaticFieldLookUp<ServiceType>(ServiceType.class, ServiceType.class);
        List<ServiceType> lookup = staticFieldLookUp.lookup();
        Assert.assertTrue(StaticFieldLookUpTest.findType(lookup, UNKNOWN_DB));
        Assert.assertTrue(StaticFieldLookUpTest.findType(lookup, UNDEFINED));
    }

    @Test
    public void testNotFindServiceType() throws IllegalAccessException {
        StaticFieldLookUp<ServiceType> staticFieldLookUp = new StaticFieldLookUp<ServiceType>(ServiceType.class, ServiceType.class);
        List<ServiceType> lookup = staticFieldLookUp.lookup();
        final int SERVER_CATEGORY_MAX = 1999;
        ServiceType notExist = ServiceTypeFactory.of(SERVER_CATEGORY_MAX, "test", "test");
        Assert.assertFalse(StaticFieldLookUpTest.findType(lookup, notExist));
    }

    @Test
    public void testFindDisplayArgumentMatcher() throws IllegalAccessException {
        StaticFieldLookUp<DisplayArgumentMatcher> staticFieldLookUp = new StaticFieldLookUp<DisplayArgumentMatcher>(DefaultDisplayArgument.class, DisplayArgumentMatcher.class);
        List<DisplayArgumentMatcher> lookup = staticFieldLookUp.lookup();
        Assert.assertTrue(StaticFieldLookUpTest.findType(lookup, UNKNOWN_DB_MATCHER));
    }

    @Test
    public void testNotFindDisplayArgumentMatcher() throws IllegalAccessException {
        StaticFieldLookUp<DisplayArgumentMatcher> staticFieldLookUp = new StaticFieldLookUp<DisplayArgumentMatcher>(DefaultDisplayArgument.class, DisplayArgumentMatcher.class);
        List<DisplayArgumentMatcher> lookup = staticFieldLookUp.lookup();
        DisplayArgumentMatcher notExist = new DisplayArgumentMatcher(ServiceType.UNDEFINED, AnnotationKeyMatchers.NOTHING_MATCHER);
        Assert.assertFalse(StaticFieldLookUpTest.findType(lookup, notExist));
    }
}

