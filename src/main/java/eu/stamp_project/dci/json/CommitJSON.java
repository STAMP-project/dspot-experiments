package eu.stamp_project.dci.json;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/08/18
 */
public class CommitJSON {

    public final String sha;
    public final String parent;
    public final String concernedModule;
    public final long timeToComputeDiffCoverage;
    public final int nb_test_per_commit;
    public String date;
    public int nbModifiedTest = 0;

    public CommitJSON(String sha,
                      String parent,
                      String concernedModule,
                      long timeToComputeDiffCoverage,
                      int nb_test_per_commit) {
        this.sha = sha;
        this.parent = parent;
        this.concernedModule = concernedModule;
        this.timeToComputeDiffCoverage = timeToComputeDiffCoverage;
        this.nb_test_per_commit = nb_test_per_commit;
        this.date = "";
    }

    public CommitJSON(String sha,
                      String parent,
                      String concernedModule,
                      long timeToComputeDiffCoverage,
                      int nb_test_per_commit,
                      String date) {
        this.sha = sha;
        this.parent = parent;
        this.concernedModule = concernedModule;
        this.timeToComputeDiffCoverage = timeToComputeDiffCoverage;
        this.nb_test_per_commit = nb_test_per_commit;
        this.date = date;
    }

    public CommitJSON(String sha,
                      String parent,
                      String concernedModule,
                      long timeToComputeDiffCoverage,
                      int nb_test_per_commit,
                      String date,
                      int nbModifiedTest) {
        this.sha = sha;
        this.parent = parent;
        this.concernedModule = concernedModule;
        this.timeToComputeDiffCoverage = timeToComputeDiffCoverage;
        this.nb_test_per_commit = nb_test_per_commit;
        this.date = date;
        this.nbModifiedTest = 0;
    }
}
