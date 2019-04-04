package eu.stamp_project.dci.diff;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 21/09/18
 */
public class DiffFilter {

    private static boolean isAMove(DiffEntry diffEntry, List<DiffEntry> diffEntries) {
        if (diffEntry.getChangeType() == DiffEntry.ChangeType.ADD) {
            final String fileNameToLookFor = getFileNameToLookFor(diffEntry.getNewPath());
            return isAMoveSpecificChangeType(
                    diffEntries,
                    DiffEntry.ChangeType.DELETE,
                    fileNameToLookFor,
                    DiffEntry::getOldPath
            );
        } else if (diffEntry.getChangeType() == DiffEntry.ChangeType.DELETE) {
            final String fileNameToLookFor = getFileNameToLookFor(diffEntry.getOldPath());
            return isAMoveSpecificChangeType(
                    diffEntries,
                    DiffEntry.ChangeType.ADD,
                    fileNameToLookFor,
                    DiffEntry::getNewPath
            );
        }
        return false;
    }

    private static boolean isAMoveSpecificChangeType(List<DiffEntry> diffEntries,
                                              DiffEntry.ChangeType changeType,
                                              String fileNameToLookFor,
                                              Function<DiffEntry, String> getter) {
        return diffEntries.stream().noneMatch(other -> other.getChangeType() == changeType) ||
                diffEntries.stream().filter(other -> other.getChangeType() == changeType)
                        .anyMatch(other -> getter.apply(other).endsWith(fileNameToLookFor));
    }

    private static String getFileNameToLookFor(String filename) {
        final String[] split = filename.split("/");
        return split[split.length - 1];
    }

    public static List<String> filter(List<DiffEntry> diffEntries) {
        return diffEntries.stream()
                .filter(diffEntry -> diffEntry.getChangeType() != DiffEntry.ChangeType.RENAME)
                .filter(diffEntry -> diffEntry.getChangeType() != DiffEntry.ChangeType.COPY)
                .filter(diffEntry -> !DiffFilter.isAMove(diffEntry, diffEntries))
                .map(DiffEntry::getNewPath)
                .filter(DiffFilter::isSourceJavaModification)
                .collect(Collectors.toList());
    }

    public static List<DiffEntry> computeDiff(Git git, Repository repository, RevTree commitTree, RevTree parentTree) {
        try {
            ObjectReader reader = repository.newObjectReader();
            CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();
            CanonicalTreeParser commitTreeParser = new CanonicalTreeParser();
            parentTreeParser.reset(reader, parentTree);
            commitTreeParser.reset(reader, commitTree);
            DiffFormatter diffFormatter = new DiffFormatter(System.out);
            diffFormatter.setRepository(git.getRepository());
            return diffFormatter.scan(commitTreeParser, parentTreeParser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isSourceJavaModification(String path) {
        return path.endsWith(".java") && (path.contains("src/main/java") || path.contains("src/java"));
    }

}
