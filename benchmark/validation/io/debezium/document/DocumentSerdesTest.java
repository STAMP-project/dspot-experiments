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
public class DocumentSerdesTest implements Testing {
    private static final DocumentSerdes SERDES = new DocumentSerdes();

    @Test
    public void shouldConvertFromBytesToDocument1() throws IOException {
        readAsStringAndBytes("json/sample1.json");
    }

    @Test
    public void shouldUseSerdeMethodToConvertFromBytesToDocument2() throws IOException {
        readAsStringAndBytes("json/sample2.json");
    }

    @Test
    public void shouldUseSerdeMethodToConvertFromBytesToDocument3() throws IOException {
        readAsStringAndBytes("json/sample3.json");
    }

    @Test
    public void shouldUseSerdeMethodToConvertFromBytesToDocumentForResponse1() throws IOException {
        readAsStringAndBytes("json/response1.json");
    }

    @Test
    public void shouldUseSerdeMethodToConvertFromBytesToDocumentForResponse2() throws IOException {
        readAsStringAndBytes("json/response2.json");
    }
}

