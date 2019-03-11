package MyPerf4J;


import cn.myperf4j.base.constant.PropertyValues;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/20
 */
public class AbstractBootstrapTest {
    @Test
    public void test() {
        initPropertiesFile(PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB);
        boolean initial = initial();
        Assert.assertTrue(initial);
    }
}

