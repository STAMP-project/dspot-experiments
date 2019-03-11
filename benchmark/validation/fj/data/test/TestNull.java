package fj.data.test;


import CheckResult.summary;
import fj.test.Gen;
import fj.test.Property;
import org.junit.Test;


/**
 * Created by MarkPerry on 3/07/2014.
 */
public class TestNull {
    @Test
    public void testShowNullParameters() {
        Property p = Property.property(Gen.value(null), (Integer i) -> prop((i != null)));
        summary.println(p.check());
    }
}

