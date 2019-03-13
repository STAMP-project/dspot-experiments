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
package com.google.cloud.logging;


import org.junit.Assert;
import org.junit.Test;


public class SourceLocationTest {
    private static final String FILE = "file";

    private static final Long LINE = 42L;

    private static final String FUNCTION = "function";

    private static final SourceLocation SOURCE_LOCATION = SourceLocation.newBuilder().setFile(SourceLocationTest.FILE).setLine(SourceLocationTest.LINE).setFunction(SourceLocationTest.FUNCTION).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(SourceLocationTest.FILE, SourceLocationTest.SOURCE_LOCATION.getFile());
        Assert.assertEquals(SourceLocationTest.LINE, SourceLocationTest.SOURCE_LOCATION.getLine());
        Assert.assertEquals(SourceLocationTest.FUNCTION, SourceLocationTest.SOURCE_LOCATION.getFunction());
    }

    @Test
    public void testToBuilder() {
        compareSourceLocation(SourceLocationTest.SOURCE_LOCATION, SourceLocationTest.SOURCE_LOCATION.toBuilder().build());
        SourceLocation sourceLocation = SourceLocationTest.SOURCE_LOCATION.toBuilder().setFile("newFile").setLine(43L).setFunction("newFunction").build();
        Assert.assertEquals("newFile", sourceLocation.getFile());
        Assert.assertEquals(Long.valueOf(43L), sourceLocation.getLine());
        Assert.assertEquals("newFunction", sourceLocation.getFunction());
        sourceLocation = sourceLocation.toBuilder().setFile(SourceLocationTest.FILE).setLine(SourceLocationTest.LINE).setFunction(SourceLocationTest.FUNCTION).build();
        compareSourceLocation(SourceLocationTest.SOURCE_LOCATION, sourceLocation);
    }

    @Test
    public void testToAndFromPb() {
        compareSourceLocation(SourceLocationTest.SOURCE_LOCATION, SourceLocation.fromPb(SourceLocationTest.SOURCE_LOCATION.toPb()));
    }
}

