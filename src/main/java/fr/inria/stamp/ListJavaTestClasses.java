package fr.inria.stamp;

import spoon.Launcher;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 2/2/17
 */
public class ListJavaTestClasses {

    private static List<String> excludedClasses;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("usage <pathToTestSourceFolder>");
        }

        ;
        if (args.length > 1) {
            excludedClasses = Arrays.asList(args[1].split(System.getProperty("path.separator")));
        } else {
            excludedClasses = Collections.emptyList();
        }

        Launcher launcher = new Launcher();
        launcher.addInputResource(args[0]);

        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);

        launcher.buildModel();

        final List<CtType<?>> allClasses = launcher.getFactory().Class().getAll();
        final List<String> testClasses = allClasses.stream()
                .filter(ctType ->
                        isNotExcluded(ctType) && (
                                gotATestMethod(ctType) ||
                                        (ctType.getSuperclass() != null
                                                && gotATestMethod(ctType.getSuperclass().getDeclaration())) ||
                                        (getExtensionOfGivenType(ctType, allClasses).stream()
                                                .filter(ListJavaTestClasses::gotATestMethod)
                                                .findFirst()
                                                .isPresent()))
                )
                .map(CtType::getQualifiedName).collect(Collectors.toList());
        testClasses.forEach(qualifiedName -> System.out.print(qualifiedName + " "));
    }

    private static boolean isNotExcluded(CtType<?> ctType) {
        return !excludedClasses.stream()
                .filter(excludedClass -> ctType.getPackage().getQualifiedName().contains(excludedClass) ||
                        ctType.getQualifiedName().equals(excludedClass))
                .findFirst()
                .isPresent();
    }

    private static List<CtType> getExtensionOfGivenType(CtType<?> ctTypeToBeExtended, List<CtType<?>> classes) {
        return classes.stream()
                .filter(ctType -> ctType.getSuperclass() != null &&
                        ctType.getSuperclass().getDeclaration().equals(ctTypeToBeExtended))
                .collect(Collectors.toList());
    }

    private static boolean gotATestMethod(CtType<?> ctType) {
        return ctType.getMethods().stream()
                .filter(ctMethod ->
                        ctMethod.getAnnotation(org.junit.Test.class) != null)
                .findFirst()
                .isPresent();
    }

}
