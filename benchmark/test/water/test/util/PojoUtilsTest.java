package water.test.util;


import Frame.VecSpecifier;
import FrameV3.ColSpecifierV3;
import GBMModel.GBMParameters;
import PojoUtils.FieldNaming.CONSISTENT;
import hex.tree.gbm.GBMModel;
import org.junit.Assert;
import org.junit.Test;
import water.Iced;
import water.TestUtil;
import water.api.SchemaServer;
import water.api.schemas3.FrameV3;
import water.api.schemas3.SchemaV3;
import water.fvec.Frame;
import water.util.PojoUtils;


public class PojoUtilsTest extends TestUtil {
    @Test
    public void testGetFieldValue() {
        GBMModel.GBMParameters o = new GBMModel.GBMParameters();
        Assert.assertEquals(50, PojoUtils.getFieldValue(o, "_ntrees", CONSISTENT));
    }

    public static class TestIcedPojo extends Iced {
        VecSpecifier column;

        VecSpecifier[] column_array;
    }

    public static class TestSchemaPojo extends SchemaV3<PojoUtilsTest.TestIcedPojo, PojoUtilsTest.TestSchemaPojo> {
        ColSpecifierV3 column;

        ColSpecifierV3[] column_array;
    }

    @Test
    public void testVecSpecifierToColSpecifier() {
        SchemaServer.registerAllSchemasIfNecessary();
        PojoUtilsTest.TestIcedPojo fromIced = new PojoUtilsTest.TestIcedPojo();
        PojoUtilsTest.TestSchemaPojo toSchema = new PojoUtilsTest.TestSchemaPojo();
        fromIced.column = new Frame.VecSpecifier();
        fromIced.column._column_name = "see one";
        fromIced.column_array = new Frame.VecSpecifier[2];
        fromIced.column_array[0] = new Frame.VecSpecifier();
        fromIced.column_array[1] = new Frame.VecSpecifier();
        fromIced.column_array[0]._column_name = "C1";
        fromIced.column_array[1]._column_name = "C2";
        PojoUtils.copyProperties(toSchema, fromIced, CONSISTENT);
        Assert.assertEquals(fromIced.column._column_name.toString(), toSchema.column.column_name);
        for (int i = 0; i < (fromIced.column_array.length); i++)
            Assert.assertEquals(fromIced.column_array[i]._column_name.toString(), toSchema.column_array[i].column_name);

    }

    @Test
    public void testColSpecifierToVecSpecifier() {
        SchemaServer.registerAllSchemasIfNecessary();
        PojoUtilsTest.TestSchemaPojo fromSchema = new PojoUtilsTest.TestSchemaPojo();
        PojoUtilsTest.TestIcedPojo toIced = new PojoUtilsTest.TestIcedPojo();
        fromSchema.column = new FrameV3.ColSpecifierV3();
        fromSchema.column.column_name = "see one";
        fromSchema.column_array = new FrameV3.ColSpecifierV3[2];
        fromSchema.column_array[0] = new FrameV3.ColSpecifierV3();
        fromSchema.column_array[1] = new FrameV3.ColSpecifierV3();
        fromSchema.column_array[0].column_name = "C1";
        fromSchema.column_array[1].column_name = "C2";
        PojoUtils.copyProperties(toIced, fromSchema, CONSISTENT);
        Assert.assertEquals(fromSchema.column.column_name, toIced.column._column_name.toString());
        for (int i = 0; i < (fromSchema.column_array.length); i++)
            Assert.assertEquals(fromSchema.column_array[i].column_name, toIced.column_array[i]._column_name.toString());

    }

    public static class TestNestedFillFromJson extends Iced {
        public int meaning = 42;

        public GBMParameters parameters = null;

        public double double_meaning = 84.0;
    }

    @Test
    public void TestFillFromJson() {
        // Fill only one level, scalar:
        GBMModel.GBMParameters o = new GBMModel.GBMParameters();
        Assert.assertEquals(50, o._ntrees);
        Assert.assertEquals(5, o._max_depth);
        PojoUtils.fillFromJson(o, "{\"_ntrees\": 17}");
        Assert.assertEquals(17, o._ntrees);
        Assert.assertEquals(5, o._max_depth);
        // Fill with a nested object:
        PojoUtilsTest.TestNestedFillFromJson nested = new PojoUtilsTest.TestNestedFillFromJson();
        nested.parameters = new GBMModel.GBMParameters();
        Assert.assertEquals(50, nested.parameters._ntrees);
        Assert.assertEquals(5, nested.parameters._max_depth);
        PojoUtils.fillFromJson(nested, "{\"double_meaning\": 96, \"parameters\": {\"_ntrees\": 17}}");
        Assert.assertEquals(96, nested.double_meaning, 1.0E-5);
        Assert.assertEquals(42, nested.meaning);
        Assert.assertEquals(17, nested.parameters._ntrees);
        Assert.assertEquals(5, nested.parameters._max_depth);
    }
}

