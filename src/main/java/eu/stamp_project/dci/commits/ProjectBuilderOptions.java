package eu.stamp_project.dci.commits;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;
import eu.stamp_project.dci.util.Options;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/08/18
 */
public class ProjectBuilderOptions implements Options {

    @Override
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

        FlaggedOption owner = new FlaggedOption("owner");
        owner.setRequired(true);
        owner.setLongFlag("owner");
        owner.setStringParser(JSAP.STRING_PARSER);
        owner.setHelp("[mandatory] specify the name of the owner of the repository");

        FlaggedOption project = new FlaggedOption("project");
        project.setRequired(true);
        project.setLongFlag("project");
        project.setStringParser(JSAP.STRING_PARSER);
        project.setHelp("[mandatory] specify the name of the repository");

        FlaggedOption output = new FlaggedOption("output");
        output.setLongFlag("output");
        output.setDefault("dataset/<owner>_<project>.json");
        output.setShortFlag('o');
        output.setStringParser(JSAP.STRING_PARSER);
        output.setHelp("[optional] specify where to save the generated json.");

        FlaggedOption sizeGoal = new FlaggedOption("size-goal");
        sizeGoal.setLongFlag("size-goal");
        sizeGoal.setDefault("5");
        sizeGoal.setStringParser(JSAP.INTEGER_PARSER);

        FlaggedOption mavenHome = new FlaggedOption("maven-home");
        mavenHome.setLongFlag("maven-home");
        mavenHome.setDefault("/home/spirals/danglot/apache-maven-3.5.4/");
        mavenHome.setStringParser(JSAP.STRING_PARSER);

        FlaggedOption commit = new FlaggedOption("commit");
        commit.setLongFlag("commit");
        commit.setStringParser(JSAP.STRING_PARSER);

        jsap.registerParameter(path);
        jsap.registerParameter(owner);
        jsap.registerParameter(project);
        jsap.registerParameter(output);
        jsap.registerParameter(sizeGoal);
        jsap.registerParameter(mavenHome);
        jsap.registerParameter(commit);
        jsap.registerParameter(help);

        return jsap;
    }

}
