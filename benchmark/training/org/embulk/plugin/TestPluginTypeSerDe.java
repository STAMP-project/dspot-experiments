package org.embulk.plugin;


import PluginSource.Type.DEFAULT;
import PluginSource.Type.MAVEN;
import org.embulk.EmbulkTestRuntime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestPluginTypeSerDe {
    @Rule
    public EmbulkTestRuntime testRuntime = new EmbulkTestRuntime();

    @Test
    public void testParseTypeString() {
        PluginType pluginType = testRuntime.getModelManager().readObjectWithConfigSerDe(PluginType.class, "\"file\"");
        Assert.assertTrue((pluginType instanceof DefaultPluginType));
        Assert.assertEquals(DEFAULT, pluginType.getSourceType());
        Assert.assertEquals("file", pluginType.getName());
    }

    @Test
    public void testParseTypeMapping() {
        PluginType pluginType = testRuntime.getModelManager().readObjectWithConfigSerDe(PluginType.class, "{ \"name\": \"dummy\" }");
        Assert.assertTrue((pluginType instanceof DefaultPluginType));
        Assert.assertEquals(DEFAULT, pluginType.getSourceType());
        Assert.assertEquals("dummy", pluginType.getName());
    }

    @Test
    public void testParseTypeMaven() {
        PluginType pluginType = testRuntime.getModelManager().readObjectWithConfigSerDe(PluginType.class, "{ \"name\": \"foo\", \"source\": \"maven\", \"group\": \"org.embulk.bar\", \"version\": \"0.1.2\" }");
        Assert.assertTrue((pluginType instanceof MavenPluginType));
        Assert.assertEquals(MAVEN, pluginType.getSourceType());
        MavenPluginType mavenPluginType = ((MavenPluginType) (pluginType));
        Assert.assertEquals(mavenPluginType.getName(), "foo");
        Assert.assertEquals(mavenPluginType.getGroup(), "org.embulk.bar");
        Assert.assertEquals(mavenPluginType.getVersion(), "0.1.2");
        Assert.assertNull(mavenPluginType.getClassifier());
    }

    @Test
    public void testParseTypeMavenWithClassifier() {
        PluginType pluginType = testRuntime.getModelManager().readObjectWithConfigSerDe(PluginType.class, "{ \"name\": \"foo\", \"source\": \"maven\", \"group\": \"org.embulk.bar\", \"version\": \"0.1.2\", \"classifier\": \"foo\" }");
        Assert.assertTrue((pluginType instanceof MavenPluginType));
        Assert.assertEquals(MAVEN, pluginType.getSourceType());
        MavenPluginType mavenPluginType = ((MavenPluginType) (pluginType));
        Assert.assertEquals(mavenPluginType.getName(), "foo");
        Assert.assertEquals(mavenPluginType.getGroup(), "org.embulk.bar");
        Assert.assertEquals(mavenPluginType.getVersion(), "0.1.2");
        Assert.assertEquals(mavenPluginType.getClassifier(), "foo");
    }
}

