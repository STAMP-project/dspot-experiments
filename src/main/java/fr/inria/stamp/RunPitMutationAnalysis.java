package fr.inria.stamp;

import fr.inria.diversify.automaticbuilder.AutomaticBuilder;
import fr.inria.diversify.automaticbuilder.AutomaticBuilderFactory;
import fr.inria.diversify.dspot.support.DSpotCompiler;
import fr.inria.diversify.utils.Initializer;
import fr.inria.diversify.utils.sosiefier.InputConfiguration;
import fr.inria.diversify.utils.sosiefier.InputProgram;
import spoon.reflect.declaration.CtClass;


/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 11/10/17
 */
public class RunPitMutationAnalysis {

    public static void main(String[] args) throws Exception  {
        if (args.length < 2) {
            System.err.println("Usage: java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis <path-to-properties> <full-qualified-name-test>");
            System.exit(-1);
        }
        Main.verbose = true;
        final InputConfiguration inputConfiguration = new InputConfiguration(args[0]);
        Initializer.initialize(inputConfiguration, false);

        InputProgram inputProgram = inputConfiguration.getInputProgram();

        AutomaticBuilder builder = AutomaticBuilderFactory.getAutomaticBuilder(inputConfiguration);
        String dependencies = builder.buildClasspath(inputProgram.getProgramDir());

        DSpotCompiler compiler = DSpotCompiler.createDSpotCompiler(inputProgram, dependencies);
        inputProgram.setFactory(compiler.getLauncher().getFactory());

        final CtClass<?> classTestToBeRun = inputProgram.getFactory().Class().get(args[1]);
        AutomaticBuilderFactory.getAutomaticBuilder(inputConfiguration)
                .runPit(inputConfiguration.getInputProgram().getProgramDir(),
                        classTestToBeRun
                );

    }


}