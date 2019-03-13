package com.vaadin.v7.tests.server.component.table;


import Table.ROW_HEADER_MODE_EXPLICIT;
import com.vaadin.v7.ui.Table;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;


public class TableSerializationTest {
    @Test
    public void testSerialization() {
        Table t = new Table();
        byte[] ser = SerializationUtils.serialize(t);
        Table t2 = ((Table) (SerializationUtils.deserialize(ser)));
    }

    @Test
    public void testSerializationWithRowHeaders() {
        Table t = new Table();
        t.setRowHeaderMode(ROW_HEADER_MODE_EXPLICIT);
        t.setColumnWidth(null, 100);
        byte[] ser = SerializationUtils.serialize(t);
        Table t2 = ((Table) (SerializationUtils.deserialize(ser)));
    }
}

