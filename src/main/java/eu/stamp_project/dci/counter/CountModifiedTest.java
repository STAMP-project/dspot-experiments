package eu.stamp_project.dci.counter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.diff.DiffFilter;
import eu.stamp_project.dci.json.CommitJSON;
import eu.stamp_project.dci.json.ProjectJSON;
import eu.stamp_project.dci.repositories.RepositoriesSetter;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 08/10/18
 */
public class CountModifiedTest extends AbstractRepositoryAndGit {

    private ProjectJSON projectJSON;

    /**
     * @param project path to the root folder of the git repository (must have .git folder)
     */
    public CountModifiedTest(String project) {
        super("september-2018/dataset/" + project);
        this.project = project;
    }

    private String project;

    private String module;

    public void run() {
        final String pathToJson = "september-2018/dataset/" + project + ".json";
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (new File(pathToJson).exists()) {
            try {
                HashSet<CtMethod<?>> tests = new HashSet<>();
                this.projectJSON = gson.fromJson(new FileReader(pathToJson), ProjectJSON.class);
                final RepositoriesSetter repositoriesSetter = new RepositoriesSetter(this.pathToRootFolder, projectJSON);
                for (int i = 1 ; i < 2 ; i++) {
                    final CommitJSON commitjson = this.projectJSON.commits.get(i);
                    repositoriesSetter.setUpForGivenCommit(commitjson);
                    this.module = commitjson.concernedModule;
                    final RevCommit commit = this.git.log().call().iterator().next();
                    final RevCommit parentCommit = commit.getParent(0);
                    final List<DiffEntry> diffEntries = DiffFilter.computeDiff(this.git, this.repository, commit.getTree(), parentCommit.getTree());
                    for (DiffEntry diffEntry : diffEntries) {
                        String newPath = diffEntry.getNewPath();
                        String oldPath = diffEntry.getOldPath();
                        File f1 = new File(this.pathToRootFolder + "/" + newPath);
                        File f2 = new File(this.pathToRootFolder + "_parent/" + oldPath);
                        if ("/dev/null".equals(oldPath)) {
                            f1 = new File("empty");
                            f2 = new File(this.pathToRootFolder + "_parent/" + newPath);
                        } else if ("/dev/null".equals(newPath)) {
                            f1 = new File("empty");
                            f2 = new File(this.pathToRootFolder + "/" + oldPath);
                        }
                        final Diff compare = new AstComparator().compare(f1, f2);
                        for (Operation allOperation : compare.getAllOperations()) {
                            if (allOperation.getDstNode() != null) {
                                addParentIfTest(allOperation.getDstNode(), tests);
                            }
                            if (allOperation.getSrcNode() != null) {
                                addParentIfTest(allOperation.getSrcNode(), tests);
                            }
                        }
                    }
                    commitjson.nbModifiedTest = tests.size();
                    tests.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProjectJSON.save(this.projectJSON, pathToJson);
        }
    }

    public void addParentIfTest(CtElement node, HashSet<CtMethod<?>> tests) {
        if (node.getParent(CtMethod.class) != null) {
            if (isTest(node.getParent(CtMethod.class))) {
                tests.add(node.getParent(CtMethod.class));
            }
        }
    }

    public boolean isTest(CtMethod<?> candidate) {
        return hasAnnotation(Test.class, candidate) || (candidate.getSimpleName().contains("test") || candidate.getSimpleName().contains("should"));
    }

    private static boolean hasAnnotation(Class<?> classOfAnnotation, CtElement candidate) {
        return candidate.getAnnotations().stream().anyMatch((ctAnnotation) -> {
            return ctAnnotation.getAnnotationType().getQualifiedName().equals(classOfAnnotation.getName());
        });
    }

    public static void main(String[] args) {
        JSAPResult configuration = OptionsWrapper.parse(new CounterOptions(), args);
        if (configuration.getBoolean("help")) {
            OptionsWrapper.usage();
            System.exit(1);
        }
        final String pathToRepo = configuration.getString("project");
        new CountModifiedTest(pathToRepo).run();
    }

}
