package eu.stamp_project.dci.json;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 18/09/18
 */
public class BlackListElement {

    public final String sha;
    public final String cause;

    public BlackListElement(String sha, String cause) {
        this.sha = sha;
        this.cause = cause;
    }
}
