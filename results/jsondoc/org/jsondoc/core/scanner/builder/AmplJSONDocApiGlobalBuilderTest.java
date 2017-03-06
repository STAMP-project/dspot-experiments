

package org.jsondoc.core.scanner.builder;


public class AmplJSONDocApiGlobalBuilderTest {
    org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.global.ApiGlobal(sections = { @org.jsondoc.core.annotation.global.ApiGlobalSection(title = "title", paragraphs = { "Paragraph 1" , "Paragraph 2" , "/jsondocfile./src/main/resources/text.txt" }) })
    private class Global {    }

    @org.jsondoc.core.annotation.global.ApiGlobal(sections = { @org.jsondoc.core.annotation.global.ApiGlobalSection(title = "section1", paragraphs = { "Paragraph 1" })
     , @org.jsondoc.core.annotation.global.ApiGlobalSection(title = "abc", paragraphs = { "Paragraph 1" , "Paragraph2" })
     , @org.jsondoc.core.annotation.global.ApiGlobalSection(title = "198xyz", paragraphs = { "Paragraph 1" , "Paragraph2" , "Paragraph3" , "Paragraph4" }) })
    private class MultipleGlobalSections {    }

    @org.jsondoc.core.annotation.global.ApiChangelogSet(changlogs = { @org.jsondoc.core.annotation.global.ApiChangelog(changes = { "Change #1" }, version = "1.0") })
    private class Changelog {    }

    @org.jsondoc.core.annotation.global.ApiMigrationSet(migrations = { @org.jsondoc.core.annotation.global.ApiMigration(fromversion = "1.0", steps = { "Step #1" }, toversion = "1.1") })
    private class Migration {    }

    @org.jsondoc.core.annotation.global.ApiGlobal(sections = { @org.jsondoc.core.annotation.global.ApiGlobalSection(title = "title", paragraphs = { "Paragraph 1" , "Paragraph 2" , "/jsondocfile./src/main/resources/text.txt" }) })
    @org.jsondoc.core.annotation.global.ApiChangelogSet(changlogs = { @org.jsondoc.core.annotation.global.ApiChangelog(changes = { "Change #1" }, version = "1.0") })
    @org.jsondoc.core.annotation.global.ApiMigrationSet(migrations = { @org.jsondoc.core.annotation.global.ApiMigration(fromversion = "1.0", steps = { "Step #1" }, toversion = "1.1") })
    private class AllTogether {    }

    @org.junit.Test
    public void testApiGlobalDoc() {
        org.jsondoc.core.pojo.global.ApiGlobalDoc apiGlobalDoc = jsondocScanner.getApiGlobalDoc(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.Global.class), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet());
        org.junit.Assert.assertNotNull(apiGlobalDoc);
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getSections().size());
        org.jsondoc.core.pojo.global.ApiGlobalSectionDoc sectionDoc = apiGlobalDoc.getSections().iterator().next();
        org.junit.Assert.assertEquals("title", sectionDoc.getTitle());
        org.junit.Assert.assertEquals(3, sectionDoc.getParagraphs().size());
        apiGlobalDoc = jsondocScanner.getApiGlobalDoc(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.Changelog.class), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet());
        org.junit.Assert.assertNotNull(apiGlobalDoc);
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getChangelogset().getChangelogs().size());
        apiGlobalDoc = jsondocScanner.getApiGlobalDoc(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.MultipleGlobalSections.class), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet());
        org.junit.Assert.assertNotNull(apiGlobalDoc);
        org.junit.Assert.assertEquals(3, apiGlobalDoc.getSections().size());
        org.jsondoc.core.pojo.global.ApiGlobalSectionDoc[] apiGlobalSectionDocs = apiGlobalDoc.getSections().toArray(new org.jsondoc.core.pojo.global.ApiGlobalSectionDoc[apiGlobalDoc.getSections().size()]);
        org.junit.Assert.assertEquals("section1", apiGlobalSectionDocs[0].getTitle());
        org.junit.Assert.assertEquals("abc", apiGlobalSectionDocs[1].getTitle());
        org.junit.Assert.assertEquals("198xyz", apiGlobalSectionDocs[2].getTitle());
        apiGlobalDoc = jsondocScanner.getApiGlobalDoc(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.Migration.class));
        org.junit.Assert.assertNotNull(apiGlobalDoc);
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getMigrationset().getMigrations().size());
        apiGlobalDoc = jsondocScanner.getApiGlobalDoc(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.AllTogether.class), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.AllTogether.class), com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiGlobalBuilderTest.AllTogether.class));
        org.junit.Assert.assertNotNull(apiGlobalDoc);
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getSections().size());
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getMigrationset().getMigrations().size());
        org.junit.Assert.assertEquals(1, apiGlobalDoc.getChangelogset().getChangelogs().size());
    }
}

