package com.mongodb.hadoop.hive.input;


import BSONSerDe.MONGO_COLS;
import ColumnProjectionUtils.READ_ALL_COLUMNS;
import ColumnProjectionUtils.READ_COLUMN_NAMES_CONF_STR;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.hadoop.hive.HiveTest;
import com.mongodb.util.JSON;
import java.util.Arrays;
import java.util.Map;
import org.apache.hadoop.hive.ql.index.IndexPredicateAnalyzer;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPAnd;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqual;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqualOrGreaterThan;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPGreaterThan;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPLessThan;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;


public class HiveMongoInputFormatTest extends HiveTest {
    private static HiveMongoInputFormat inputFormat;

    private static IndexPredicateAnalyzer analyzer;

    private static Map<String, String> colNameMapping;

    @Test
    public void testTranslateEqualsOp() {
        // WHERE i = 20
        GenericUDFOPEqual equal = new GenericUDFOPEqual();
        ExprNodeDesc[] children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("i")), new ExprNodeConstantDesc(20) };
        ExprNodeGenericFuncDesc expr = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, equal, Arrays.asList(children));
        Assert.assertEquals(new BasicDBObject("mongo_i", 20), filterForExpr(expr));
    }

    @Test
    public void testTranslateCompareOp() {
        // WHERE i >= 20
        GenericUDFOPEqualOrGreaterThan gte = new GenericUDFOPEqualOrGreaterThan();
        ExprNodeDesc[] children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("i")), new ExprNodeConstantDesc(20) };
        ExprNodeGenericFuncDesc expr = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, gte, Arrays.asList(children));
        Assert.assertEquals(new BasicDBObject("mongo_i", new BasicDBObject("$gte", 20)), filterForExpr(expr));
    }

    @Test
    public void testTranslateConjoinedQuery() {
        // i < 50
        GenericUDFOPLessThan lt = new GenericUDFOPLessThan();
        ExprNodeDesc[] iLt50Children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("i")), new ExprNodeConstantDesc(50) };
        ExprNodeGenericFuncDesc iLt50 = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, lt, Arrays.asList(iLt50Children));
        // j > 20
        GenericUDFOPGreaterThan gt = new GenericUDFOPGreaterThan();
        ExprNodeDesc[] jGt20Children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("j")), new ExprNodeConstantDesc(20) };
        ExprNodeGenericFuncDesc jGt20 = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, gt, Arrays.asList(jGt20Children));
        // i < 50 AND j > 20
        ExprNodeDesc[] andExprChildren = new ExprNodeDesc[]{ iLt50, jGt20 };
        ExprNodeGenericFuncDesc expr = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, new GenericUDFOPAnd(), Arrays.asList(andExprChildren));
        // {"$and": [{"i": {"$lt": 50}}, {"j": {"$gt": 20}}]}
        Assert.assertEquals(new BasicDBObjectBuilder().push("mongo_i").add("$lt", 50).pop().push("mongo_j").add("$gt", 20).pop().get(), filterForExpr(expr));
    }

    @Test
    public void testProjection() {
        String selectedColumns = "i,j";
        JobConf conf = new JobConf();
        conf.set(READ_COLUMN_NAMES_CONF_STR, selectedColumns);
        conf.setBoolean(READ_ALL_COLUMNS, false);
        Assert.assertEquals(new BasicDBObjectBuilder().add("i", 1).add("j", 1).add("_id", 0).get(), HiveMongoInputFormatTest.inputFormat.getProjection(conf, null));
    }

    @Test
    public void testProjectionWithColumnMapping() {
        DBObject mapping = new BasicDBObjectBuilder().add("i", "mongo_i").add("j", "mongo_j").add("id", "_id").get();
        String selectedColumns = "id,i";
        JobConf conf = new JobConf();
        conf.set(READ_COLUMN_NAMES_CONF_STR, selectedColumns);
        conf.set(MONGO_COLS, JSON.serialize(mapping));
        conf.setBoolean(READ_ALL_COLUMNS, false);
        // _id field is implicitly mapped to id field in Hive.
        Assert.assertEquals(new BasicDBObjectBuilder().add("mongo_i", 1).add("_id", 1).get(), HiveMongoInputFormatTest.inputFormat.getProjection(conf, HiveMongoInputFormatTest.colNameMapping));
    }
}

