package fr.inria.stamp.remover;

import fr.inria.diversify.buildSystem.android.InvalidSdkException;
import fr.inria.diversify.dspot.AmplificationChecker;
import fr.inria.diversify.dspot.DSpot;
import fr.inria.diversify.dspot.selector.PitMutantScoreSelector;
import fr.inria.diversify.dspot.support.DSpotCompiler;
import fr.inria.diversify.runner.InputConfiguration;
import fr.inria.diversify.runner.InputProgram;
import fr.inria.diversify.util.FileUtils;
import fr.inria.diversify.util.Log;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.evosuite.runtime.EvoRunner;
import org.junit.runner.RunWith;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.inria.stamp.remover.MavenHelper.*;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 20/04/17
 */
public class FailingTestCasesRemover {

    private static final String PREFIX_PROPERTIES = "src/main/resources/";

    private static final String SUFFIX_PROPERTIES = ".properties";

    private static final String PREFIX_MUTANT = "original/";

    private static final String SUFFIX_MUTANT = "/mutations.csv";

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    private static final Predicate<CtType<?>> isEvosuiteTest = (ctType) ->
            (ctType.getAnnotation(RunWith.class) != null &&
                    ctType.getAnnotation(RunWith.class).value() == EvoRunner.class);

    // project evosuite seed/index
    public static void main(String[] args) throws Exception, InvalidSdkException {
        Log.set(Log.LEVEL_TRACE);
        InputConfiguration inputConfiguration = new InputConfiguration(PREFIX_PROPERTIES + args[0] + SUFFIX_PROPERTIES);
        try {
            FileUtils.forceDelete(new File(inputConfiguration.getProperty("tmpDir")));
        } catch (Exception ignored) {
            //ignored
        }
        DSpot dspot = new DSpot(inputConfiguration, new PitMutantScoreSelector(PREFIX_MUTANT + args[0] + SUFFIX_MUTANT));
        final InputProgram inputProgram = dspot.getInputProgram();
        final String dependencies = inputProgram.getProgramDir() + inputProgram.getClassesDir() + PATH_SEPARATOR +
                inputProgram.getProgramDir() + inputProgram.getTestClassesDir();
        final DSpotCompiler compiler = getCompiler(dspot);
        FileUtils.copyDirectory(new File(inputProgram.getProgramDir() + args[1]), compiler.getSourceOutputDirectory());

        //TODO compilation problems must be handle
        List<CategorizedProblem> categorizedProblems = compiler.compileAndGetProbs(dependencies);
        handleCompilationProblems(categorizedProblems);

        Launcher spoon = buildSpoon(inputProgram.getProgramDir() + args[1]);
        final List<String> fullQualifiedNames = spoon.getFactory().Type().getAll()
                .stream()
                .filter(isEvosuiteTest)
                .map(CtType::getQualifiedName)
                .collect(Collectors.toList());
        runMvnTest(inputConfiguration, inputProgram, fullQualifiedNames);
        removeFailingTestCasesOnClasses(inputProgram, fullQualifiedNames, spoon.getFactory());

        spoon.getFactory().Class().getAll()
                .stream()
                .filter(ctType -> ctType.getMethods().stream().anyMatch(AmplificationChecker::isTest))
                .forEach(ctType -> ctType.getPackage().removeType(ctType));

        try {
            FileUtils.forceDelete(compiler.getSourceOutputDirectory());
        } catch (Exception ignored) {
            //ignored
        }
        spoon.setSourceOutputDirectory(compiler.getSourceOutputDirectory());
        spoon.prettyprint();

        categorizedProblems = compiler.compileAndGetProbs(dependencies);
        handleCompilationProblems(categorizedProblems);
        runMvnTest(inputConfiguration, inputProgram, fullQualifiedNames);
        if (hasErrorSurefire(inputProgram, fullQualifiedNames)) {
            throw new RuntimeException("Error after purifying test cases, cannot handle such generated test suite");
        }
        // run mutation analysis
        runMvnPitest(inputConfiguration, inputProgram);
        // copy source of EvoSuite and pit reports
        final File outputDirectory = new File(args[0] + "_" + args[2]);
        FileUtils.copyDirectory(compiler.getSourceOutputDirectory(), outputDirectory);
        File mutationsResults = new File(inputProgram.getProgramDir() + FILE_SEPARATOR + OUTPUT_DIR);
        if (mutationsResults.listFiles() != null && mutationsResults.listFiles()[0] != null) {
            mutationsResults = new File(mutationsResults.listFiles()[0].getAbsolutePath() + FILE_SEPARATOR + "mutations.csv");
            FileUtils.copyFile(mutationsResults,
                    new File(outputDirectory.getAbsolutePath() + FILE_SEPARATOR + "mutations.csv"));
        } else {
            throw new RuntimeException("error for retrieving results of mutations analysis");
        }
    }

    private static DSpotCompiler getCompiler(DSpot dSpot) {
        try {
            Class<DSpot> dSpotClass = DSpot.class;
            Field compiler = dSpotClass.getDeclaredField("compiler");
            compiler.setAccessible(true);
            return (DSpotCompiler) compiler.get(dSpot);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleCompilationProblems(List<CategorizedProblem> categorizedProblems) {
        //empty TODO
    }

    private static Launcher buildSpoon(String path) {
        Launcher spoon = new Launcher();
        spoon.getEnvironment().setNoClasspath(true);
        spoon.getEnvironment().setCommentEnabled(true);
        spoon.addInputResource(path);
        spoon.buildModel();
        return spoon;
    }
}
