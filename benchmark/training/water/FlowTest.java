package water;


import org.junit.Test;


public class FlowTest extends TestUtil {
    // ---
    // Test flow-coding a filter & group-by computing e.g. mean
    @Test
    public void testBasic() {
        Key k = Key.make("cars.hex");
        Frame fr = TestUtil.parseFrame(k, "smalldata/cars.csv");
        // Frame fr = parseFrame(k, "../datasets/UCI/UCI-large/covtype/covtype.data");
        // Call into another class so we do not need to weave anything in this class
        // when run as a JUnit
        FlowTest2.basicStatic(k, fr);
    }
}

