package water.bindings.examples.retrofit;


import java.io.IOException;
import org.junit.Test;


public class ImportPatternExampleTest extends ExampleTestFixture {
    @Test
    public void testRun() throws IOException {
        ImportPatternExample.importPatternExample(ExampleTestFixture.getH2OUrl());
    }
}

