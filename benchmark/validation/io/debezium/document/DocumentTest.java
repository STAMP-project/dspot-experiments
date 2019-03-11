/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.document;


import io.debezium.doc.FixFor;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class DocumentTest {
    private Document doc;

    private Map<Path, Value> found = new LinkedHashMap<>();

    private Iterator<Map.Entry<Path, Value>> iterator;

    @Test
    public void shouldPerformForEachOnFlatDocument() {
        doc = Document.create("a", "A", "b", "B");
        doc.forEach(( path, value) -> found.put(path, value));
        iterator = found.entrySet().iterator();
        assertPair(iterator, "/a", "A");
        assertPair(iterator, "/b", "B");
        assertNoMore(iterator);
    }

    @Test
    @FixFor("DBZ-759")
    public void shouldCreateArrayFromValues() {
        Document document = Document.create();
        document.setArray("my_field", 1, 2, 3);
        assertThat(document.toString()).isEqualTo(("{\n" + ("  \"my_field\" : [ 1, 2, 3 ]\n" + "}")));
    }
}

