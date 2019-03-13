/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.tooling.maven;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;
import org.sonatype.plexus.build.incremental.DefaultBuildContext;


/**
 * Test case for Maven Enhance Plugin
 *
 * @author Luis Barreiro
 */
public class MavenEnhancePluginTest {
    @Test
    public void testEnhancePlugin() throws Exception {
        File baseDir = new File("target/classes/java/test");
        URL[] baseURLs = new URL[]{ baseDir.toURI().toURL() };
        MavenEnhancePlugin plugin = new MavenEnhancePlugin();
        Map<String, Object> pluginContext = new HashMap<>();
        pluginContext.put("project", new MavenProject());
        setVariableValueToObject(plugin, "pluginContext", pluginContext);
        setVariableValueToObject(plugin, "buildContext", new DefaultBuildContext());
        setVariableValueToObject(plugin, "base", baseDir.getAbsolutePath());
        setVariableValueToObject(plugin, "dir", baseDir.getAbsolutePath());
        setVariableValueToObject(plugin, "failOnError", true);
        setVariableValueToObject(plugin, "enableLazyInitialization", true);
        setVariableValueToObject(plugin, "enableDirtyTracking", true);
        setVariableValueToObject(plugin, "enableAssociationManagement", true);
        setVariableValueToObject(plugin, "enableExtendedEnhancement", false);
        plugin.execute();
        try (URLClassLoader classLoader = new URLClassLoader(baseURLs, getClass().getClassLoader())) {
            Assert.assertTrue(declaresManaged(classLoader.loadClass(MavenEnhancePluginTest.ParentEntity.class.getName())));
            Assert.assertTrue(declaresManaged(classLoader.loadClass(MavenEnhancePluginTest.ChildEntity.class.getName())));
            Assert.assertTrue(declaresManaged(classLoader.loadClass(MavenEnhancePluginTest.TestEntity.class.getName())));
        }
    }

    // --- //
    @MappedSuperclass
    public static class ParentEntity {
        String parentValue;
    }

    @MappedSuperclass
    public static class ChildEntity extends MavenEnhancePluginTest.ParentEntity {
        String childValue;
    }

    @Entity
    public static class TestEntity extends MavenEnhancePluginTest.ChildEntity {
        @Id
        long id;

        String testValue;
    }
}

