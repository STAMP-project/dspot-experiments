package fr.inria.stamp.remover;

import fr.inria.diversify.buildSystem.maven.MavenBuilder;
import fr.inria.diversify.runner.InputConfiguration;
import fr.inria.diversify.runner.InputProgram;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fr.inria.diversify.dspot.DSpotUtils.buildMavenHome;
import static fr.inria.stamp.remover.FailingTestCasesRemover.FILE_SEPARATOR;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 21/04/17
 */
public class MavenHelper {

    private static final String OPT_VALUE_MUTATORS_EVOSUITE = "-Dmutators=VOID_METHOD_CALLS,NON_VOID_METHOD_CALLS,EXPERIMENTAL_MEMBER_VARIABLE,INCREMENTS,INVERT_NEGS,MATH,NEGATE_CONDITIONALS,CONDITIONALS_BOUNDARY,INLINE_CONSTS";

    public static final String OUTPUT_DIR = "target/pit-reports";

    public static void runMvnPitest(InputConfiguration inputConfiguration, InputProgram inputProgram) throws IOException, InterruptedException {
        final String[] goals = {"org.pitest:pitest-maven:mutationCoverage",
                "-DreportsDirectory=" + OUTPUT_DIR,
                "-DoutputFormats=CSV",
                "-DwithHistory",
                OPT_VALUE_MUTATORS_EVOSUITE,
                "-DtargetClasses=" + inputConfiguration.getProperty("filter") + " ",
                "-DtimeoutConst=10000",
                "-DjvmArgs=16G"};
        MavenBuilder builder = new MavenBuilder(inputProgram.getProgramDir());
        builder.setBuilderPath(buildMavenHome(inputConfiguration));
        builder.setGoals(goals);
        builder.initTimeOut();
    }

    public static void runMvnTest(InputConfiguration inputConfiguration, InputProgram inputProgram, List<String> fullQualifiedNames) throws IOException, InterruptedException {
        MavenBuilder builder = new MavenBuilder(inputProgram.getProgramDir());
        builder.setBuilderPath(buildMavenHome(inputConfiguration));
        builder.setGoals(new String[]{"test", "-Dtest=" + fullQualifiedNames.stream()
                .collect(Collectors.joining(","))});
        builder.initTimeOut();
    }

    public static void runMvnCompile(InputConfiguration inputConfiguration, InputProgram inputProgram) throws IOException, InterruptedException {
        MavenBuilder builder = new MavenBuilder(inputProgram.getProgramDir());
        builder.setBuilderPath(buildMavenHome(inputConfiguration));
        builder.setGoals(new String[]{"clean", "test", "-DskipTests"});
        builder.initTimeOut();
    }

    public static void removeFailingTestCasesOnClasses(InputProgram program, List<String> fullQualifiedNames, Factory factory) {
        final String reportPrefix = program.getProgramDir() + FILE_SEPARATOR + "target" + FILE_SEPARATOR + "surefire-reports" + FILE_SEPARATOR + "TEST-";
        final String reportSuffix = ".xml";
        fullQualifiedNames.forEach(fullQualifiedName -> {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(reportPrefix + fullQualifiedName + reportSuffix);
                Node currentNode = getNextTestCase(doc.getFirstChild().getFirstChild());
                List<String> namesOfFailingTestCase = new ArrayList<>();
                while (currentNode != null) {
                    if (isFailure(currentNode)) {
                        final String currentNameTest = currentNode.getAttributes().getNamedItem("name").getNodeValue();
                        namesOfFailingTestCase.add(currentNameTest);
                    }
                    currentNode = getNextTestCase(currentNode);
                }
                final CtClass<?> testClass = factory.Class().get(fullQualifiedName);
                namesOfFailingTestCase.forEach(name -> testClass.removeMethod(testClass.getMethodsByName(name).get(0)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean hasErrorSurefire(InputProgram program, List<String> fullQualifiedNames) {
        final String reportPrefix = program.getProgramDir() + FILE_SEPARATOR + "target" + FILE_SEPARATOR + "surefire-reports" + FILE_SEPARATOR + "TEST-";
        final String reportSuffix = ".xml";
        return fullQualifiedNames.stream().anyMatch(fullQualifiedName -> {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(reportPrefix + fullQualifiedName + reportSuffix);
                Node currentNode = getNextTestCase(doc.getFirstChild().getFirstChild());
                while (currentNode != null) {
                    if (isFailure(currentNode)) {
                        return true;
                    }
                    currentNode = getNextTestCase(currentNode);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return false;
        });
    }

    private static Node getNextTestCase(Node currentNode) {
        Node nextTestCase = currentNode.getNextSibling();
        while (nextTestCase != null && !nextTestCase.getNodeName().equals("testcase")) {
            nextTestCase = nextTestCase.getNextSibling();
        }
        return nextTestCase;
    }

    private static boolean isFailure(Node node) {
        return node != null
                && node.getFirstChild() != null
                && (node.getFirstChild().getNextSibling().getNodeName().equals("failure") ||
                node.getFirstChild().getNextSibling().getNodeName().equals("error"));
    }

}
