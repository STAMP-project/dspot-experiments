/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.util;


import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Unit tests for {@link ArrayUtils}.
 *
 * @since GemFire 7.x
 */
@SuppressWarnings("null")
public class ArrayUtilsTest {
    @Rule
    public TestName testName = new TestName();

    @Test
    public void testGetElementAtIndex() {
        Object[] arrayOfThree = new Object[]{ "test", "testing", "tested" };
        assertThat(ArrayUtils.getElementAtIndex(arrayOfThree, 0, null)).isEqualTo("test");
        assertThat(ArrayUtils.getElementAtIndex(arrayOfThree, 1, null)).isEqualTo("testing");
        assertThat(ArrayUtils.getElementAtIndex(arrayOfThree, 2, null)).isEqualTo("tested");
    }

    @Test
    public void getElementAtIndex_emptyArray_returnsDefaultValue() {
        Object[] emptyArray = new Object[]{  };
        String defaultValue = this.testName.getMethodName();
        assertThat(ArrayUtils.getElementAtIndex(emptyArray, 0, defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    public void getElementAtIndex_emptyArray_returnsNullDefaultValue() {
        Object[] emptyArray = new Object[]{  };
        assertThat(ArrayUtils.getElementAtIndex(emptyArray, 0, null)).isNull();
    }

    @Test
    public void getElementAtIndex_indexOutOfBounds_returnsDefaultValue() {
        Object[] arrayOfOne = new Object[]{ "test" };
        String defaultValue = this.testName.getMethodName();
        assertThat(ArrayUtils.getElementAtIndex(arrayOfOne, 2, defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    public void getElementAtIndex_empty_indexOutOfBounds_returnsDefaultValue() {
        Object[] emptyArray = new Object[]{  };
        String defaultValue = this.testName.getMethodName();
        assertThat(ArrayUtils.getElementAtIndex(emptyArray, 2, defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    public void getFirst_array_returnsFirstElement() {
        assertThat(ArrayUtils.getFirst("first", "second", "third")).isEqualTo("first");
    }

    @Test
    public void getFirst_arrayContainingNull_returnsFirstElement() {
        assertThat(ArrayUtils.getFirst("null", "nil", null)).isEqualTo("null");
    }

    @Test
    public void getFirst_oneElement_returnsFirstElement() {
        assertThat(ArrayUtils.getFirst("test")).isEqualTo("test");
    }

    @Test
    public void getFirst_null_returnsNull() {
        Object nullObject = null;
        assertThat(ArrayUtils.getFirst(nullObject)).isNull();
    }

    @Test
    public void getFirst_empty_returnsNull() {
        Object[] emptyArray = new Object[0];
        assertThat(((Object[]) (ArrayUtils.getFirst(emptyArray)))).isNull();
    }

    @Test
    public void getFirst_arrayOfNullValues_returnsNull() {
        assertThat(((Object) (ArrayUtils.getFirst(null, null, null)))).isNull();
    }

    @Test
    public void toString_returnsOrderedStringInBrackets() {
        Object[] arrayOfThree = new Object[]{ "test", "testing", "tested" };
        assertThat(ArrayUtils.toString(arrayOfThree)).isEqualTo("[test, testing, tested]");
    }

    @Test
    public void toString_empty_returnsEmptyBrackets() {
        assertThat(ArrayUtils.toString(new Object[0])).isEqualTo("[]");
    }

    @Test
    public void toString_null_returnsEmptyBrackets() {
        assertThat(ArrayUtils.toString(((Object[]) (null)))).isEqualTo("[]");
    }

    @Test
    public void toIntegerArray_returnsOrderedArray() {
        int[] sequence = new int[]{ 0, 1, 2, 4, 8 };
        assertThat(ArrayUtils.toIntegerArray(sequence)).isNotNull().hasSize(sequence.length).containsExactly(0, 1, 2, 4, 8);
    }

    @Test
    public void toIntegerArray_empty_returnsEmptyArray() {
        assertThat(ArrayUtils.toIntegerArray(new int[]{  })).isNotNull().hasSize(0);
    }

    @Test
    public void toIntegerArray_null_returnsEmptyArray() {
        assertThat(ArrayUtils.toIntegerArray(null)).isNotNull().hasSize(0);
    }

    @Test
    public void toByteArray_returnsBytes() {
        int count = 0;
        byte[][] array = new byte[10][5];
        for (int i = 0; i < (array.length); i++) {
            for (int j = 0; j < (array[i].length); j++) {
                array[i][j] = ((byte) (++count));
            }
        }
        assertThat(count).isEqualTo(50);
        count = 0;
        Byte[][] byteArray = ArrayUtils.toByteArray(array);
        for (int i = 0; i < (byteArray.length); i++) {
            for (int j = 0; j < (byteArray[i].length); j++) {
                assertThat(byteArray[i][j].byteValue()).isEqualTo(((byte) (++count)));
            }
        }
        assertThat(count).isEqualTo(50);
    }

    @Test
    public void toBytes_returnsPrimitiveBytes() {
        int count = 100;
        Byte[][] byteArray = new Byte[5][10];
        for (int i = 0; i < (byteArray.length); i++) {
            for (int j = 0; j < (byteArray[i].length); j++) {
                byteArray[i][j] = ((byte) (--count));
            }
        }
        assertThat(count).isEqualTo(50);
        count = 100;
        byte[][] array = ArrayUtils.toBytes(byteArray);
        for (int i = 0; i < (array.length); i++) {
            for (int j = 0; j < (array[i].length); j++) {
                assertThat(array[i][j]).isEqualTo(((byte) (--count)));
            }
        }
        assertThat(count).isEqualTo(50);
    }

    @Test
    public void toByteArray_empty_returnsEmptyBytes() {
        byte[][] array = new byte[0][0];
        assertThat(ArrayUtils.toByteArray(array)).isEqualTo(new Byte[0][0]);
    }

    @Test
    public void toByteArray_null_returnsNull() {
        byte[][] array = null;
        assertThat(ArrayUtils.toByteArray(array)).isNull();
    }

    @Test
    public void toBytes_empty_returnsEmpty() {
        Byte[][] byteArray = new Byte[0][0];
        assertThat(ArrayUtils.toBytes(byteArray)).isEqualTo(new byte[0][0]);
    }

    @Test
    public void toBytes_null_returnsNull() {
        Byte[][] byteArray = null;
        assertThat(ArrayUtils.toBytes(byteArray)).isNull();
    }

    @Test
    public void asList_returnsModifiableList() throws Exception {
        List<String> modifiable = ArrayUtils.asList("Larry", "Moe", "Curly");
        assertThat(modifiable.remove("Curly")).isTrue();
        assertThat(modifiable).contains("Larry", "Moe").doesNotContain("Curly");
    }
}

