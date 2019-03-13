package me.tatarka;


import TaskOutcome.SUCCESS;
import java.io.File;
import java.io.StringWriter;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class GroovyPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private File rootDir;

    private File buildFile;

    @Test
    public void assemble() throws Exception {
        TestHelpers.writeFile(buildFile, (((((((((((((((("buildscript {\n" + ("    dependencies {\n" + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'groovy\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "dependencies {\n") + "    compile \'org.codehaus.groovy:groovy-all:2.4.7\'\n") + "}"));
        File javaFile = new File(rootDir, "src/main/java/Main.java");
        TestHelpers.writeFile(javaFile, ("public class Main {\n" + (((("    public static void main(String[] args) {\n" + "        Runnable lambda = () -> System.out.println(\"Hello, Lambda!\");\n") + "        lambda.run();\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withProjectDir(rootDir).withArguments("assemble", "--stacktrace").forwardStdError(errorOutput).build();
        assertThat(result.task(":assemble").getOutcome()).isEqualTo(SUCCESS);
        File mainClassFile = TestHelpers.findFile(rootDir, "Main.class");
        File lambdaClassFile = TestHelpers.findFile(rootDir, "Main$$Lambda$1.class");
        assertThat(mainClassFile).exists();
        assertThat(lambdaClassFile).exists();
    }

    @Test
    public void test() throws Exception {
        TestHelpers.writeFile(buildFile, ((((((((((((((((((((("buildscript {\n" + ("    dependencies {\n" + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'groovy\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "dependencies {\n") + "    compile \'org.codehaus.groovy:groovy-all:2.4.7\'\n") + "    testCompile \'junit:junit:4.12\'\n") + "}\n") + "\n") + "test {\n") + "    testLogging { events \"failed\" }\n") + "}"));
        File javaFile = new File(rootDir, "src/main/java/Main.java");
        TestHelpers.writeFile(javaFile, ("import java.util.concurrent.Callable;\n" + ((((("\n" + "public class Main {\n") + "    public static Callable<String> f() {\n") + "        return () -> \"Hello, Lambda Test!\";\n") + "    }\n") + "}")));
        File testJavaFile = new File(rootDir, "src/test/java/Test.java");
        TestHelpers.writeFile(testJavaFile, ("import org.junit.Assert;\n" + ((((((((((((("import org.junit.runner.RunWith;\n" + "import org.junit.runners.JUnit4;\n") + "\n") + "import java.util.concurrent.Callable;\n") + "\n") + "@RunWith(JUnit4.class)\n") + "public class Test {\n") + "    @org.junit.Test\n") + "    public void test() throws Exception {\n") + "        Runnable lambda = () -> Assert.assertTrue(true);\n") + "        lambda.run();\n") + "        Assert.assertEquals(\"Hello, Lambda Test!\", Main.f().call());\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withProjectDir(rootDir).withArguments("test", "--stacktrace").forwardStdError(errorOutput).build();
        assertThat(result.task(":test").getOutcome()).isEqualTo(SUCCESS);
    }

    @Test
    public void run() throws Exception {
        TestHelpers.writeFile(buildFile, ((((((((((((((((((((((((("buildscript {\n" + ("    dependencies {\n" + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'groovy\'\n") + "apply plugin: \'application\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "dependencies {\n") + "    compile \'org.codehaus.groovy:groovy-all:2.4.7\'\n") + "}\n") + "\n") + "mainClassName = \"Main\"\n") + "\n") + "jar {\n") + "    manifest {\n") + "        attributes \'Main-Class\': mainClassName\n") + "    }\n") + "}"));
        File javaFile = new File(rootDir, "src/main/java/Main.java");
        TestHelpers.writeFile(javaFile, ("public class Main {\n" + (((("    public static void main(String[] args) {\n" + "        Runnable lambda = () -> System.out.println(\"Hello, Lambda Run!\");\n") + "        lambda.run();\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withProjectDir(rootDir).withArguments("run").forwardStdError(errorOutput).build();
        assertThat(result.task(":run").getOutcome()).isEqualTo(SUCCESS);
        assertThat(result.getOutput()).contains("Hello, Lambda Run!");
    }
}

