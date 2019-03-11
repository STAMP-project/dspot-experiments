package io.github.classgraph.issues.issue171;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.net.URL;
import java.util.List;
import org.junit.Test;


/**
 * The Class Issue171Test.
 */
public class Issue171Test {
    /**
     * Spring boot fully executable jar.
     */
    @Test
    public void springBootFullyExecutableJar() {
        final URL jarURL = Issue171Test.class.getClassLoader().getResource("spring-boot-fully-executable-jar.jar");
        try (ScanResult scanResult = // 
        new ClassGraph().whitelistPackagesNonRecursive("hello", "org.springframework.boot").overrideClasspath((jarURL + "!BOOT-INF/classes")).scan()) {
            final List<String> classNames = scanResult.getAllClasses().getNames();
            // BOOT-INF/lib should be added automatically to the classpath to be scanned
            assertThat(classNames).contains("hello.HelloController", "org.springframework.boot.ApplicationHome");
        }
    }
}

