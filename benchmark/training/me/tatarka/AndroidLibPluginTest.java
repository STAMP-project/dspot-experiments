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
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AndroidLibPluginTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private final String androidVersion;

    private final String gradleVersion;

    private final String buildToolsVersion;

    private File rootDir;

    private File buildFile;

    public AndroidLibPluginTest(String androidVersion, String gradleVersion, String buildToolsVersion) {
        this.androidVersion = androidVersion;
        this.gradleVersion = gradleVersion;
        this.buildToolsVersion = buildToolsVersion;
    }

    @Test
    public void assembleDebug() throws Exception {
        // language="Groovy"
        TestHelpers.writeFile(buildFile, ((((((((((((((((((((((((((("buildscript {\n" + (((((("    repositories {\n" + "        maven { url \'https://maven.google.com\' }\n") + "        jcenter()\n") + "    }\n") + "    \n") + "    dependencies {\n") + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "        classpath 'com.android.tools.build:gradle:") + (androidVersion)) + "\'\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'com.android.library\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "android {\n") + "    compileSdkVersion 24\n") + "    buildToolsVersion '") + (buildToolsVersion)) + "\'\n") + "    \n") + "    defaultConfig {\n") + "        minSdkVersion 15\n") + "        targetSdkVersion 24\n") + "    }\n") + "}"));
        File manifestFile = new File(rootDir, "src/main/AndroidManifest.xml");
        // language="XML"
        TestHelpers.writeFile(manifestFile, ("<manifest package=\"test\" " + (("xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" + "    <application/>\n") + "</manifest>")));
        File javaFile = new File(rootDir, "src/main/java/MainActivity.java");
        TestHelpers.writeFile(javaFile, ("package test;" + (((((((("import android.app.Activity;" + "import android.os.Bundle;") + "import android.util.Log;") + "public class MainActivity extends Activity {\n") + "    public void onCreate(Bundle savedInstanceState) {\n") + "        Runnable lambda = () -> Log.d(\"MainActivity\", \"Hello, Lambda!\");\n") + "        lambda.run();\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withGradleVersion(gradleVersion).withProjectDir(rootDir).withArguments("assembleDebug", "--stacktrace").forwardStdError(errorOutput).build();
        assertThat(result.task(":assembleDebug").getOutcome()).isEqualTo(SUCCESS);
        File mainClassFile = TestHelpers.findFile(rootDir, "MainActivity.class");
        File lambdaClassFile = TestHelpers.findFile(rootDir, "MainActivity$$Lambda$1.class");
        assertThat(mainClassFile).exists();
        assertThat(lambdaClassFile).exists();
    }

    @Test
    public void unitTest() throws Exception {
        // language="Groovy"
        TestHelpers.writeFile(buildFile, ((((((((((((((((((((((((((((((("buildscript {\n" + (((((("    repositories {\n" + "        maven { url \'https://maven.google.com\' }\n") + "        jcenter()\n") + "    }\n") + "    \n") + "    dependencies {\n") + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "        classpath 'com.android.tools.build:gradle:") + (androidVersion)) + "\'\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'com.android.library\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "android {\n") + "    compileSdkVersion 24\n") + "    buildToolsVersion '") + (buildToolsVersion)) + "\'\n") + "    \n") + "    defaultConfig {\n") + "        minSdkVersion 15\n") + "        targetSdkVersion 24\n") + "    }\n") + "}\n") + "\n") + "dependencies {\n") + "    testCompile \'junit:junit:4.12\'\n") + "}"));
        File manifestFile = new File(rootDir, "src/main/AndroidManifest.xml");
        // language="XML"
        TestHelpers.writeFile(manifestFile, ("<manifest package=\"test\" " + (("xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" + "    <application/>\n") + "</manifest>")));
        File javaFile = new File(rootDir, "src/main/java/Main.java");
        TestHelpers.writeFile(javaFile, ("import java.util.concurrent.Callable;\n" + ((((("\n" + "public class Main {\n") + "    public static Callable<String> f() {\n") + "        return () -> \"Hello, Lambda Test!\";\n") + "    }\n") + "}")));
        File testJavaFile = new File(rootDir, "src/test/java/Test.java");
        TestHelpers.writeFile(testJavaFile, ("import org.junit.Assert;\n" + ((((((((((((("import org.junit.runner.RunWith;\n" + "import org.junit.runners.JUnit4;\n") + "\n") + "import java.util.concurrent.Callable;\n") + "\n") + "@RunWith(JUnit4.class)\n") + "public class Test {\n") + "    @org.junit.Test\n") + "    public void test() throws Exception {\n") + "        Runnable lambda = () -> Assert.assertTrue(true);\n") + "        lambda.run();\n") + "        Assert.assertEquals(\"Hello, Lambda Test!\", Main.f().call());\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withGradleVersion(gradleVersion).withProjectDir(rootDir).withArguments("test", "--stacktrace").forwardStdError(errorOutput).build();
        assertThat(result.task(":test").getOutcome()).isEqualTo(SUCCESS);
    }

    @Test
    public void androidTest() throws Exception {
        // language="Groovy"
        TestHelpers.writeFile(buildFile, ((((((((((((((((((((((((((((((((("buildscript {\n" + (((((("    repositories {\n" + "        maven { url \'https://maven.google.com\' }\n") + "        jcenter()\n") + "    }\n") + "    \n") + "    dependencies {\n") + "        classpath files(")) + (TestHelpers.getPluginClasspath())) + ")\n") + "        classpath 'com.android.tools.build:gradle:") + (androidVersion)) + "\'\n") + "    }\n") + "}\n") + "\n") + "apply plugin: \'com.android.library\'\n") + "apply plugin: \'me.tatarka.retrolambda\'\n") + "\n") + "repositories {\n") + "    mavenCentral()\n") + "}\n") + "\n") + "android {\n") + "    compileSdkVersion 23\n") + "    buildToolsVersion '") + (buildToolsVersion)) + "\'\n") + "    \n") + "    defaultConfig {\n") + "        minSdkVersion 15\n") + "        targetSdkVersion 24\n") + "        testInstrumentationRunner \"android.support.test.runner.AndroidJUnitRunner\"\n") + "    }\n") + "}\n") + "\n") + "dependencies {\n") + "    androidTestCompile \'com.android.support.test:runner:0.5\'\n") + "    androidTestCompile \'com.android.support.test:rules:0.5\'\n") + "}"));
        File manifestFile = new File(rootDir, "src/main/AndroidManifest.xml");
        // language="XML"
        TestHelpers.writeFile(manifestFile, ("<manifest package=\"test\" " + (("xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" + "    <application/>\n") + "</manifest>")));
        File javaFile = new File(rootDir, "src/main/java/test/Main.java");
        TestHelpers.writeFile(javaFile, ("package test;\nimport java.util.concurrent.Callable;\n" + ((((("\n" + "public class Main {\n") + "    public static Callable<String> f() {\n") + "        return () -> \"Hello, Lambda Test!\";\n") + "    }\n") + "}")));
        File testJavaFile = new File(rootDir, "src/androidTest/java/test/Test.java");
        TestHelpers.writeFile(testJavaFile, ("package test;\nimport org.junit.Assert;\n" + ((((((((((((("import org.junit.runner.RunWith;\n" + "import android.support.test.runner.AndroidJUnit4;\n") + "\n") + "import java.util.concurrent.Callable;\n") + "\n") + "@RunWith(AndroidJUnit4.class)\n") + "public class Test {\n") + "    @org.junit.Test\n") + "    public void test() throws Exception {\n") + "        Runnable lambda = () -> Assert.assertTrue(true);\n") + "        lambda.run();\n") + "        Assert.assertEquals(\"Hello, Lambda Test!\", Main.f().call());\n") + "    }\n") + "}")));
        StringWriter errorOutput = new StringWriter();
        BuildResult result = GradleRunner.create().withGradleVersion(gradleVersion).withProjectDir(rootDir).withArguments("connectedCheck", "--stacktrace").forwardStdError(errorOutput).build();
        assertThat(result.task(":connectedCheck").getOutcome()).isEqualTo(SUCCESS);
    }
}

