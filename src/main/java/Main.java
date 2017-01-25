import fr.inria.diversify.buildSystem.android.InvalidSdkException;
import fr.inria.diversify.dspot.AmplificationHelper;
import fr.inria.diversify.dspot.DSpot;
import fr.inria.diversify.dspot.DSpotUtils;
import fr.inria.diversify.dspot.selector.PitMutantScoreSelector;
import fr.inria.diversify.runner.InputConfiguration;
import fr.inria.diversify.runner.InputProgram;
import spoon.reflect.declaration.CtType;

import java.io.File;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 12/9/16
 */
public class Main {


    public static void main(String[] args) throws InvalidSdkException, Exception {
        run(JSAPOptions.parse(args));
        System.exit(0);
    }

    public static void run(JSAPOptions.Configuration configuration) throws InvalidSdkException, Exception {
        InputConfiguration inputConfiguration = new InputConfiguration(configuration.pathToConfigurationFile);
        AmplificationHelper.setSeedRandom(23L);
        InputProgram program = new InputProgram();
        inputConfiguration.setInputProgram(program);
        DSpot dspot = new DSpot(inputConfiguration, configuration.nbIteration, configuration.amplifiers, new PitMutantScoreSelector());
        if (!"".equals(configuration.pathToOutput)) {
            inputConfiguration.getProperties().setProperty("outputDirectory", configuration.pathToOutput);
        }
        createOutputDirectories(inputConfiguration);
        if ("all".equals(configuration.testCase)) {
            amplifyAll(dspot, inputConfiguration);
        } else {
            amplifyOne(dspot, configuration.testCase, inputConfiguration);
        }
    }

    private static void createOutputDirectories(InputConfiguration inputConfiguration) {
        if (!new File(inputConfiguration.getOutputDirectory()).exists()) {

            String[] paths = inputConfiguration.getOutputDirectory().split(System.getProperty("file.separator"));
            if (!new File(paths[0]).exists()) {
                new File(paths[0]).mkdir();
            }
            new File(inputConfiguration.getOutputDirectory()).mkdir();
        }
    }

    private static void amplifyOne(DSpot dspot, String fullQualifiedNameTestClass, InputConfiguration configuration) {
        long time = System.currentTimeMillis();
        final File outputDirectory = new File(configuration.getOutputDirectory() + "/");
        try {
            CtType amplifiedTestClass = dspot.amplifyTest(fullQualifiedNameTestClass);
            DSpotUtils.printJavaFileWithComment(amplifiedTestClass, outputDirectory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(System.currentTimeMillis() - time + " ms");
    }

    private static void amplifyAll(DSpot dspot, InputConfiguration configuration) {
        long time = System.currentTimeMillis();
        final File outputDirectory = new File(configuration.getOutputDirectory() + "/");
        if (!outputDirectory.exists()) {
            if (!new File("results").exists()) {
                new File("results").mkdir();
            }
            if (!outputDirectory.exists()) {
                outputDirectory.mkdir();
            }
        }
        try {
            dspot.amplifyAllTests().forEach(amplifiedTestClass ->
                    DSpotUtils.printJavaFileWithComment(amplifiedTestClass, outputDirectory)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(System.currentTimeMillis() - time + " ms");
    }

}
