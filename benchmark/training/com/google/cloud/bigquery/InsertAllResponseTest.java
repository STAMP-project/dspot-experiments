/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class InsertAllResponseTest {
    private static final List<BigQueryError> ERRORS1 = ImmutableList.of(new BigQueryError("reason1", "location1", "message1"), new BigQueryError("reason2", "location2", "message2"));

    private static final List<BigQueryError> ERRORS2 = ImmutableList.of(new BigQueryError("reason3", "location3", "message3"), new BigQueryError("reason4", "location4", "message4"));

    private static final Map<Long, List<BigQueryError>> ERRORS_MAP = ImmutableMap.of(0L, InsertAllResponseTest.ERRORS1, 1L, InsertAllResponseTest.ERRORS2);

    private static final InsertAllResponse INSERT_ALL_RESPONSE = new InsertAllResponse(InsertAllResponseTest.ERRORS_MAP);

    private static final InsertAllResponse EMPTY_INSERT_ALL_RESPONSE = new InsertAllResponse(null);

    @Test
    public void testConstructor() {
        Assert.assertEquals(InsertAllResponseTest.INSERT_ALL_RESPONSE, InsertAllResponseTest.INSERT_ALL_RESPONSE);
    }

    @Test
    public void testErrorsFor() {
        Assert.assertEquals(InsertAllResponseTest.ERRORS_MAP, InsertAllResponseTest.INSERT_ALL_RESPONSE.getInsertErrors());
        Assert.assertEquals(InsertAllResponseTest.ERRORS1, InsertAllResponseTest.INSERT_ALL_RESPONSE.getErrorsFor(0L));
        Assert.assertEquals(InsertAllResponseTest.ERRORS2, InsertAllResponseTest.INSERT_ALL_RESPONSE.getErrorsFor(1L));
        Assert.assertNull(InsertAllResponseTest.INSERT_ALL_RESPONSE.getErrorsFor(2L));
    }

    @Test
    public void testHasErrors() {
        Assert.assertTrue(InsertAllResponseTest.INSERT_ALL_RESPONSE.hasErrors());
        Assert.assertFalse(InsertAllResponseTest.EMPTY_INSERT_ALL_RESPONSE.hasErrors());
    }

    @Test
    public void testToPbAndFromPb() {
        compareInsertAllResponse(InsertAllResponseTest.INSERT_ALL_RESPONSE, InsertAllResponse.fromPb(InsertAllResponseTest.INSERT_ALL_RESPONSE.toPb()));
        compareInsertAllResponse(InsertAllResponseTest.EMPTY_INSERT_ALL_RESPONSE, InsertAllResponse.fromPb(InsertAllResponseTest.EMPTY_INSERT_ALL_RESPONSE.toPb()));
    }
}

