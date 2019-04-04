package eu.stamp_project.dci.counter;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;
import eu.stamp_project.dci.util.Options;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 08/10/18
 */
public class CounterOptions  implements Options {

    @Override
    public JSAP initJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        Switch help = new Switch("help");
        help.setDefault("false");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setHelp("show this help");

        FlaggedOption path = new FlaggedOption("project");
        path.setRequired(true);
        path.setLongFlag("project");
        path.setStringParser(JSAP.STRING_PARSER);

        jsap.registerParameter(path);
        jsap.registerParameter(help);

        return jsap;
    }


}
