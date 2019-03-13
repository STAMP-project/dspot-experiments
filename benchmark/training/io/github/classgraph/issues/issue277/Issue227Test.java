package io.github.classgraph.issues.issue277;


import io.github.classgraph.ClassGraph;
import org.junit.Test;


/**
 * https://github.com/classgraph/classgraph/issues/277
 */
public class Issue227Test {
    /**
     * Test no args blacklist lib or ext jars.
     */
    @Test
    public void testNoArgsBlacklistLibOrExtJars() {
        new ClassGraph().blacklistLibOrExtJars();
    }

    /**
     * Test no args whitelist lib or ext jars.
     */
    @Test
    public void testNoArgsWhitelistLibOrExtJars() {
        new ClassGraph().whitelistLibOrExtJars();
    }
}

