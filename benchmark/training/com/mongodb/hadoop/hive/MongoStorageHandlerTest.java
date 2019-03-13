package com.mongodb.hadoop.hive;


import HiveStoragePredicateHandler.DecomposedPredicate;
import java.util.Arrays;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.metadata.HiveStoragePredicateHandler;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBetween;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPAnd;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPGreaterThan;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.junit.Assert;
import org.junit.Test;
import serdeConstants.LIST_COLUMNS;
import serdeConstants.LIST_COLUMN_TYPES;


public class MongoStorageHandlerTest extends HiveTest {
    private static MongoStorageHandler msh = new MongoStorageHandler();

    @Test
    public void testDecomposePredicate() throws SerDeException {
        BSONSerDe serde = new BSONSerDe();
        Configuration conf = new Configuration();
        Properties tableProperties = new Properties();
        // Set table columns.
        tableProperties.setProperty(LIST_COLUMNS, "id,i,j");
        tableProperties.setProperty(LIST_COLUMN_TYPES, "string,int,int");
        serde.initialize(conf, tableProperties);
        // Build a query.
        // WHERE i > 20
        GenericUDFOPGreaterThan gt = new GenericUDFOPGreaterThan();
        ExprNodeDesc[] children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("i")), new ExprNodeConstantDesc(20) };
        ExprNodeGenericFuncDesc expr = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, gt, Arrays.asList(children));
        HiveStoragePredicateHandler.DecomposedPredicate decomposed = MongoStorageHandlerTest.msh.decomposePredicate(null, serde, expr);
        Assert.assertNull(decomposed.residualPredicate);
        Assert.assertEquals(expr.getExprString(), decomposed.pushedPredicate.getExprString());
    }

    @Test
    public void testDecomposePredicateUnrecognizedOps() throws SerDeException {
        BSONSerDe serde = new BSONSerDe();
        Configuration conf = new Configuration();
        Properties tableProperties = new Properties();
        // Set table columns.
        tableProperties.setProperty(LIST_COLUMNS, "id,i,j");
        tableProperties.setProperty(LIST_COLUMN_TYPES, "string,int,int");
        serde.initialize(conf, tableProperties);
        // Build a query.
        // WHERE i > 20 AND j IS BETWEEN 1 AND 10
        GenericUDFOPGreaterThan gt = new GenericUDFOPGreaterThan();
        ExprNodeDesc[] children = new ExprNodeDesc[]{ new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("i")), new ExprNodeConstantDesc(20) };
        ExprNodeGenericFuncDesc iGt20 = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, gt, Arrays.asList(children));
        GenericUDFBetween between = new GenericUDFBetween();
        ExprNodeDesc[] betweenChildren = new ExprNodeDesc[]{ new ExprNodeConstantDesc(false), new ExprNodeColumnDesc(new HiveTest.SimpleMockColumnInfo("j")), new ExprNodeConstantDesc(1), new ExprNodeConstantDesc(10) };
        ExprNodeGenericFuncDesc jBetween1And10 = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, between, Arrays.asList(betweenChildren));
        ExprNodeDesc[] exprChildren = new ExprNodeDesc[]{ iGt20, jBetween1And10 };
        ExprNodeGenericFuncDesc expr = new ExprNodeGenericFuncDesc(TypeInfoFactory.booleanTypeInfo, new GenericUDFOPAnd(), Arrays.asList(exprChildren));
        HiveStoragePredicateHandler.DecomposedPredicate decomposed = MongoStorageHandlerTest.msh.decomposePredicate(null, serde, expr);
        Assert.assertEquals(jBetween1And10.getExprString(), decomposed.residualPredicate.getExprString());
        Assert.assertEquals(iGt20.getExprString(), decomposed.pushedPredicate.getExprString());
    }
}

