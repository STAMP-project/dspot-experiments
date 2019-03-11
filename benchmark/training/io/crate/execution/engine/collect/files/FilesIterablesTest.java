/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.collect.files;


import com.google.common.collect.Iterators;
import java.util.Iterator;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class FilesIterablesTest {
    @Test
    public void testSqlFeatureIterable() throws Exception {
        SqlFeaturesIterable featuresIterable = new SqlFeaturesIterable();
        Iterator iterator = featuresIterable.iterator();
        Assert.assertTrue(iterator.hasNext());
        SqlFeatureContext context = ((SqlFeatureContext) (iterator.next()));
        Assert.assertEquals("B011", context.featureId);
        Assert.assertEquals("Embedded Ada", context.featureName);
        Assert.assertEquals("", context.subFeatureId);
        Assert.assertEquals("", context.subFeatureName);
        Assert.assertEquals(false, context.isSupported);
        Assert.assertNull(context.isVerifiedBy);
        Assert.assertNull(context.comments);
        Assert.assertEquals(671L, Iterators.size(iterator));
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSummitsIterable() throws Exception {
        SummitsIterable summitsIterable = new SummitsIterable();
        Iterator iterator = summitsIterable.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(Iterators.size(iterator), Is.is(1605));
        Assert.assertFalse(iterator.hasNext());
    }
}

