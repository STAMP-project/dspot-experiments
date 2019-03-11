package hex;


import ModelMetricsBinomial.MetricBuilderBinomial;
import org.junit.Assert;
import org.junit.Test;


public class ModelMetricsTest {
    @Test
    public void testEmptyModelAUC() {
        ModelMetricsBinomial.MetricBuilderBinomial mbb = new ModelMetricsBinomial.MetricBuilderBinomial(new String[]{ "yes", "yes!!" });
        ModelMetrics mm = mbb.makeModelMetrics(null, null, null, null);
        Assert.assertTrue((mm instanceof ModelMetricsBinomial));
        Assert.assertTrue(Double.isNaN(auc()));
        Assert.assertTrue(Double.isNaN(ModelMetrics.getMetricFromModelMetric(mm, "auc")));
    }
}

