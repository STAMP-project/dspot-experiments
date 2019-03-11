package com.baeldung.jgit;


import com.baeldung.jgit.helper.Helper;
import java.io.IOException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests which show issues with JGit that we reported upstream.
 */
public class JGitBugIntegrationTest {
    @Test
    public void testRevWalkDisposeClosesReader() throws IOException {
        try (Repository repo = Helper.openJGitRepository()) {
            try (ObjectReader reader = newObjectReader()) {
                try (RevWalk walk = new RevWalk(reader)) {
                    dispose();
                    Ref head = exactRef("refs/heads/master");
                    System.out.println(("Found head: " + head));
                    ObjectLoader loader = open(head.getObjectId());
                    Assert.assertNotNull(loader);
                }
            }
        }
    }
}

