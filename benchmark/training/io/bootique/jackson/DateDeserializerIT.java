/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.jackson;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class DateDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialize() throws Exception {
        Date date = deserialize(Date.class, "\"1999-10-12T13:45:05\"");
        Assert.assertNotNull(date);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(date.getTime());
        Assert.assertEquals(1999, cal.get(Calendar.YEAR));
        Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(45, cal.get(Calendar.MINUTE));
        Assert.assertEquals(5, cal.get(Calendar.SECOND));
    }
}

