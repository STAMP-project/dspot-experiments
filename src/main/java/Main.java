import fr.inria.diversify.buildSystem.android.InvalidSdkException;
import fr.inria.diversify.dspot.AmplificationHelper;
import fr.inria.diversify.dspot.DSpot;
import fr.inria.diversify.dspot.amplifier.Amplifier;
import fr.inria.diversify.dspot.amplifier.StatementAdderOnAssert;
import fr.inria.diversify.dspot.amplifier.TestDataMutator;
import fr.inria.diversify.dspot.amplifier.TestMethodCallRemover;
import fr.inria.diversify.dspot.selector.PitMutantScoreSelector;
import fr.inria.diversify.runner.InputConfiguration;
import fr.inria.diversify.runner.InputProgram;
import fr.inria.diversify.util.PrintClassUtils;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 12/9/16
 */
public class Main {

    public static void main(String[] args) throws InvalidSdkException, Exception {
        run(args);
        System.exit(0);
    }

    public static void run(String[] args) throws InvalidSdkException, Exception {
        if (args.length < 1) {
            System.err.println("You must specify a path to a properties file");
            System.exit(1);
        }
        InputConfiguration configuration = new InputConfiguration(args[0]);
        AmplificationHelper.setSeedRandom(23L);
        InputProgram program = new InputProgram();

        configuration.setInputProgram(program);
        List<Amplifier> amplifiers = new ArrayList<>();
        amplifiers.add(new TestDataMutator());
        amplifiers.add(new TestMethodCallRemover());
        amplifiers.add(new StatementAdderOnAssert());
        DSpot dspot = new DSpot(configuration, 3, amplifiers, new PitMutantScoreSelector());

        if (args.length > 1) {
            amplifyOne(dspot, args[1], configuration);
        } else {
            amplifyAll(dspot, configuration);
        }
    }

    private static void amplifyOne(DSpot dspot, String fullQualifiedNameTestClass, InputConfiguration configuration) {
        long time = System.currentTimeMillis();
        final File outputDirectory = new File(configuration.getOutputDirectory() + "/");
        try {
            CtType amplifiedTestClass = dspot.amplifyTest(fullQualifiedNameTestClass);
            PrintClassUtils.printJavaFile(outputDirectory, amplifiedTestClass);
        } catch (Exception ignored) {

        }
        System.out.println(System.currentTimeMillis() - time + " ms");
    }

    private static void amplifyAll(DSpot dspot, InputConfiguration configuration) {
        long time = System.currentTimeMillis();
        final File outputDirectory = new File(configuration.getProjectPath() + "/" + configuration.getRelativeTestSourceCodeDir());
        try {
            dspot.amplifyAllTests().forEach(amplifiedTestClass -> {
                        try {
                            PrintClassUtils.printJavaFile(outputDirectory, amplifiedTestClass);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(System.currentTimeMillis() - time + " ms");
    }

}
