package eu.stamp_project.dci.repositories;

import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.json.CommitJSON;
import eu.stamp_project.dci.json.ProjectJSON;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public class RepositoriesSetter extends AbstractRepositoryAndGit {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoriesSetter.class);

    protected static final String SUFFIX_PATH_TO_OTHER = "_parent";

    protected RepositoriesSetter parent;

    public RepositoriesSetter(String pathToRepository, String project, String jsonFolder) {
        super(pathToRepository);
        this.parent = new RepositoriesSetter(pathToRepository + SUFFIX_PATH_TO_OTHER);
        this.projectJSON = ProjectJSON.load(jsonFolder + "/" + project + ".json");
    }

    public RepositoriesSetter(String pathToRepository, ProjectJSON projectJSON) {
        super(pathToRepository);
        this.parent = new RepositoriesSetter(pathToRepository + SUFFIX_PATH_TO_OTHER);
        this.projectJSON = projectJSON;
    }

    private RepositoriesSetter(String pathToRepository) {
        super(pathToRepository);
    }

    public void setUpForGivenCommit(CommitJSON commit) {
        this.setUpForGivenCommit(commit.sha);
        this.parent.setUpForGivenCommit(commit.parent);
    }

    public void setUpForGivenCommit(String sha) {
        LOGGER.info("{{}} Cleaning local repository...", this.pathToRootFolder);
        try {
            this.git.clean()
                    .setCleanDirectories(true)
                    .setDryRun(true)
                    .call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Checkout {} revision...", sha);
        try {
            this.git.reset()
                    .setRef(sha)
                    .setMode(ResetCommand.ResetType.HARD)
                    .call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUpForGivenCommit(int index) {
        this.setUpForGivenCommit(this.projectJSON.commits.get(index));
    }

    public static void main(String[] args) {
        JSAPResult configuration = OptionsWrapper.parse(new RepositoriesSetterOptions(), args);
        if (configuration.getBoolean("help")) {
            OptionsWrapper.usage();
        }
        final String project = configuration.getString("project");
        final String jsonFolder = configuration.getString("json");
        final int indexOfCommit = configuration.getInt("index");
        final String pathToRepository = configuration.getString("path-to-repository");
        new RepositoriesSetter(pathToRepository, project, jsonFolder)
                .setUpForGivenCommit(indexOfCommit);
    }


}
