/**
 * Copyright 2016 Google LLC
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


import org.junit.Assert;
import org.junit.Test;


public class BigQueryErrorTest {
    private static final String REASON = "reason";

    private static final String LOCATION = "location";

    private static final String DEBUG_INFO = "debugInfo";

    private static final String MESSAGE = "message";

    private static final BigQueryError ERROR = new BigQueryError(BigQueryErrorTest.REASON, BigQueryErrorTest.LOCATION, BigQueryErrorTest.MESSAGE, BigQueryErrorTest.DEBUG_INFO);

    private static final BigQueryError ERROR_INCOMPLETE = new BigQueryError(BigQueryErrorTest.REASON, BigQueryErrorTest.LOCATION, BigQueryErrorTest.MESSAGE);

    @Test
    public void testConstructor() {
        Assert.assertEquals(BigQueryErrorTest.REASON, BigQueryErrorTest.ERROR.getReason());
        Assert.assertEquals(BigQueryErrorTest.LOCATION, BigQueryErrorTest.ERROR.getLocation());
        Assert.assertEquals(BigQueryErrorTest.DEBUG_INFO, BigQueryErrorTest.ERROR.getDebugInfo());
        Assert.assertEquals(BigQueryErrorTest.MESSAGE, BigQueryErrorTest.ERROR.getMessage());
        Assert.assertEquals(BigQueryErrorTest.REASON, BigQueryErrorTest.ERROR_INCOMPLETE.getReason());
        Assert.assertEquals(BigQueryErrorTest.LOCATION, BigQueryErrorTest.ERROR_INCOMPLETE.getLocation());
        Assert.assertEquals(null, BigQueryErrorTest.ERROR_INCOMPLETE.getDebugInfo());
        Assert.assertEquals(BigQueryErrorTest.MESSAGE, BigQueryErrorTest.ERROR_INCOMPLETE.getMessage());
    }

    @Test
    public void testToAndFromPb() {
        compareBigQueryError(BigQueryErrorTest.ERROR, BigQueryError.fromPb(BigQueryErrorTest.ERROR.toPb()));
        compareBigQueryError(BigQueryErrorTest.ERROR_INCOMPLETE, BigQueryError.fromPb(BigQueryErrorTest.ERROR_INCOMPLETE.toPb()));
    }
}

