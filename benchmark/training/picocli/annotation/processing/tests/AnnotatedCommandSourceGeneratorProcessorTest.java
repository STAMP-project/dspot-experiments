package picocli.annotation.processing.tests;


import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.StandardLocation;
import org.junit.Test;
import picocli.codegen.annotation.processing.AnnotatedCommandSourceGeneratorProcessor;


public class AnnotatedCommandSourceGeneratorProcessorTest {
    @Test
    public void generate1() {
        AnnotatedCommandSourceGeneratorProcessor processor = new AnnotatedCommandSourceGeneratorProcessor();
        Compilation compilation = javac().withProcessors(processor).compile(JavaFileObjects.forResource("picocli/codegen/aot/graalvm/Example.java"));
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedFile(StandardLocation.SOURCE_OUTPUT, "generated/picocli/codegen/aot/graalvm/Example.java").hasSourceEquivalentTo(JavaFileObjects.forResource("generated/picocli/codegen/aot/graalvm/Example.java"));
    }

    // @Ignore("TODO field constant values")
    @Test
    public void generateNested() {
        AnnotatedCommandSourceGeneratorProcessor processor = new AnnotatedCommandSourceGeneratorProcessor();
        Compilation compilation = javac().withProcessors(processor).compile(JavaFileObjects.forResource("picocli/examples/PopulateFlagsMain.java"));
        assertThat(compilation).succeeded();
        assertThat(compilation).generatedFile(StandardLocation.SOURCE_OUTPUT, "generated/picocli/examples/PopulateFlagsMain.java").hasSourceEquivalentTo(JavaFileObjects.forResource("generated/picocli/examples/PopulateFlagsMain.java"));
    }
}

