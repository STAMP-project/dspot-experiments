import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import fr.inria.diversify.dspot.amplifier.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 1/17/17
 */
public class JSAPOptions {

    public static final JSAP options = initJSAP();

    public static class Configuration {
        public final String pathToConfigurationFile;
        public final List<Amplifier> amplifiers;
        public final int nbIteration;
        public Configuration(String pathToConfigurationFile, List<Amplifier> amplifiers, int nbIteration) {
            this.pathToConfigurationFile = pathToConfigurationFile;
            this.amplifiers = amplifiers;
            this.nbIteration = nbIteration;
        }
    }

    enum AmplifierEnum {
        MethodAdd(new TestMethodCallAdder()),
        MethodRemove(new TestMethodCallRemover()),
        StatementAdderOnAssert(new StatementAdderOnAssert()),
        TestDataMutator(new TestDataMutator());
        public final Amplifier amplifier;
        private AmplifierEnum(Amplifier amplifier) {
            this.amplifier = amplifier;
        }
    }

    public static Configuration parse(String[] args) {
        JSAPResult jsapConfig = options.parse(args);
        if (!jsapConfig.success()) {
            System.err.println();
            for (Iterator<?> errs = jsapConfig.getErrorMessageIterator(); errs.hasNext(); ) {
                System.err.println("Error: " + errs.next());
            }
            showUsage();
        }
        return new Configuration(jsapConfig.getString("path"),
                buildAmplifiersFromString(jsapConfig.getStringArray("amplifiers")),
                jsapConfig.getInt("iteration"));
    }

    private static Amplifier stringToAmplifier(String amplifier) {
        return AmplifierEnum.valueOf(amplifier).amplifier;
    }

    private static List<Amplifier> buildAmplifiersFromString(String[] amplifiersAsString) {
        return Arrays.stream(amplifiersAsString)
                .map(JSAPOptions::stringToAmplifier)
                .collect(Collectors.toList());
    }

    private static void showUsage() {
        System.err.println();
        System.err.println("Usage: java -jar nopol.jar");
        System.err.println("                          " + options.getUsage());
        System.err.println();
        System.err.println(options.getHelp());
    }

    private static JSAP initJSAP() {
        JSAP jsap = new JSAP();

        FlaggedOption pathToConfigFile = new FlaggedOption("path");
        pathToConfigFile.setRequired(true);
        pathToConfigFile.setAllowMultipleDeclarations(false);
        pathToConfigFile.setLongFlag("path");
        pathToConfigFile.setShortFlag('p');
        pathToConfigFile.setStringParser(JSAP.STRING_PARSER);
        pathToConfigFile.setHelp("specify the patht to the configuration file of the target project.");

        try {
            jsap.registerParameter(pathToConfigFile);
        } catch (JSAPException e) {
            throw new RuntimeException(e);
        }

        FlaggedOption amplifiers = new FlaggedOption("amplifiers");
        amplifiers.setRequired(true);
        amplifiers.setList(true);
        amplifiers.setLongFlag("amplifiers");
        amplifiers.setShortFlag('a');
        amplifiers.setStringParser(JSAP.STRING_PARSER);
        amplifiers.setHelp("specify the list of the amplifiers to use");

        try {
            jsap.registerParameter(amplifiers);
        } catch (JSAPException e) {
            throw new RuntimeException(e);
        }

        FlaggedOption iteration = new FlaggedOption("iteration");
        iteration.setRequired(true);
        iteration.setStringParser(JSAP.INTEGER_PARSER);
        iteration.setShortFlag('i');
        iteration.setLongFlag("iteration");
        iteration.setAllowMultipleDeclarations(false);
        iteration.setHelp("specify the number of time each amplifiers is applied");

        try {
            jsap.registerParameter(iteration);
        } catch (JSAPException e) {
            throw new RuntimeException(e);
        }

        return jsap;
    }

}
