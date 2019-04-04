package eu.stamp_project.dci.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 18/09/18
 */
public class Blacklist {

    public final List<BlackListElement> blacklist;

    public Blacklist(List<BlackListElement> list) {
        this.blacklist = list;
        System.out.println(this.blacklist);
    }

    public Blacklist() {
        this.blacklist = new ArrayList<>();
    }

    public boolean contains(String sha) {
        return this.blacklist.stream()
                .anyMatch(blackListElement -> sha.equals(blackListElement.sha));
    }

    public static void save(Blacklist blacklist, String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File file = new File(path);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(gson.toJson(blacklist));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
