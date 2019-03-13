/**
 * Copyright (C) 2013-2019 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.feio.android.omninotes.utils;


import android.support.v4.util.Pair;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.models.Tag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TagsHelperTest {
    private static String TAG1 = "#mixed";

    private static String TAG2 = "#123numbered";

    private static String TAG3 = "#tags";

    private static String TAG4 = "#tag";

    private static String TAG5 = "#numberedAfter123";

    private Note note;

    @Test
    public void retrievesTagsFromNote() {
        HashMap<String, Integer> tags = TagsHelper.retrieveTags(note);
        Assert.assertEquals(tags.size(), 4);
        Assert.assertTrue(((((tags.containsKey(TagsHelperTest.TAG1)) && (tags.containsKey(TagsHelperTest.TAG3))) && (tags.containsKey(TagsHelperTest.TAG4))) && (tags.containsKey(TagsHelperTest.TAG5))));
        Assert.assertFalse(tags.containsKey(TagsHelperTest.TAG2));
    }

    @Test
    public void retrievesTagsFromNoteMultilanguage() {
        note.setContent("#??????");
        HashMap<String, Integer> tags = TagsHelper.retrieveTags(note);
        Assert.assertTrue(tags.containsKey("#??????"));
        note.setContent("#???????");
        tags = TagsHelper.retrieveTags(note);
        Assert.assertTrue(tags.containsKey("#???????"));
    }

    @Test
    public void removesTagsFromNote() {
        Pair<String, String> pair = TagsHelper.removeTag(note.getTitle(), note.getContent(), Collections.singletonList(new Tag(TagsHelperTest.TAG4, 4)));
        note.setTitle(pair.first);
        note.setContent(pair.second);
        HashMap<String, Integer> tags = TagsHelper.retrieveTags(note);
        Assert.assertTrue(tags.containsKey(TagsHelperTest.TAG1));
        Assert.assertFalse(tags.containsKey(TagsHelperTest.TAG2));
        Assert.assertTrue(tags.containsKey(TagsHelperTest.TAG3));
        Assert.assertFalse(tags.containsKey(TagsHelperTest.TAG4));
    }

    @Test
    public void addsTagsToNote() {
        String newTag = "#addedTag";
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(newTag, 1));
        tags.add(new Tag(TagsHelperTest.TAG3, 1));
        Pair<String, List<Tag>> newTags = TagsHelper.addTagToNote(tags, new Integer[]{ 0, 1 }, note);
        Assert.assertTrue(newTags.first.contains(newTag));
        Assert.assertFalse(newTags.first.contains(TagsHelperTest.TAG3));
    }
}

