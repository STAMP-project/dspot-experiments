package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DateBinaryComparatorTest {
    private final String dateFormat = "yyyy-MM-dd";

    private final String dbUrl = "memory:DateBinaryComparatorTest";

    private final String dateValue = "2017-07-18";

    private ODatabaseDocument db;

    @Test
    public void testDateJavaClassPreparedStatement() throws ParseException {
        String str = "SELECT FROM Test WHERE date = :dateParam";
        OSQLSynchQuery query = new OSQLSynchQuery(str);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dateParam", new SimpleDateFormat(dateFormat).parse(dateValue));
        List<?> result = db.query(query, params);
        Assert.assertTrue(((result.size()) == 1));
    }
}

