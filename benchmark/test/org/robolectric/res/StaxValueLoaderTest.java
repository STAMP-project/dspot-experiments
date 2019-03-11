package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.res.android.ResTable_config;

import static ResType.CHAR_SEQUENCE;


@RunWith(JUnit4.class)
@SuppressWarnings("NewApi")
public class StaxValueLoaderTest {
    private PackageResourceTable resourceTable;

    private NodeHandler topLevelNodeHandler;

    private StaxDocumentLoader staxDocumentLoader;

    @Test
    public void ignoresXliffTags() throws Exception {
        topLevelNodeHandler.addHandler("resources", new NodeHandler().addHandler("string", new StaxValueLoader(resourceTable, "string", CHAR_SEQUENCE)));
        parse(("<resources xmlns:xliff=\"urn:oasis:names:tc:xliff:document:1.2\">" + ("<string name=\"preposition_for_date\">on <xliff:g id=\"date\" example=\"May 29\">%s</xliff:g></string>" + "</resources>")));
        assertThat(resourceTable.getValue(new ResName("pkg:string/preposition_for_date"), new ResTable_config()).getData()).isEqualTo("on %s");
    }

    @Test
    public void ignoresBTags() throws Exception {
        topLevelNodeHandler.addHandler("resources", new NodeHandler().addHandler("item[@type='string']", new StaxValueLoader(resourceTable, "string", CHAR_SEQUENCE)));
        parse(("<resources xmlns:xliff=\"urn:oasis:names:tc:xliff:document:1.2\">" + ("<item type=\"string\" name=\"sms_short_code_details\">This <b>may cause charges</b> on your mobile account.</item>" + "</resources>")));
        assertThat(resourceTable.getValue(new ResName("pkg:string/sms_short_code_details"), new ResTable_config()).getData()).isEqualTo("This may cause charges on your mobile account.");
    }
}

