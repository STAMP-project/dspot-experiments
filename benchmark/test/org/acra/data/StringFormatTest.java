/**
 * Copyright (c) 2017 the ACRA team
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
package org.acra.data;


import StringFormat.JSON;
import StringFormat.KEY_VALUE_LIST;
import com.google.common.net.MediaType;
import junit.framework.Assert;
import org.acra.ReportField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 *
 *
 * @author F43nd1r
 * @since 29.11.2017
 */
@RunWith(RobolectricTestRunner.class)
public class StringFormatTest {
    private CrashReportData reportData;

    @Test
    public void toFormattedString() throws Exception {
        testJson();
        testKeyValue();
    }

    @Test
    public void getMatchingHttpContentType() {
        Assert.assertEquals((((MediaType.JSON_UTF_8.type()) + "/") + (MediaType.JSON_UTF_8.subtype())), JSON.getMatchingHttpContentType());
        Assert.assertEquals((((MediaType.FORM_DATA.type()) + "/") + (MediaType.FORM_DATA.subtype())), KEY_VALUE_LIST.getMatchingHttpContentType());
    }

    @Test
    public void issue626() throws Exception {
        CrashReportData reportData = new CrashReportData();
        Assert.assertEquals("DEVICE_ID=null", KEY_VALUE_LIST.toFormattedString(reportData, new org.acra.collections.ImmutableSet(ReportField.DEVICE_ID), "\n", " ", true));
    }
}

