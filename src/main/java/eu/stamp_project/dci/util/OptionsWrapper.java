package eu.stamp_project.dci.util;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public class OptionsWrapper {

    public static JSAP jsap;

    public static JSAP getOptions(Options options) {
        if (jsap == null) {
            try {
                jsap = options.initJSAP();
            } catch (JSAPException e) {
                usage();
            }
        }
        return jsap;
    }

    public static JSAPResult parse(Options options, String[] args) {
        return OptionsWrapper.getOptions(options).parse(args);
    }

    public static void usage() {
        System.err.println();
        System.err.println("Usage: ");
        System.err.println("                          " + jsap.getUsage());
        System.err.println();
        System.err.println(jsap.getHelp());
    }

}
