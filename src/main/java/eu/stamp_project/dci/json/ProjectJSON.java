package eu.stamp_project.dci.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/08/18
 */
public class ProjectJSON {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectJSON.class);

    public final String owner;
    public final String name;
    public final String date;
    public final String masterSha;
    private int numberCommits;

    public final List<CommitJSON> commits;

    public ProjectJSON(String owner, String name, String date, String masterSha, List<CommitJSON> commits) {
        this.owner = owner;
        this.name = name;
        this.date = date;
        this.masterSha = masterSha;
        this.commits = commits;
    }

    public ProjectJSON(String owner, String name, String date, String masterSha) {
        this.owner = owner;
        this.name = name;
        this.date = date;
        this.masterSha = masterSha;
        this.commits = new ArrayList<>();
    }

    public static void save(ProjectJSON project, String path) {
        project.numberCommits = project.commits.size();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File file = new File(path);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(gson.toJson(project));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProjectJSON load(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File file = new File(path);
        if (file.exists()) {
            try {
                return gson.fromJson(new FileReader(file), ProjectJSON.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException(path  + " doest not exist.");
        }
    }

    public boolean contains(String sha) {
        return this.commits.stream()
                .anyMatch(commitJSON -> commitJSON.sha.equals(sha));
    }
}
