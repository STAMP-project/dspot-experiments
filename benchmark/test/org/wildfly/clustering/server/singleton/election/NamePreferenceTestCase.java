package org.wildfly.clustering.server.singleton.election;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.group.Node;
import org.wildfly.clustering.singleton.election.NamePreference;
import org.wildfly.clustering.singleton.election.Preference;


public class NamePreferenceTestCase {
    @Test
    public void test() {
        Preference preference = new NamePreference("node1");
        Node node1 = Mockito.mock(Node.class);
        Node node2 = Mockito.mock(Node.class);
        Mockito.when(node1.getName()).thenReturn("node1");
        Mockito.when(node2.getName()).thenReturn("node2");
        Assert.assertTrue(preference.preferred(node1));
        Assert.assertFalse(preference.preferred(node2));
    }
}

