package eu.stamp_project.dci.commits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.diff.DiffFilter;
import eu.stamp_project.dci.json.BlackListElement;
import eu.stamp_project.dci.json.Blacklist;
import eu.stamp_project.dci.json.ProjectJSON;
import eu.stamp_project.dci.repositories.nojson.RepositoriesSetterNoJSON;
import eu.stamp_project.dci.selection.MavenExecutor;
import eu.stamp_project.dci.selection.TestSelectionAccordingDiff;
import eu.stamp_project.dci.selection.TestSuiteSwitcherAndChecker;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 05/11/19
 */
public class ApplicableCommitCounter extends AbstractRepositoryAndGit {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectJSONBuilder.class);

    private static final String BLACKLIST_SUFFIX = "_blacklist";

    private static final String JSON_SUFFIX = ".json";

    private static final String FOLDER_JSON_PATH = "dataset/april-2019/";

    private static final String APPLICABLE_COMMITS_PATH = "_applicable_commits";

    private Blacklist blacklist;

    public class ShaOfApplicableCommitsElementJSON {
        public final String sha;
        public ShaOfApplicableCommitsElementJSON(String sha) {
            this.sha = sha;
        }
    }

    public class ShaOfApplicableCommitsJSON {
        public List<ShaOfApplicableCommitsElementJSON> commits = new ArrayList<>();
        public void add(String sha) {
            this.commits.add(new ShaOfApplicableCommitsElementJSON(sha));
        }
    }

    public ShaOfApplicableCommitsJSON shaOfApplicableCommits = new ShaOfApplicableCommitsJSON();

    public ApplicableCommitCounter(String pathToRepository, String project) {
        super(pathToRepository);
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            this.projectJSON = gson.fromJson(new FileReader(FOLDER_JSON_PATH + project + JSON_SUFFIX), ProjectJSON.class);
            this.blacklist = gson.fromJson(new FileReader(FOLDER_JSON_PATH + project + BLACKLIST_SUFFIX + JSON_SUFFIX), Blacklist.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RevCommit find(List<RevCommit> commits, String sha) {
        return commits.stream()
                .filter(revCommit ->
                        revCommit.getName().equals(sha)
                ).findFirst()
                .get();
    }

    private Function<String, String> getConcernedModule = string ->
            string.contains("src/main/java") ?
                    string.substring(0, string.indexOf("src/main/java")) :
                    string.substring(0, string.indexOf("src/java"));


    private String getConcernedModule(List<String> modifiedJavaFile) {
        return modifiedJavaFile.stream()
                .map(string -> this.getConcernedModule.apply(string))
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(new AbstractMap.SimpleImmutableEntry<>("", 0L))
                .getKey();
    }

    public void run() throws Exception {
        final List<RevCommit> commits = new ArrayList<>();
        this.git.log()
                .add(this.repository.resolve(Constants.HEAD))
                .call()
                .forEach(commits::add);
        for (BlackListElement blackListElement : this.blacklist.blacklist) {
            if (blackListElement.cause.equals("NoBehavioralChanges")) {
                final RevCommit commit = this.find(commits, blackListElement.sha);
                final RevCommit parent = commit.getParents()[0];
                final List<DiffEntry> diffEntries = DiffFilter.computeDiff(this.git, this.repository, commit.getTree(), parent.getTree());
                final List<String> modifiedJavaFiles = DiffFilter.filter(diffEntries);
                if (modifiedJavaFiles.isEmpty()) {
                    throw new RuntimeException();
                }
                final String concernedModule = getConcernedModule(modifiedJavaFiles);
                TestSuiteSwitcherAndChecker.PATH_TEST =
                        new File(this.pathToRootFolder + "/" + concernedModule + "/src/test/java/").exists() ?
                                "/src/test/java/" : "src/test/";
                // 1 setting both project to correct commit
                RepositoriesSetterNoJSON.main(new String[]{
                        "--project", this.projectJSON.name,
                        "--path-to-repository", this.pathToRootFolder,
                        "--sha", blackListElement.sha,
                        "--sha-parent", parent.getName()
                });
                // checks if we find test to be amplified
                final long time = TestSelectionAccordingDiff.testSelection(
                        parent.getName(),
                        this.projectJSON.name,
                        this.pathToRootFolder,
                        concernedModule
                );
                // check if the .csv file is created and contains some tests to be amplified
                final File file = new File(this.pathToRootFolder + "/testsThatExecuteTheChanges.csv");
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String list = reader.lines().collect(Collectors.joining("\n"));
                    if (!list.isEmpty()) {
                        LOGGER.warn("{} has test to be amplified.", blackListElement.sha);
                        this.shaOfApplicableCommits.add(blackListElement.sha);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        JSAPResult configuration = OptionsWrapper.parse(new ProjectBuilderOptions(), args);
        if (configuration.getBoolean("help")) {
            OptionsWrapper.usage();
            System.exit(1);
        }
        final String owner = configuration.getString("owner");
        final String project = configuration.getString("project");
        final String output = configuration.getString("output");
        final String mavenHomeFromCLI = configuration.getString("maven-home");
        LOGGER.info("{}", mavenHomeFromCLI);
        if (mavenHomeFromCLI != null) {
            MavenExecutor.mavenHome = mavenHomeFromCLI;
        }
        final String commit = configuration.getString("commit");
        final ApplicableCommitCounter applicableCommitCounter =
                new ApplicableCommitCounter(configuration.getString("path-to-repository"), project);
        try {
            applicableCommitCounter.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final File file = new File(
                    FOLDER_JSON_PATH + project + APPLICABLE_COMMITS_PATH + JSON_SUFFIX
            );
            LOGGER.info("{}", applicableCommitCounter.shaOfApplicableCommits);
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(gson.toJson(applicableCommitCounter.shaOfApplicableCommits));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
