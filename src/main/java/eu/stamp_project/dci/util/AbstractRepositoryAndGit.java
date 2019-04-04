package eu.stamp_project.dci.util;

import eu.stamp_project.dci.json.ProjectJSON;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public class AbstractRepositoryAndGit {

    protected String pathToRootFolder;
    protected Repository repository;
    protected Git git;

    protected ProjectJSON projectJSON;

    /**
     * @param pathToRepository path to the root folder of the git repository (must have .git folder)
     */
    public AbstractRepositoryAndGit(String pathToRepository) {
        this.pathToRootFolder = pathToRepository;
        try {
            this.repository = new FileRepositoryBuilder()
                    .setGitDir(new File(pathToRepository + "/.git"))
                    .build();
            this.git = new Git(this.repository);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
