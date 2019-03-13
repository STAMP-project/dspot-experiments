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
package it.feio.android.omninotes.helpers.count;


import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.omninotes.BaseUnitTest;
import it.feio.android.omninotes.models.Note;
import junit.framework.Assert;
import org.junit.Test;


public class DefaultWordCounterTest extends BaseUnitTest {
    private final String CHECKED_SYM = Constants.CHECKED_SYM;

    private final String UNCHECKED_SYM = Constants.UNCHECKED_SYM;

    @Test
    public void countChars() {
        Note note = getNote(1L, "one two", "three four five\nAnother line");
        Assert.assertEquals(30, new DefaultWordCounter().countChars(note));
    }

    @Test
    public void countChecklistChars() {
        String content = (((CHECKED_SYM) + "done\n") + (UNCHECKED_SYM)) + "undone yet";
        Note note = getNote(1L, "checklist", content);
        note.setChecklist(true);
        Assert.assertEquals(22, new DefaultWordCounter().countChars(note));
    }

    @Test
    public void getWords() {
        Note note = getNote(1L, "one two", "three\n four five");
        Assert.assertEquals(5, new DefaultWordCounter().countWords(note));
        note.setTitle("singleword");
        Assert.assertEquals(4, new DefaultWordCounter().countWords(note));
    }

    @Test
    public void getChecklistWords() {
        String content = (((CHECKED_SYM) + "done\n") + (UNCHECKED_SYM)) + "undone yet";
        Note note = getNote(1L, "checklist", content);
        note.setChecklist(true);
        Assert.assertEquals(4, new DefaultWordCounter().countWords(note));
    }
}

