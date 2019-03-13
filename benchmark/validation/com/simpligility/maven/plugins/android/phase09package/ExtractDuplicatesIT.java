package com.simpligility.maven.plugins.android.phase09package;


import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import java.io.File;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({ "3.2.3" })
public class ExtractDuplicatesIT {
    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public ExtractDuplicatesIT(MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.withCliOptions("-X").build();
    }

    @Test
    public void buildDeployAndRun() throws Exception {
        File basedir = resources.getBasedir("duplicates");
        MavenExecutionResult result = mavenRuntime.forProject(basedir).execute("clean", "install");
        result.assertErrorFreeLog();
        result.assertLogText("Duplicate file resourceA");
        result.assertLogText("Duplicate file resourceB");
        result.assertLogText("Duplicate file resourceC");
        File apk = new File(((result.getBasedir().getAbsolutePath()) + "/duplicates-app/target"), "duplicates-app.apk");
        Assert.assertNotNull("APK Not Null", apk);
        ZipFile apkFile = new ZipFile(apk);
        Assert.assertNotNull(apkFile.getEntry("resourceA"));
        Assert.assertNotNull(apkFile.getEntry("resourceB"));
        Assert.assertNotNull(apkFile.getEntry("resourceC"));
        // test services
        assertEntryContents(apkFile, "META-INF/services/com.jayway.maven.plugins.android.TestInterface", "ImplementationA\nImplementationB\nImplementationC\nImplementationApp");
        // test xpath xml
        assertEntryContents(apkFile, "META-INF/kmodule.xml", ExtractDuplicatesIT.expectedKmodule);
        assertEntryContents(apkFile, "kmodule.info", ExtractDuplicatesIT.expectedInfo);
    }

    private static final String expectedKmodule = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((("<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "  <kbase name=\"kb1\" packages=\"org.drools.examples.helloworld\" equalsBehavior=\"equality\" />\n") + "  <kbase name=\"kb2\" packages=\"org.drools.examples.helloworld\" equalsBehavior=\"equality\" />\n") + "</kmodule>");

    private static final String expectedInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((((((((((((((((((((((((((("<org.drools.core.rule.KieModuleMetaInfo>\n" + "  <typeMetaInfos>\n") + "    <entry>\n") + "      <string>org.drools.examples.helloworld.TestType</string>\n") + "      <org.drools.core.rule.TypeMetaInfo>\n") + "        <kind>CLASS</kind>\n") + "        <role>FACT</role>\n") + "        <isDeclaredType>true</isDeclaredType>\n") + "      </org.drools.core.rule.TypeMetaInfo>\n") + "    </entry>\n") + "    <entry>\n") + "      <string>org.drools.examples.helloworld.Message</string>\n") + "      <org.drools.core.rule.TypeMetaInfo>\n") + "        <kind>CLASS</kind>\n") + "        <role>FACT</role>\n") + "        <isDeclaredType>false</isDeclaredType>\n") + "      </org.drools.core.rule.TypeMetaInfo>\n") + "    </entry>\n") + "  </typeMetaInfos>\n") + "  <rulesByPackage>\n") + "    <entry>\n") + "      <string>org.drools.examples.helloworld</string>\n") + "      <set>\n") + "        <string>Hello</string>\n") + "      </set>\n") + "    </entry>\n") + "    <entry>\n") + "      <string>org.drools.examples.goodbye</string>\n") + "      <set>\n") + "        <string>Good Bye</string>\n") + "      </set>\n") + "    </entry>\n") + "  </rulesByPackage>\n") + "</org.drools.core.rule.KieModuleMetaInfo>");
}

