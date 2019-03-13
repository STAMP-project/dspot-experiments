/**
 * Copyright (c) 2014, Cloudera, Inc. and Intel Corp. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als;


import MediaType.APPLICATION_JSON_TYPE;
import com.cloudera.oryx.common.OryxTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class PopularRepresentativeItemsTest extends AbstractALSServingTest {
    @Test
    public void testPopularRepresentativeItems() {
        List<String> items = target("/popularRepresentativeItems").request().accept(APPLICATION_JSON_TYPE).get(LIST_STRING_TYPE);
        Assert.assertEquals(2, items.size());
        // These are tied in the current model:
        OryxTest.assertContains(Arrays.asList("I0", "I3"), items.get(0));
        Assert.assertEquals("I4", items.get(1));
    }

    @Test
    public void testPopularRepresentativeItemsCSV() {
        String response = target("/popularRepresentativeItems").request().get(String.class);
        Assert.assertEquals(2, response.split("\n").length);
    }
}

