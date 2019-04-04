package eu.stamp_project.dci.selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 20/09/18
 */
public class TestSuiteSwitcherAndChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteSwitcherAndChecker.class);

    private static final String COMMAND_TEST_SUITE_SWITCHER = "python src/main/python/september-2018/test_suite_switcher.py";

    public static String PATH_TEST = "/src/test/java/";

    private static final String POM_FILE = "/pom.xml";

    private static final String PATH_TO_SUREFIRE_REPORTS = "target/surefire-reports/";

    public static boolean switchAndCheckThatContainAtLeastOneFailingTestCase(String pathToProject,
                                                                             String pathToProjectWithTestSuite,
                                                                             String concernedModule,
                                                                             boolean computeCoverage) {
        LOGGER.info("Test suite: {}", TestSuiteSwitcherAndChecker.PATH_TEST);
        String pathToConcernedModule = pathToProject + "/" + concernedModule;
        String pathToConcernedModuleOther = pathToProjectWithTestSuite + "/" + concernedModule;
        // modify the test source directory
        CommandExecutor.runCmd(
                String.join(" ",
                        COMMAND_TEST_SUITE_SWITCHER,
                        pathToConcernedModuleOther + PATH_TEST,
                        pathToConcernedModule + POM_FILE
                )
        );
        MavenExecutor.runGoals(pathToProject + POM_FILE, "clean", "install");
        MavenExecutor.runGoals(pathToConcernedModule + POM_FILE,
                "clean",
                (computeCoverage ? "org.openclover:clover-maven-plugin:4.2.0:setup" : ""),
                "test",
                "-Dcheckstyle.skip=true",
                "-Denforcer.skip=true"
        );
        // clean up pom to reuse original
        CommandExecutor.runCmd(
                String.join(" ",
                        COMMAND_TEST_SUITE_SWITCHER,
                        pathToConcernedModule + POM_FILE
                )
        );
        if (!new File(pathToConcernedModule + "/" + PATH_TO_SUREFIRE_REPORTS).exists() ||
                new File(pathToConcernedModule + "/" + PATH_TO_SUREFIRE_REPORTS).listFiles() == null) {
            return false;
        }
        return Arrays.stream(new File(pathToConcernedModule + "/" + PATH_TO_SUREFIRE_REPORTS).listFiles())
                .map(File::getAbsolutePath)
                .filter(path -> path.endsWith(".xml"))
                .filter(path -> {
                    final String[] split = path.split("/");
                    return split[split.length - 1].startsWith("TEST-");
                })
                .anyMatch(TestSuiteSwitcherAndChecker::containsAFailingTestCase);
    }

    private static boolean containsAFailingTestCase(String pathToSurefireReports) {
        try {
            File fXmlFile = new File(pathToSurefireReports);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            final NodeList testCases = doc.getElementsByTagName("testcase");
            return IntStream.range(0, testCases.getLength()).boxed()
                    .map(testCases::item)
                    .anyMatch(TestSuiteSwitcherAndChecker::containsAFailingTestCase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean containsAFailingTestCase(Node testCase) {
        if (testCase.getFirstChild() != null) {
            if (testCase.getFirstChild().getNextSibling() != null) {
                final Node nextSibling = testCase.getFirstChild().getNextSibling();
                return "failure".equals(nextSibling.getNodeName())
                        || "error".equals(nextSibling.getNodeName());
            }
        }
        return false;
    }

}
