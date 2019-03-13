/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.functions;


import io.reactivex.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class ObjectHelperTest {
    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(ObjectHelper.class);
    }

    @Test
    public void hashCodeOf() {
        Assert.assertEquals(0, ObjectHelper.hashCode(null));
        Assert.assertEquals(((Integer) (1)).hashCode(), ObjectHelper.hashCode(1));
    }

    @Test
    public void verifyPositiveInt() throws Exception {
        Assert.assertEquals(1, ObjectHelper.verifyPositive(1, "param"));
    }

    @Test
    public void verifyPositiveLong() throws Exception {
        Assert.assertEquals(1L, ObjectHelper.verifyPositive(1L, "param"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyPositiveIntFail() throws Exception {
        Assert.assertEquals((-1), ObjectHelper.verifyPositive((-1), "param"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyPositiveLongFail() throws Exception {
        Assert.assertEquals((-1L), ObjectHelper.verifyPositive((-1L), "param"));
    }

    @Test
    public void compare() {
        Assert.assertEquals((-1), ObjectHelper.compare(0, 2));
        Assert.assertEquals(0, ObjectHelper.compare(0, 0));
        Assert.assertEquals(1, ObjectHelper.compare(2, 0));
    }

    @Test
    public void compareLong() {
        Assert.assertEquals((-1), ObjectHelper.compare(0L, 2L));
        Assert.assertEquals(0, ObjectHelper.compare(0L, 0L));
        Assert.assertEquals(1, ObjectHelper.compare(2L, 0L));
    }

    @SuppressWarnings("deprecation")
    @Test(expected = InternalError.class)
    public void requireNonNullPrimitive() {
        ObjectHelper.requireNonNull(0, "value");
    }
}

