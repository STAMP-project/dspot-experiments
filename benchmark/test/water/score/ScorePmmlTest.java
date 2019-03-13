package water.score;


import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class ScorePmmlTest extends TestUtil {
    private static final String HEADER = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n" + ((((("<PMML version=\'4.1\' xmlns=\'http://www.dmg.org/PMML-4_1\' xmlns:xsi=\'http://www.w3.org/2001/XMLSchema-instance\'>\n" + "  <Header copyright=\'0xData Testing\' description=\'0xData testing Model\'>\n") + "    <Application name=\'H2O\' version=\'0.1\'/>\n") + "    <Timestamp>2013-01-11T15:52:10.8</Timestamp>\n") + "  </Header>\n") + "");

    private static final String FOOTER = "</PMML>";

    enum DataType {

        DOUBLE("continuous"),
        INT("continuous"),
        BOOLEAN("categorical"),
        STRING("categorical");
        private final String opType;

        private DataType(String op) {
            opType = op;
        }

        public String getOpType() {
            return opType;
        }

        public String getDataType() {
            return toString().toLowerCase();
        }
    }

    enum UsageType {

        PREDICTED,
        ACTIVE;
        public String getUsageType() {
            return toString().toLowerCase();
        }
    }

    enum SimpleOp {

        EQ("equal", true),
        LE("lessOrEqual", true),
        LT("lessThan", true),
        GE("greaterOrEqual", true),
        GT("greaterThan", true),
        MISSING("isMissing", false);
        private final String op;

        private final boolean hasArg;

        private SimpleOp(String op, boolean hasArg) {
            this.op = op;
            this.hasArg = hasArg;
        }

        public String getOp() {
            return op;
        }

        public boolean hasArg() {
            return hasArg;
        }
    }

    enum SimpleSetOp {

        IN("isIn"),
        NOT_IN("isNotIn");
        private final String op;

        private SimpleSetOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }
    }

    enum CompoundOp {

        AND("and"),
        OR("or");
        private final String op;

        private CompoundOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }
    }

    static final String simplePmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("y", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active"), ScorePmmlTest.makeMiningField("y", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.LT, 0.0)), ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.GE, 0.0))), ScorePmmlTest.makeCharacteristic("y_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSimplePredicate("y", ScorePmmlTest.SimpleOp.LE, 0.0)), ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSimplePredicate("y", ScorePmmlTest.SimpleOp.GT, 0.0)))));

    @Test
    public void testBasic() throws Exception {
        String pmml = ScorePmmlTest.simplePmml;
        double[][] tests = new double[][]{ new double[]{ -1.0, -1.0, 2.0 }, new double[]{ -1.0, 1.0, 3.0 }, new double[]{ 1.0, -1.0, 3.0 }, new double[]{ 1.0, 1.0, 4.0 }, new double[]{ 0.0, 0.0, 3.0 } };
        for (double[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", t[0]);
            m.put("y", t[1]);
            ScorecardModel scm = getSCM(pmml);
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(t[2], predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testComparators() throws Exception {
        Object[][] tests = new Object[][]{ new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LT, 0.0, -1.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LT, 0.0, 0.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LT, 0.0, 1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LT, 0L, -1L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LT, 0L, 0L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LT, 0L, 1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GT, 0.0, -1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GT, 0.0, 0.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GT, 0.0, 1.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GT, 0L, -1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GT, 0L, 0L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GT, 0L, 1L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LE, 0.0, -1.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LE, 0.0, 0.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.LE, 0.0, 1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LE, 0L, -1L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LE, 0L, 0L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.LE, 0L, 1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GE, 0.0, -1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GE, 0.0, 0.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.GE, 0.0, 1.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GE, 0L, -1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GE, 0L, 0L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.GE, 0L, 1L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.EQ, 0.0, -1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.EQ, 0.0, 0.0, 1.0 }, new Object[]{ ScorePmmlTest.DataType.DOUBLE, ScorePmmlTest.SimpleOp.EQ, 0.0, 1.0, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.EQ, 0L, -1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.EQ, 0L, 0L, 1.0 }, new Object[]{ ScorePmmlTest.DataType.INT, ScorePmmlTest.SimpleOp.EQ, 0L, 1L, 0.0 }, new Object[]{ ScorePmmlTest.DataType.STRING, ScorePmmlTest.SimpleOp.EQ, "a", "a", 1.0 }, new Object[]{ ScorePmmlTest.DataType.STRING, ScorePmmlTest.SimpleOp.EQ, "a", "b", 0.0 }, new Object[]{ ScorePmmlTest.DataType.STRING, ScorePmmlTest.SimpleOp.EQ, "a", null, 0.0 }, new Object[]{ ScorePmmlTest.DataType.BOOLEAN, ScorePmmlTest.SimpleOp.EQ, true, true, 1.0 }, new Object[]{ ScorePmmlTest.DataType.BOOLEAN, ScorePmmlTest.SimpleOp.EQ, true, false, 0.0 } };
        for (Object[] t : tests) {
            String pmml = ScorePmmlTest.makeBasic(((ScorePmmlTest.DataType) (t[0])), ((ScorePmmlTest.SimpleOp) (t[1])), t[2]);
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", ((Comparable) (t[3])));
            ScorecardModel scm = getSCM(pmml);
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(((Double) (t[4])), predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testMissing() throws Exception {
        String pmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("y", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active"), ScorePmmlTest.makeMiningField("y", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.MISSING, 0.0))), ScorePmmlTest.makeCharacteristic("y_check", ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSimplePredicate("y", ScorePmmlTest.SimpleOp.MISSING, 0.0)))));
        Double[][] tests = new Double[][]{ new Double[]{ 1.0, null, 2.0 }, new Double[]{ -1.0, 1.0, 0.0 }, new Double[]{ null, -1.0, 1.0 }, new Double[]{ null, null, 3.0 } };
        for (Double[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", t[0]);
            m.put("y", t[1]);
            ScorecardModel scm = getSCM(pmml);
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(t[2], predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testCompound() throws Exception {
        String pmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeCompoundPredicate(ScorePmmlTest.CompoundOp.AND, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.GT, 0.0), ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.LT, 1.0))))));
        double[][] tests = new double[][]{ new double[]{ 1.0, 0.0 }, new double[]{ 0.0, 0.0 }, new double[]{ 0.3, 1.0 }, new double[]{ -0.3, 0.0 }, new double[]{ 1.3, 0.0 } };
        for (double[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", t[0]);
            ScorecardModel scm = getSCM(pmml);
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(t[1], predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testSet() throws Exception {
        String pmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.STRING), ScorePmmlTest.makeDataField("y", ScorePmmlTest.DataType.STRING), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSetPredicate("x", ScorePmmlTest.SimpleSetOp.IN, "asdf", "qwer"))), ScorePmmlTest.makeCharacteristic("y_check", ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSetPredicate("y", ScorePmmlTest.SimpleSetOp.NOT_IN, "qwer", "monkey", "ninja")))));
        Object[][] tests = new Object[][]{ new Object[]{ "asdf", "asdf", 3.0 }, new Object[]{ "qwer", "qwer", 1.0 }, new Object[]{ "monkey", "monkey", 0.0 }, new Object[]{ "cowboy", "cowboy", 2.0 }, new Object[]{ "", "", 2.0 }, new Object[]{ null, null, 2.0 }, new Object[]{ "ASDF", "ASDF", 2.0 } };
        for (Object[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", ((Comparable) (t[0])));
            m.put("y", ((Comparable) (t[1])));
            ScorecardModel scm = getSCM(pmml);
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(((Double) (t[2])), predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testMissingParams1() throws Exception {
        String pmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("y", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active"), ScorePmmlTest.makeMiningField("y", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.GE, 0.0))), ScorePmmlTest.makeCharacteristic("y_check", ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSimplePredicate("y", ScorePmmlTest.SimpleOp.GE, 0.0)))));
        ScorecardModel scm = getSCM(pmml);
        HashMap<String, Comparable> m = new HashMap<String, Comparable>();
        Assert.assertEquals(0.0, ScorePmmlTest.score2(scm, m), 1.0E-5);
        m.put("y", 1.0);
        Assert.assertEquals(2.0, ScorePmmlTest.score2(scm, m), 1.0E-5);
        m.put("x", 2.0);
        Assert.assertEquals(3.0, ScorePmmlTest.score2(scm, m), 1.0E-5);
        m.remove("y");
        Assert.assertEquals(1.0, ScorePmmlTest.score2(scm, m), 1.0E-5);
    }

    @Test
    public void testMissingParams2() throws Exception {
        String pmml = ScorePmmlTest.makePmml(ScorePmmlTest.makeDataDictionary(ScorePmmlTest.makeDataField("x", ScorePmmlTest.DataType.STRING), ScorePmmlTest.makeDataField("res", ScorePmmlTest.DataType.DOUBLE)), ScorePmmlTest.makeScorecard(ScorePmmlTest.makeMiningSchema(ScorePmmlTest.makeMiningField("res", "predicted"), ScorePmmlTest.makeMiningField("x", "active")), ScorePmmlTest.makeOutputSchema("res", ScorePmmlTest.DataType.DOUBLE), ScorePmmlTest.makeCharacteristic("x_check", ScorePmmlTest.makeAttribute(0.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.EQ, "XY")), ScorePmmlTest.makeAttribute(1.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.EQ, "X")), ScorePmmlTest.makeAttribute(2.0, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.EQ, "Y")), ScorePmmlTest.makeAttribute(100.0, ScorePmmlTest.makeCompoundPredicate(ScorePmmlTest.CompoundOp.OR, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.MISSING, 0.0), ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.EQ, ""))), ScorePmmlTest.makeAttribute(100.0, ScorePmmlTest.makeCompoundPredicate(ScorePmmlTest.CompoundOp.AND, ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.MISSING, 0.0), ScorePmmlTest.makeSimplePredicate("x", ScorePmmlTest.SimpleOp.EQ, ""))))));
        Object[][] tests = new Object[][]{ new Object[]{ "XY", 0.0 }, new Object[]{ "X", 1.0 }, new Object[]{ "Y", 2.0 }, new Object[]{ "", 100.0 }, new Object[]{ null, 100.0 }, new Object[]{ "BLUDICKA", 0.0 } };
        ScorecardModel scm = getSCM(pmml);
        for (Object[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", ((Comparable) (t[0])));
            m.put("dummy", ((Comparable) (t[0])));
            double predictedScore = ScorePmmlTest.score2(scm, m);
            Assert.assertEquals(((Double) (t[1])), predictedScore, 1.0E-5);
        }
    }

    @Test
    public void testWrongTypes() throws Exception {
        String pmml = ScorePmmlTest.simplePmml;
        Object[][] tests = new Object[][]{ new Object[]{ -1.0, "Y", 1.0 }, new Object[]{ "X", 1.0, 2.0 }, new Object[]{ 1.0, true, 2.0 }, new Object[]{ false, 1.0, 2.0 }, new Object[]{ 1890L, 0.0, 3.0 }, new Object[]{ 0.0, 12L, 4.0 }, new Object[]{ 2222, 0.0, 3.0 }, new Object[]{ 0.0, 99, 4.0 } };
        for (Object[] t : tests) {
            HashMap<String, Comparable> m = new HashMap<String, Comparable>();
            m.put("x", ((Comparable) (t[0])));
            m.put("y", ((Comparable) (t[1])));
            ScorecardModel scm = getSCM(pmml);
            double predictedScore0 = ScorePmmlTest.score2(scm, m);// "interpreter" version

            Assert.assertEquals(((Double) (t[2])), predictedScore0, 1.0E-5);
            double predictedScore1 = ScorePmmlTest.score2(scm, m);// "compiled" w/hashmap version

            Assert.assertEquals(((Double) (t[2])), predictedScore1, 1.0E-5);
        }
    }
}

