package picocli.annotation.processing.tests;


import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class AbstractCommandSpecProcessorTest {
    @Test
    public void testCommandWithMixin() {
        Compilation compilation = compareCommandDump(slurp("/picocli/examples/mixin/CommandWithMixin.yaml"), JavaFileObjects.forResource("picocli/examples/mixin/CommandWithMixin.java"), JavaFileObjects.forResource("picocli/examples/mixin/CommonOption.java"));
        assertOnlySourceVersionWarning(compilation);
    }

    @Test
    public void testSubcommands() {
        Compilation compilation = compareCommandDump(slurp("/picocli/examples/subcommands/FileUtils.yaml"), JavaFileObjects.forResource("picocli/examples/subcommands/ParentCommandDemo.java"));
        assertOnlySourceVersionWarning(compilation);
    }

    @Test
    public void testSetterMethodOnClass() {
        List<String> expected = slurpAll("/picocli/examples/annotatedmethods/CPrimitives.yaml", "/picocli/examples/annotatedmethods/CPrimitivesWithDefault.yaml", "/picocli/examples/annotatedmethods/CObjectsWithDefaults.yaml", "/picocli/examples/annotatedmethods/CObjects.yaml");
        Compilation compilation = compareCommandDump(expected, JavaFileObjects.forResource("picocli/examples/annotatedmethods/AnnotatedClassMethodOptions.java"));
        assertOnlySourceVersionWarning(compilation);
    }

    @Test
    public void testGetterMethodOnInterface() {
        List<String> expected = slurpAll("/picocli/examples/annotatedmethods/IFPrimitives.yaml", "/picocli/examples/annotatedmethods/IFPrimitivesWithDefault.yaml", "/picocli/examples/annotatedmethods/IFObjects.yaml", "/picocli/examples/annotatedmethods/IFObjectsWithDefault.yaml");
        Compilation compilation = compareCommandDump(expected, JavaFileObjects.forResource("picocli/examples/annotatedmethods/AnnotatedInterfaceMethodOptions.java"));
        assertOnlySourceVersionWarning(compilation);
    }

    @Test
    public void testInvalidAnnotationsOnInterface() {
        CommandSpec2YamlProcessor processor = new CommandSpec2YamlProcessor();
        Compilation compilation = javac().withProcessors(processor).compile(JavaFileObjects.forResource("picocli/examples/annotatedmethods/InvalidAnnotatedInterfaceMethodOptions.java"));
        Assert.assertThat(compilation).failed();
        ImmutableList<Diagnostic<? extends JavaFileObject>> errors = compilation.errors();
        Assert.assertEquals("expected error count", 3, errors.size());
        for (Diagnostic<? extends JavaFileObject> diag : errors) {
            MatcherAssert.assertThat(diag.getMessage(Locale.ENGLISH), CoreMatchers.containsString("Invalid picocli annotation on interface field"));
        }
        // assertThat(compilation).hadErrorContaining("Invalid picocli annotation on interface field");
    }

    @Test
    public void testInvalidAnnotationCombinations() {
        CommandSpec2YamlProcessor processor = new CommandSpec2YamlProcessor();
        Compilation compilation = javac().withProcessors(processor).compile(JavaFileObjects.forResource("picocli/examples/validation/Invalid.java"));
        Assert.assertThat(compilation).failed();
        Set<String> expected = new TreeSet<>(Arrays.asList("Subcommand is missing @Command annotation with a name attribute", "Subcommand @Command annotation should have a name attribute", "@Mixin must have a declared type, not int", "invalidOptionAndMixin cannot have both @picocli.CommandLine.Mixin and @picocli.CommandLine.Option annotations", "invalidParametersAndMixin cannot have both @picocli.CommandLine.Mixin and @picocli.CommandLine.Parameters annotations", "invalidUnmatchedAndMixin cannot have both @picocli.CommandLine.Mixin and @picocli.CommandLine.Unmatched annotations", "invalidSpecAndMixin cannot have both @picocli.CommandLine.Mixin and @picocli.CommandLine.Spec annotations", "invalidOptionAndUnmatched cannot have both @picocli.CommandLine.Unmatched and @picocli.CommandLine.Option annotations", "invalidParametersAndUnmatched cannot have both @picocli.CommandLine.Unmatched and @picocli.CommandLine.Parameters annotations", "invalidOptionAndSpec cannot have both @picocli.CommandLine.Spec and @picocli.CommandLine.Option annotations", "invalidParametersAndSpec cannot have both @picocli.CommandLine.Spec and @picocli.CommandLine.Parameters annotations", "invalidUnmatchedAndSpec cannot have both @picocli.CommandLine.Spec and @picocli.CommandLine.Unmatched annotations", "invalidOptionAndParameters cannot have both @picocli.CommandLine.Option and @picocli.CommandLine.Parameters annotations"));
        ImmutableList<Diagnostic<? extends JavaFileObject>> errors = compilation.errors();
        for (Diagnostic<? extends JavaFileObject> diag : errors) {
            Assert.assertTrue(("Unexpected error: " + (diag.getMessage(Locale.ENGLISH))), expected.remove(diag.getMessage(Locale.ENGLISH)));
        }
        Assert.assertTrue(("Expected errors: " + expected), expected.isEmpty());
    }

    @Test
    public void testCommandWithBundle() {
        Compilation compilation = compareCommandDump(slurp("/picocli/examples/messages/CommandWithBundle.yaml"), JavaFileObjects.forResource("picocli/examples/messages/CommandWithBundle.java"));
        assertOnlySourceVersionWarning(compilation);
    }
}

