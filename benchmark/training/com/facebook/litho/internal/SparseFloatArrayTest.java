/**
 * Copyright 2019-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.internal;


import org.junit.Test;


public class SparseFloatArrayTest {
    @Test
    public void test_get() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        result = array.get(1);
        assertThat(result).isEqualTo(0.0F);
        result = array.get(1, 1.0F);
        assertThat(result).isEqualTo(1.0F);
        result = array.get(1, 1.0F);
        assertThat(result).isEqualTo(1.0F);
    }

    @Test
    public void test_append() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        array.append(4, 1.0F);
        result = array.get(4);
        assertThat(result).isEqualTo(1.0F);
        // override
        array.append(4, 2.0F);
        result = array.get(4);
        assertThat(result).isEqualTo(2.0F);
        // do not use default value if found
        array.append(6, 1.0F);
        result = array.get(6, (-1.0F));
        assertThat(result).isEqualTo(1.0F);
        // use default value if NOT found
        array.append(1, 1.0F);
        array.append(2, 1.0F);
        array.append(3, 1.0F);
        array.append(5, 1.0F);
        array.append(6, 1.0F);
        array.append(8, 1.0F);
        result = array.get(7, (-1.0F));
        assertThat(result).isEqualTo((-1.0F));
    }

    @Test
    public void test_insert() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        array.put(4, 1.0F);
        result = array.get(4);
        assertThat(result).isEqualTo(1.0F);
        // override
        array.put(4, 2.0F);
        result = array.get(4);
        assertThat(result).isEqualTo(2.0F);
        // do not use default value if found
        array.put(6, 1.0F);
        result = array.get(6, (-1.0F));
        assertThat(result).isEqualTo(1.0F);
        // use default value if NOT found
        array.put(1, 1.0F);
        array.put(2, 1.0F);
        array.put(3, 1.0F);
        array.put(5, 1.0F);
        array.put(6, 1.0F);
        array.put(8, 1.0F);
        result = array.get(7, (-1.0F));
        assertThat(result).isEqualTo((-1.0F));
    }

    @Test
    public void test_delete() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        array.put(1, 1.0F);
        array.put(2, 1.0F);
        array.put(3, 1.0F);
        array.put(4, 1.0F);
        array.put(5, 1.0F);
        array.delete(2);
        result = array.get(2, (-1.0F));
        assertThat(result).isEqualTo((-1.0F));
        array.put(2, 1.0F);
        result = array.get(2, (-1.0F));
        assertThat(result).isEqualTo(1.0F);
    }

    @Test
    public void test_removeAt() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        array.put(1, 5.0F);
        array.put(2, 4.0F);
        array.put(3, 3.0F);
        array.put(4, 2.0F);
        array.put(5, 1.0F);
        result = array.get(2, (-1.0F));
        assertThat(result).isEqualTo(4.0F);
        array.removeAt(1);
        result = array.get(2, (-1.0F));
        assertThat(result).isEqualTo((-1.0F));
    }

    @Test
    public void test_keyAt_and_valueAt() {
        SparseFloatArray array = new SparseFloatArray();
        float value;
        int key;
        array.put(1, 5.0F);
        array.put(2, 4.0F);
        array.put(3, 3.0F);
        array.put(4, 2.0F);
        array.put(5, 1.0F);
        key = array.keyAt(0);
        assertThat(key).isEqualTo(1);
        value = array.valueAt(0);
        assertThat(value).isEqualTo(5.0F);
        key = array.keyAt(4);
        assertThat(key).isEqualTo(5);
        value = array.valueAt(4);
        assertThat(value).isEqualTo(1.0F);
    }

    @Test
    public void test_setValueAt() {
        SparseFloatArray array = new SparseFloatArray();
        float result;
        array.put(1, 1.0F);
        array.put(2, 2.0F);
        array.put(3, 3.0F);
        array.put(4, 4.0F);
        array.put(5, 5.0F);
        array.setValueAt(2, 0.0F);
        result = array.get(3, (-1.0F));
        assertThat(result).isEqualTo(0.0F);
        assertThat(array.valueAt(2)).isEqualTo(0.0F);
    }

    @Test
    public void test_indexOfKey() {
        SparseFloatArray array = new SparseFloatArray();
        int index;
        array.put(5, 1.0F);
        array.put(4, 2.0F);
        array.put(3, 3.0F);
        array.put(2, 4.0F);
        array.put(1, 5.0F);
        index = array.indexOfKey(1);
        assertThat(index).isEqualTo(0);
        index = array.indexOfKey(5);
        assertThat(index).isEqualTo(4);
        index = array.indexOfKey(3);
        assertThat(index).isEqualTo(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_out_of_bounds_check_keyAt() {
        SparseFloatArray array = new SparseFloatArray();
        array.put(1, 5.0F);
        array.put(2, 4.0F);
        array.put(3, 3.0F);
        array.put(4, 2.0F);
        array.put(5, 1.0F);
        array.keyAt(8);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_out_of_bounds_check_valueAt() {
        SparseFloatArray array = new SparseFloatArray();
        array.put(1, 5.0F);
        array.put(2, 4.0F);
        array.put(3, 3.0F);
        array.put(4, 2.0F);
        array.put(5, 1.0F);
        array.valueAt(8);
    }

    @Test
    public void test_growth_upto_growth() {
        SparseFloatArray array = new SparseFloatArray();
        array.put(1, 1.0F);
        assertThat(array.valueAt(0)).isEqualTo(1.0F);
        assertThat(array.valueAt(1)).isEqualTo(0.0F);
        array.put(2, 1.0F);
        assertThat(array.valueAt(1)).isEqualTo(1.0F);
        array.put(3, 1.0F);
        assertThat(array.valueAt(2)).isEqualTo(1.0F);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_growth_at_limit() {
        SparseFloatArray array = new SparseFloatArray();
        array.put(1, 1.0F);
        assertThat(array.valueAt(0)).isEqualTo(1.0F);
        // next should be zero
        assertThat(array.valueAt(1)).isEqualTo(0.0F);
        array.put(2, 1.0F);
        assertThat(array.valueAt(1)).isEqualTo(1.0F);
        // next should throw IndexOutOfBoundsException
        array.valueAt(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_growth_greater_than() {
        SparseFloatArray array = new SparseFloatArray();
        array.put(1, 1.0F);
        array.put(2, 1.0F);
        array.put(3, 1.0F);
        array.put(4, 1.0F);
        assertThat(array.valueAt(0)).isEqualTo(1.0F);
        assertThat(array.valueAt(1)).isEqualTo(1.0F);
        assertThat(array.valueAt(2)).isEqualTo(1.0F);
        assertThat(array.valueAt(3)).isEqualTo(1.0F);
        array.put(5, 1.0F);
        assertThat(array.valueAt(4)).isEqualTo(1.0F);
        // rest should be zero
        assertThat(array.valueAt(5)).isEqualTo(0.0F);
        assertThat(array.valueAt(6)).isEqualTo(0.0F);
        assertThat(array.valueAt(7)).isEqualTo(0.0F);
        // next should throw IndexOutOfBoundsException
        array.valueAt(8);
    }
}

