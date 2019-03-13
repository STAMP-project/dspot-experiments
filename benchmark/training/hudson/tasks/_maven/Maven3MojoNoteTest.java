package hudson.tasks._maven;


import org.junit.Test;


public class Maven3MojoNoteTest {
    @Test
    public void testAnnotateMavenPlugin() {
        check("[INFO] <b class=maven-mojo>--- maven-clean-plugin:2.4.1:clean (default-clean) @ jobConfigHistory ---</b>", "[INFO] --- maven-clean-plugin:2.4.1:clean (default-clean) @ jobConfigHistory ---");
    }

    @Test
    public void testAnnotateCodehausPlugin() {
        check("[INFO] <b class=maven-mojo>--- cobertura-maven-plugin:2.4:instrument (report:cobertura) @ sardine ---</b>", "[INFO] --- cobertura-maven-plugin:2.4:instrument (report:cobertura) @ sardine ---");
    }

    @Test
    public void testAnnotateOtherPlugin() {
        check("[INFO] <b class=maven-mojo>--- gmaven-plugin:1.0-rc-5:generateTestStubs (test-in-groovy) @ jobConfigHistory ---</b>", "[INFO] --- gmaven-plugin:1.0-rc-5:generateTestStubs (test-in-groovy) @ jobConfigHistory ---");
    }
}

