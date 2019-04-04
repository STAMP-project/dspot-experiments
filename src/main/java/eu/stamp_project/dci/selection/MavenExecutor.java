package eu.stamp_project.dci.selection;

import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 20/09/18
 */
public class MavenExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenExecutor.class);

    public static String mavenHome = "/home/spirals/danglot/apache-maven-3.5.4/";

    private static final List<String> additionnalProperties = new ArrayList<>();

    static {
        additionnalProperties.add("-Dmaven.compiler.source=1.8");
        additionnalProperties.add("-Dmaven.compiler.target=1.8");
        additionnalProperties.add("-Dmaven.compile.source=1.8");
        additionnalProperties.add("-Dmaven.compile.target=1.8");
        additionnalProperties.add("-Dcheckstyle.skip=true");
        additionnalProperties.add("-Denforcer.skip=true");
        additionnalProperties.add("-Dxwiki.clirr.skip=true");
        additionnalProperties.add("-Danimal.sniffer.skip=true");
    }

    public static int runGoals(String pathToPom, String... goals) {
        InvocationRequest request = new DefaultInvocationRequest();

        final List<String> finalGoals = new ArrayList<>(Arrays.asList(goals));
        finalGoals.addAll(additionnalProperties);
        request.setGoals(finalGoals);
        request.setPomFile(new File(pathToPom));
        request.setJavaHome(new File(System.getProperty("java.home")));

        Properties properties = new Properties();
        properties.setProperty("enforcer.skip", "true");
        properties.setProperty("checkstyle.skip", "true");
        properties.setProperty("cobertura.skip", "true");
        properties.setProperty("skipITs", "true");
        properties.setProperty("rat.skip", "true");
        properties.setProperty("license.skip", "true");
        properties.setProperty("findbugs.skip", "true");
        properties.setProperty("gpg.skip", "true");
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));
        LOGGER.info("run {}/mvn {}", mavenHome, String.join(" ", finalGoals));
        invoker.setOutputHandler(System.out::println);
        invoker.setErrorHandler(System.err::println);
        try {
            return invoker.execute(request).getExitCode();
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
