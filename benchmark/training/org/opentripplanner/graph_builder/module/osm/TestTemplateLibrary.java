package org.opentripplanner.graph_builder.module.osm;


import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.opentripplanner.openstreetmap.model.OSMWithTags;


/**
 *
 *
 * @author laurent
 */
public class TestTemplateLibrary extends TestCase {
    public void testTemplate() {
        OSMWithTags osmTags = new OSMWithTags();
        osmTags.addTag("note", "Note EN");
        osmTags.addTag("description:fr", "Description FR");
        osmTags.addTag("wheelchair:description", "Wheelchair description EN");
        osmTags.addTag("wheelchair:description:fr", "Wheelchair description FR");
        TestCase.assertEquals(null, TemplateLibrary.generate(null, osmTags));
        TestCase.assertEquals("", TemplateLibrary.generate("", osmTags));
        TestCase.assertEquals("Static text", TemplateLibrary.generate("Static text", osmTags));
        TestCase.assertEquals("Note: Note EN", TemplateLibrary.generate("Note: {note}", osmTags));
        TestCase.assertEquals("Inexistant: ", TemplateLibrary.generate("Inexistant: {foobar:description}", osmTags));
        TestCase.assertEquals("Wheelchair note: Wheelchair description EN", TemplateLibrary.generate("Wheelchair note: {wheelchair:description}", osmTags));
        TestCase.assertEquals(null, TemplateLibrary.generateI18N(null, osmTags));
        Map<String, String> expected = new HashMap<>();
        expected.put(null, "");
        TestCase.assertEquals(expected, TemplateLibrary.generateI18N("", osmTags));
        expected.clear();
        expected.put(null, "Note: Note EN");
        TestCase.assertEquals(expected, TemplateLibrary.generateI18N("Note: {note}", osmTags));
        expected.clear();
        expected.put(null, "Desc: Description FR");
        expected.put("fr", "Desc: Description FR");
        TestCase.assertEquals(expected, TemplateLibrary.generateI18N("Desc: {description}", osmTags));
        expected.clear();
        expected.put(null, "Note: Note EN, Wheelchair description EN");
        expected.put("fr", "Note: Note EN, Wheelchair description FR");
        TestCase.assertEquals(expected, TemplateLibrary.generateI18N("Note: {note}, {wheelchair:description}", osmTags));
        expected.clear();
        expected.put(null, "Note: Note EN, Wheelchair description EN, ");
        expected.put("fr", "Note: Note EN, Wheelchair description FR, ");
        TestCase.assertEquals(expected, TemplateLibrary.generateI18N("Note: {note}, {wheelchair:description}, {foobar:description}", osmTags));
    }
}

