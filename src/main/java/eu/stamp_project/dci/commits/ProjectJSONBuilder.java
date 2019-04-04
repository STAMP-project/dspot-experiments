package eu.stamp_project.dci.commits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.diff.DiffFilter;
import eu.stamp_project.dci.json.BlackListElement;
import eu.stamp_project.dci.json.Blacklist;
import eu.stamp_project.dci.json.CommitJSON;
import eu.stamp_project.dci.json.ProjectJSON;
import eu.stamp_project.dci.repositories.RepositoriesSetter;
import eu.stamp_project.dci.repositories.nojson.RepositoriesSetterNoJSON;
import eu.stamp_project.dci.selection.CommandExecutor;
import eu.stamp_project.dci.selection.MavenExecutor;
import eu.stamp_project.dci.selection.TestSelectionAccordingDiff;
import eu.stamp_project.dci.selection.TestSuiteSwitcherAndChecker;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/08/18
 */
public class ProjectJSONBuilder extends AbstractRepositoryAndGit {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectJSONBuilder.class);

    private Blacklist blacklist;

    private final String absoluteOutputPath;

    private static final String PREFIX_RESULT = "september-2018/result/";

    public ProjectJSONBuilder(String pathToRepository, String owner, String project, String output) {
        super(pathToRepository);
        this.absoluteOutputPath = new File(output + "/" + project).getAbsolutePath();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (new File(this.absoluteOutputPath + ".json").exists()) {
            try {
                this.projectJSON = gson.fromJson(new FileReader(this.absoluteOutputPath + ".json"), ProjectJSON.class);
                new RepositoriesSetter(pathToRepository, this.projectJSON).setUpForGivenCommit(this.projectJSON.masterSha);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                this.projectJSON = new ProjectJSON(owner, project, this.getDate(),
                        this.git.log()
                                .add(this.repository.resolve(Constants.HEAD))
                                .call()
                                .iterator()
                                .next()
                                .getName()
                );
                ProjectJSON.save(this.projectJSON, this.absoluteOutputPath + ".json");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (new File(this.absoluteOutputPath + "_blacklist.json").exists()) {
            try {
                this.blacklist = gson.fromJson(new FileReader(this.absoluteOutputPath + "_blacklist.json"), Blacklist.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.blacklist = new Blacklist();
        }
    }

    private String getDate() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.format("%d/%d/%d", localDate.getMonthValue(), localDate.getDayOfMonth(), localDate.getYear());
    }

    private void buildGivenCandidateCommit(String sha) {
        try {
            final Iterable<RevCommit> commits = this.git.log()
                    .add(this.repository.resolve(Constants.HEAD))
                    .call();
            final Iterator<RevCommit> iterator = commits.iterator();
            while (iterator.hasNext()) {
                final RevCommit commit = iterator.next();
                if (sha.equals(commit.getName())) {
                    if (this.buildCandidateCommit(commit)) {
                        ProjectJSON.save(this.projectJSON, this.absoluteOutputPath + ".json");
                    } else {
                        Blacklist.save(this.blacklist, this.absoluteOutputPath + "_blacklist.json");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildListCandidateCommits(int sizeGoal) {
        try {
            final Iterable<RevCommit> commits = this.git.log()
                    .add(this.repository.resolve(Constants.HEAD))
                    .call();
            final Iterator<RevCommit> iterator = commits.iterator();
            while (iterator.hasNext() && !(this.projectJSON.commits.size() >= sizeGoal)) {
                if (this.buildCandidateCommit(iterator.next())) {
                    ProjectJSON.save(this.projectJSON, this.absoluteOutputPath + ".json");
                } else {
                    Blacklist.save(this.blacklist, this.absoluteOutputPath + "_blacklist.json");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private boolean buildCandidateCommit(RevCommit commit) {
        final String commitName = commit.getName();
        if (commit.getParentCount() < 1) {
            this.blacklist.blacklist.add(new BlackListElement(commitName, "NoShaParent"));
            return false;
        }
        if (this.projectJSON.contains(commitName)) {
            LOGGER.warn("{} is already in the list!", commitName.substring(0, 7));
            return false;
        }
        if (this.blacklist.contains(commitName)) {
            LOGGER.warn("{} is in the blacklist!", commitName.substring(0, 7));
            return false;
        }
        final RevCommit parentCommit = commit.getParents()[0];
        final List<DiffEntry> diffEntries = DiffFilter.computeDiff(this.git, this.repository, commit.getTree(), parentCommit.getTree());
        final List<String> modifiedJavaFiles = DiffFilter.filter(diffEntries);
        if (!modifiedJavaFiles.isEmpty()) {
            final String concernedModule = getConcernedModule(modifiedJavaFiles);
            if (!new File(this.pathToRootFolder + "/" + concernedModule + "/src/test/java/").exists() &&
                    !new File(this.pathToRootFolder + "/" + concernedModule + "/src/test/").exists()) {
                return addToBlackListWithMessageAndCause(commitName, "The module does not contain any test: {}", "NoTest");
            }
            TestSuiteSwitcherAndChecker.PATH_TEST =
                    new File(this.pathToRootFolder + "/" + concernedModule + "/src/test/java/").exists() ?
                            "/src/test/java/" : "src/test/";
            // 1 setting both project to correct commit
            RepositoriesSetterNoJSON.main(new String[]{
                    "--project", this.projectJSON.name,
                    "--path-to-repository", this.pathToRootFolder,
                    "--sha", commitName,
                    "--sha-parent", parentCommit.getName()
            });
            CommandExecutor.runCmd("python src/main/python/september-2018/preparation.py " + this.projectJSON.name);
            if (new File(this.pathToRootFolder + "/" + concernedModule).exists()) {
                TestSuiteSwitcherAndChecker.PATH_TEST =
                    new File(new File(this.pathToRootFolder + "_parent/").getAbsolutePath() + concernedModule + "/src/test/java/").exists() ?
                            "src/test/java" : "src/test/";
                final boolean containsAtLeastOneFailingTestCaseTsOnPPrime = TestSuiteSwitcherAndChecker.switchAndCheckThatContainAtLeastOneFailingTestCase(
                        new File(this.pathToRootFolder + "/").getAbsolutePath(),
                        new File(this.pathToRootFolder + "_parent/").getAbsolutePath(),
                        concernedModule,
                        false // HERE WE COMPUTE ALSO THE COVERAGE
                );
                TestSuiteSwitcherAndChecker.PATH_TEST =
                        new File(new File(this.pathToRootFolder).getAbsolutePath() + concernedModule + "/src/test/java/").exists() ?
                                "src/test/java" : "src/test/";
                final boolean containsAtLeastOneFAilingTestCaseTsPrimeOnP = TestSuiteSwitcherAndChecker.switchAndCheckThatContainAtLeastOneFailingTestCase(
                        new File(this.pathToRootFolder + "_parent/").getAbsolutePath(),
                        new File(this.pathToRootFolder + "/").getAbsolutePath(),
                        concernedModule,
                        false
                );
                if ((!containsAtLeastOneFailingTestCaseTsOnPPrime && !containsAtLeastOneFAilingTestCaseTsPrimeOnP) ||
                        (containsAtLeastOneFailingTestCaseTsOnPPrime && containsAtLeastOneFAilingTestCaseTsPrimeOnP)) {
                    return addToBlackListWithMessageAndCause(commitName, "No behavioral changes could be checked for {}", "NoBehavioralChanges");
                }
                // checks if we find test to be amplified
                final long time = TestSelectionAccordingDiff.testSelection(
                        parentCommit.getName(),
                        this.projectJSON.name,
                        this.pathToRootFolder,
                        concernedModule
                );
                // check if the .csv file is created and contains some tests to be amplified
                final File file = new File(this.pathToRootFolder + "/testsThatExecuteTheChanges.csv");
                if (!file.exists()) {
                    return addToBlackListWithMessageAndCause(commitName, "no test could be found for {}", "SelectionFailed");
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String list = reader.lines().collect(Collectors.joining("\n"));
                    if (!list.isEmpty()) {
                        // copy the csv file to keep it, and rename it
                        final File outputDirectory = new File(PREFIX_RESULT + projectJSON.name
                                + "/commit_" + projectJSON.commits.size() + "_" + commitName.substring(0, 7));
                        if (!(outputDirectory.exists())) {
                            FileUtils.forceMkdir(outputDirectory);
                        }
                        FileUtils.copyFile(file, new File(outputDirectory.getAbsolutePath() + "/testsThatExecuteTheChanges.csv"));
                        final File fileCoverage = new File(this.pathToRootFolder + "/testsThatExecuteTheChanges_coverage.csv");
                        FileUtils.copyFile(fileCoverage, new File(outputDirectory.getAbsolutePath() + "/testsThatExecuteTheChanges_coverage.csv"));
                        this.projectJSON.commits.add(
                                new CommitJSON(commitName,
                                        parentCommit.getName(),
                                        concernedModule,
                                        time,
                                        0)
                        );
                        LOGGER.warn("could find test to be amplified for {}", commitName.substring(0, 7));
                        return true;
                    } else {
                        return addToBlackListWithMessageAndCause(commitName, "No test execute the changes for {}", "NoTestExecuteChanges");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                return addToBlackListWithMessageAndCause(commitName, "Empty test folder{}", "NoTestInModule");
            }
        }
        return addToBlackListWithMessageAndCause(commitName, "There is no java file modified for {}", "NoJavaModification");
    }

    private boolean addToBlackListWithMessageAndCause(String commitName, String s, String noJavaModification) {
        LOGGER.warn(s, commitName.substring(0, 7));
        this.blacklist.blacklist.add(new BlackListElement(commitName, noJavaModification));
        return false;
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
        final ProjectJSONBuilder projectJSONBuilder = new ProjectJSONBuilder(
                configuration.getString("path-to-repository"),
                owner,
                project,
                output
        );

        if (commit != null) {
            projectJSONBuilder.buildGivenCandidateCommit(commit);
        } else {
            projectJSONBuilder.buildListCandidateCommits(configuration.getInt("size-goal"));
        }
    }

}

