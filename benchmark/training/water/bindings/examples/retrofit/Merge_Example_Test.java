package water.bindings.examples.retrofit;


import java.io.IOException;
import org.junit.Test;


public class Merge_Example_Test extends ExampleTestFixture {
    @Test
    public void testRun() throws IOException {
        Merge_Example.mergeExample(ExampleTestFixture.getH2OUrl());
    }
}

