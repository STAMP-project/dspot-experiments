package eu.stamp_project.dci.commits.date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.martiansoftware.jsap.JSAPResult;
import eu.stamp_project.dci.json.CommitJSON;
import eu.stamp_project.dci.json.ProjectJSON;
import eu.stamp_project.dci.repositories.RepositoriesSetter;
import eu.stamp_project.dci.util.AbstractRepositoryAndGit;
import eu.stamp_project.dci.util.OptionsWrapper;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 08/10/18
 */
public class DateGetter extends AbstractRepositoryAndGit {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateGetter.class);

    private final String project;

    /**
     * @param project path to the root folder of the git repository (must have .git folder)
     */
    public DateGetter(String project) {
        super("september-2018/dataset/" + project);
        this.project = project;
    }

    public void run() {
        final String pathToJson = "september-2018/dataset/" + project + ".json";
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (new File(pathToJson).exists()) {
            try {
                this.projectJSON = gson.fromJson(new FileReader(pathToJson), ProjectJSON.class);
                final RepositoriesSetter repositoriesSetter = new RepositoriesSetter(this.pathToRootFolder, projectJSON);
                for (CommitJSON commit : projectJSON.commits) {
                    repositoriesSetter.setUpForGivenCommit(commit.sha);
                    final Iterable<RevCommit> call = this.git.log().call();
                    final PersonIdent authorIdent = call.iterator().next().getAuthorIdent();
                    final Date date = authorIdent.getWhen();
                    final LocalDate localDate = date.toInstant().atZone(ZoneId.of(authorIdent.getTimeZone().getID())).toLocalDate();
                    commit.date = String.format("%d/%d/%d", localDate.getMonthValue(), localDate.getDayOfMonth(), localDate.getYear());
                }
                ProjectJSON.save(this.projectJSON, pathToJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.info("{} does not exists", pathToJson);
        }
    }

    public static void main(String[] args) {
        JSAPResult configuration = OptionsWrapper.parse(new DateGetterOptions(), args);
        if (configuration.getBoolean("help")) {
            OptionsWrapper.usage();
            System.exit(1);
        }
        final String project = configuration.getString("project");
        new DateGetter(project).run();
    }

}
