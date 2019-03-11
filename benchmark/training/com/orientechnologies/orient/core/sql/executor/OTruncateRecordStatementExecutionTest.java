package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OTruncateRecordStatementExecutionTest {
    static ODatabaseDocument database;

    @Test
    public void truncateRecord() {
        if (!(OTruncateRecordStatementExecutionTest.database.getMetadata().getSchema().existsClass("truncateRecord")))
            OTruncateRecordStatementExecutionTest.database.command("create class truncateRecord");

        OTruncateRecordStatementExecutionTest.database.command("insert into truncateRecord (sex, salary) values ('female', 2100)");
        final Long total = OTruncateRecordStatementExecutionTest.database.countClass("truncateRecord");
        final OResultSet resultset = OTruncateRecordStatementExecutionTest.database.query("select from truncateRecord where sex = 'female' and salary = 2100");
        OResultSet records = OTruncateRecordStatementExecutionTest.database.command((("truncate record [" + (resultset.next().getElement().get().getIdentity())) + "]"));
        resultset.close();
        int truncatedRecords = toList(records).size();
        Assert.assertEquals(truncatedRecords, 1);
        OClass cls = OTruncateRecordStatementExecutionTest.database.getMetadata().getSchema().getClass("truncateRecord");
        Set<OIndex<?>> indexes = cls.getIndexes();
        for (OIndex<?> index : indexes) {
            index.rebuild();
        }
        Assert.assertEquals(OTruncateRecordStatementExecutionTest.database.countClass("truncateRecord"), (total - truncatedRecords));
    }

    @Test
    public void truncateNonExistingRecord() {
        if (!(OTruncateRecordStatementExecutionTest.database.getMetadata().getSchema().existsClass("truncateNonExistingRecord")))
            OTruncateRecordStatementExecutionTest.database.command("create class truncateNonExistingRecord");

        OResultSet records = OTruncateRecordStatementExecutionTest.database.command((("truncate record [ #" + (OTruncateRecordStatementExecutionTest.database.getClusterIdByName("truncateNonExistingRecord"))) + ":99999999 ]"));
        Assert.assertEquals(toList(records).size(), 0);
    }
}

