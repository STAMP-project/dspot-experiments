/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.document;


import io.debezium.util.Testing;
import java.io.IOException;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class ArraySerdesTest implements Testing {
    private static final ArraySerdes SERDES = new ArraySerdes();

    @Test
    public void shouldConvertFromBytesToArray1() throws IOException {
        readAsStringAndBytes("json/array1.json");
    }

    @Test
    public void shouldConvertFromBytesToArray2() throws IOException {
        readAsStringAndBytes("json/array2.json");
    }
}

