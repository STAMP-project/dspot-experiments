package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Test;


public class TestOrderByIndexPropDesc {
    private static final String DOCUMENT_CLASS_NAME = "MyDocument";

    private static final String PROP_INDEXED_STRING = "dateProperty";

    private ODatabaseDocument db;

    @Test
    public void worksFor1000() {
        test(1000);
    }

    @Test
    public void worksFor10000() {
        test(50000);
    }
}

