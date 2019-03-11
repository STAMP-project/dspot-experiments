/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.apache.druid.collections.bitmap;


import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class RoaringBitmapFactoryTest {
    // testing https://github.com/metamx/bytebuffer-collections/issues/26
    @Test
    public void testIssue26() {
        checkEmptyComplement(new ConciseBitmapFactory());
        checkEmptyComplement(new RoaringBitmapFactory());
    }

    @Test
    public void testUnwrapWithNull() {
        RoaringBitmapFactory factory = new RoaringBitmapFactory();
        ImmutableBitmap bitmap = factory.union(Iterables.transform(Collections.singletonList(new WrappedRoaringBitmap()), new Function<WrappedRoaringBitmap, ImmutableBitmap>() {
            @Override
            public ImmutableBitmap apply(WrappedRoaringBitmap input) {
                return null;
            }
        }));
        Assert.assertEquals(0, bitmap.size());
    }

    @Test
    public void testUnwrapMerge() {
        RoaringBitmapFactory factory = new RoaringBitmapFactory();
        WrappedRoaringBitmap set = new WrappedRoaringBitmap();
        set.add(1);
        set.add(3);
        set.add(5);
        ImmutableBitmap bitmap = factory.union(Arrays.asList(factory.makeImmutableBitmap(set), null));
        Assert.assertEquals(3, bitmap.size());
    }
}

