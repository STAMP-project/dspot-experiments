package eu.stamp_project.dci.repositories;

import eu.stamp_project.dci.util.AbstractRepositoryAndGit;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public abstract class AbstractRepositoriesManager extends AbstractRepositoryAndGit {

    protected static final String SUFFIX_PATH_TO_OTHER = "_parent";

    protected AbstractRepositoryAndGit parent;

    /**
     * @param pathToRepository path to the root folder of the git repository (must have .git folder)
     */
    public AbstractRepositoriesManager(String pathToRepository) {
        super(pathToRepository);
    }
}
