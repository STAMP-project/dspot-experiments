package jadx.tests.functional;


import jadx.core.export.TemplateFile;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TemplateFileTest {
    @Test
    public void testBuildGradle() throws Exception {
        TemplateFile tmpl = TemplateFile.fromResources("/export/build.gradle.tmpl");
        tmpl.add("applicationId", "SOME_ID");
        tmpl.add("minSdkVersion", 1);
        tmpl.add("targetSdkVersion", 2);
        String res = tmpl.build();
        System.out.println(res);
        Assert.assertThat(res, Matchers.containsString("applicationId 'SOME_ID'"));
        Assert.assertThat(res, Matchers.containsString("targetSdkVersion 2"));
    }
}

