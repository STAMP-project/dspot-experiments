package hex.tree.xgboost;


import XGBoostUtils.FeatureScore;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class XGBoostUtilsTest {
    @Test
    public void parseFeatureScores() throws IOException, ParseException {
        String[] modelDump = XGBoostUtilsTest.readLines(getClass().getResource("xgbdump.txt"));
        String[] expectedVarImps = XGBoostUtilsTest.readLines(getClass().getResource("xgbvarimps.txt"));
        Map<String, XGBoostUtils.FeatureScore> scores = XGBoostUtils.parseFeatureScores(modelDump);
        double totalGain = 0;
        double totalCover = 0;
        double totalFrequency = 0;
        for (XGBoostUtils.FeatureScore score : scores.values()) {
            totalGain += score._gain;
            totalCover += score._cover;
            totalFrequency += score._frequency;
        }
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        for (String varImp : expectedVarImps) {
            String[] vals = varImp.split(" ");
            XGBoostUtils.FeatureScore score = scores.get(vals[0]);
            Assert.assertNotNull((("Score " + (vals[0])) + " should ve calculated"), score);
            float expectedGain = nf.parse(vals[1]).floatValue();
            Assert.assertEquals(("Gain of " + (vals[0])), expectedGain, ((score._gain) / totalGain), 1.0E-6);
            float expectedCover = nf.parse(vals[2]).floatValue();
            Assert.assertEquals(("Cover of " + (vals[0])), expectedCover, ((score._cover) / totalCover), 1.0E-6);
            float expectedFrequency = nf.parse(vals[3]).floatValue();
            Assert.assertEquals(("Frequency of " + (vals[0])), expectedFrequency, ((score._frequency) / totalFrequency), 1.0E-6);
        }
    }
}

