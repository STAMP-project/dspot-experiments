package water.bindings.examples.retrofit;


import java.io.IOException;
import org.junit.Test;


public class GBM_Example_Test extends ExampleTestFixture {
    @Test
    public void testRun() throws IOException {
        GBM_Example.gbmExampleFlow(ExampleTestFixture.getH2OUrl());
    }
}

