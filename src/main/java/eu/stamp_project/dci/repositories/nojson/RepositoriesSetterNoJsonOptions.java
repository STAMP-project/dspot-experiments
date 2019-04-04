package eu.stamp_project.dci.repositories.nojson;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;
import eu.stamp_project.dci.util.Options;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public class RepositoriesSetterNoJsonOptions implements Options {

    public JSAP initJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        Switch help = new Switch("help");
        help.setDefault("false");
        help.setLongFlag("help");
        help.setShortFlag('h');
        help.setHelp("show this help");

        FlaggedOption path = new FlaggedOption("path-to-repository");
        path.setRequired(true);
        path.setLongFlag("path-to-repository");
        path.setStringParser(JSAP.STRING_PARSER);
        path.setHelp("[mandatory] specify the path to the local clone of the repository");

        FlaggedOption project = new FlaggedOption("project");
        project.setRequired(true);
        project.setLongFlag("project");
        project.setStringParser(JSAP.STRING_PARSER);
        project.setHelp("[mandatory] specify the name of the repository");

        FlaggedOption sha = new FlaggedOption("sha");
        sha.setRequired(true);
        sha.setLongFlag("sha");
        sha.setStringParser(JSAP.STRING_PARSER);
        sha.setHelp("[mandatory] specify the sha");

        FlaggedOption shaParent = new FlaggedOption("sha-parent");
        shaParent.setRequired(true);
        shaParent.setLongFlag("sha-parent");
        shaParent.setStringParser(JSAP.STRING_PARSER);
        shaParent.setHelp("[mandatory] specify the sha parent");

        jsap.registerParameter(path);
        jsap.registerParameter(project);
        jsap.registerParameter(sha);
        jsap.registerParameter(shaParent);
        jsap.registerParameter(help);

        return jsap;
    }

}
