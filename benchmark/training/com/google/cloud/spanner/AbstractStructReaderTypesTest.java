/**
 * Copyright 2017 Google LLC
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
package com.google.cloud.spanner;


import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AbstractStructReader} that cover all type combinations.
 */
@RunWith(Parameterized.class)
public class AbstractStructReaderTypesTest {
    private static class TestStructReader extends AbstractStructReader {
        @Override
        protected boolean getBooleanInternal(int columnIndex) {
            return false;
        }

        @Override
        protected long getLongInternal(int columnIndex) {
            return 0;
        }

        @Override
        protected double getDoubleInternal(int columnIndex) {
            return 0;
        }

        @Override
        protected String getStringInternal(int columnIndex) {
            return null;
        }

        @Override
        protected ByteArray getBytesInternal(int columnIndex) {
            return null;
        }

        @Override
        protected Timestamp getTimestampInternal(int columnIndex) {
            return null;
        }

        @Override
        protected Date getDateInternal(int columnIndex) {
            return null;
        }

        @Override
        protected boolean[] getBooleanArrayInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Boolean> getBooleanListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected long[] getLongArrayInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Long> getLongListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected double[] getDoubleArrayInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Double> getDoubleListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<String> getStringListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<ByteArray> getBytesListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Timestamp> getTimestampListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Date> getDateListInternal(int columnIndex) {
            return null;
        }

        @Override
        protected List<Struct> getStructListInternal(int columnIndex) {
            return null;
        }

        @Override
        public Type getType() {
            return null;
        }

        @Override
        public boolean isNull(int columnIndex) {
            return false;
        }
    }

    private static List<String> NON_VALUE_GETTERS = Arrays.asList("getType", "getColumnCount", "getColumnIndex", "getColumnType");

    /**
     * The type of the column being tested.
     */
    @Parameterized.Parameter(0)
    public Type type;

    /**
     * The name of the implementation method to be called in {@code AbstractStructReader}.
     */
    @Parameterized.Parameter(1)
    public String implMethodName;

    /**
     * The value that should be returned when {@code implMethodName} is called, and is expected from
     * {@code getterMethodName}.
     */
    @Parameterized.Parameter(2)
    public Object value;

    /**
     * The name of the public getter method to be called.
     */
    @Parameterized.Parameter(3)
    public String getterMethodName;

    @Parameterized.Parameter(4)
    @Nullable
    public List<String> otherAllowedGetters;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AbstractStructReaderTypesTest.TestStructReader reader;

    @Test
    public void getter() throws Exception {
        Mockito.when(reader.getType()).thenReturn(Type.struct(Type.StructField.of("F0", Type.int64()), Type.StructField.of("F1", type)));
        int columnIndex = 1;
        Mockito.when(reader.isNull(columnIndex)).thenReturn(false);
        Mockito.when(reader.getClass().getDeclaredMethod(implMethodName, int.class).invoke(reader, columnIndex)).thenReturn(value);
        assertThat(getterByIndex(columnIndex)).isEqualTo(value);
        assertThat(getterByName("F1")).isEqualTo(value);
    }

    @Test
    public void getterForIncorrectType() throws Exception {
        Mockito.when(reader.getType()).thenReturn(Type.struct(Type.StructField.of("F1", type)));
        int columnIndex = 0;
        Mockito.when(reader.isNull(columnIndex)).thenReturn(false);
        for (Method method : StructReader.class.getMethods()) {
            if (AbstractStructReaderTypesTest.NON_VALUE_GETTERS.contains(method.getName())) {
                continue;
            }
            if (((!(method.getName().startsWith("get"))) || ((method.getParameterTypes().length) != 1)) || ((method.getParameterTypes()[0]) != (int.class))) {
                // Skip non-column index getter methods.
                continue;
            }
            if ((method.getName().equals(getterMethodName)) || (((otherAllowedGetters) != null) && (otherAllowedGetters.contains(method.getName())))) {
                // Skip allowed getters.
                continue;
            }
            try {
                getterByIndex(method.getName(), columnIndex);
                Assert.fail(("Expected ISE for " + method));
            } catch (IllegalStateException e) {
                assertThat(e.getMessage()).named(("Exception for " + method)).contains(("was " + (type)));
                assertThat(e.getMessage()).named(("Exception for " + method)).contains(("Column " + columnIndex));
            }
            try {
                getterByName(method.getName(), "F1");
                Assert.fail(("Expected ISE for " + method));
            } catch (IllegalStateException e) {
                assertThat(e.getMessage()).named(("Exception for " + method)).contains(("was " + (type)));
                assertThat(e.getMessage()).named(("Exception for " + method)).contains("Column F1");
            }
        }
    }

    @Test
    public void getterWhenNull() throws Exception {
        Mockito.when(reader.getType()).thenReturn(Type.struct(Type.StructField.of("F1", type)));
        Mockito.when(reader.isNull(0)).thenReturn(true);
        expectedException.expect(NullPointerException.class);
        getterByIndex(0);
    }

    @Test
    public void getterByNameWhenNull() throws Exception {
        Mockito.when(reader.getType()).thenReturn(Type.struct(Type.StructField.of("F1", type)));
        Mockito.when(reader.isNull(0)).thenReturn(true);
        expectedException.expect(NullPointerException.class);
        getterByName("F1");
    }
}

