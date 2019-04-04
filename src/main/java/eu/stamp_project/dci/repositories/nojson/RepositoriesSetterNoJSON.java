package eu.stamp_project.dci.repositories.nojson;

import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 18/09/18
 */
public class RepositoriesSetterNoJSON extends AbstractRepositoryAndGit {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoriesSetterNoJSON.class);

    protected static final String SUFFIX_PATH_TO_OTHER = "_parent";

    protected RepositoriesSetterNoJSON parent;

    public RepositoriesSetterNoJSON(String pathToRepository, String project) {
        super(pathToRepository);
        this.parent = new RepositoriesSetterNoJSON(pathToRepository + SUFFIX_PATH_TO_OTHER);
    }

    private RepositoriesSetterNoJSON(String pathToRepository) {
        super(pathToRepository);
    }

    public void setUpForGivenCommit(String sha, String shaParent) {
        this.setUpForGivenCommit(sha);
        this.parent.setUpForGivenCommit(shaParent);
    }

    private void setUpForGivenCommit(String sha) {
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

    public static void main(String[] args) {
        OptionsWrapper.jsap = null;
        JSAPResult configuration = OptionsWrapper.parse(new RepositoriesSetterNoJsonOptions(), args);
        if (configuration.getBoolean("help")) {
            OptionsWrapper.usage();
        }
        final String project = configuration.getString("project");
        final String pathToRepository = configuration.getString("path-to-repository");
        final String sha = configuration.getString("sha");
        final String shaParent = configuration.getString("sha-parent");
        new RepositoriesSetterNoJSON(pathToRepository, project).setUpForGivenCommit(sha, shaParent);
    }

}
