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


public class IdeogramsWordCounterTest extends BaseUnitTest {
    private final String CHECKED_SYM = Constants.CHECKED_SYM;

    private final String UNCHECKED_SYM = Constants.UNCHECKED_SYM;

    @Test
    public void countChars() {
        Note note = getNote(1L, "??????", "\u9019\u662f\u4e2d\u6587\u6e2c\u8a66\n \u3053\u308c\u306f\u65e5\u672c\u8a9e\u306e\u30c6\u30b9\u30c8\u3067\u3059");
        Assert.assertEquals(24, new IdeogramsWordCounter().countChars(note));
    }

    @Test
    public void countChecklistChars() {
        String content = (((CHECKED_SYM) + "\u9019\u662f\u4e2d\u6587\u6e2c\u8a66\n") + (UNCHECKED_SYM)) + "????????????";
        Note note = getNote(1L, "??????", content);
        note.setChecklist(true);
        Assert.assertEquals(24, new IdeogramsWordCounter().countChars(note));
    }

    @Test
    public void getWords() {
        Note note = getNote(1L, "??????", "\u9019\u662f\u4e2d\u6587\u6e2c\u8a66\n \u3053\u308c\u306f\u65e5\u672c\u8a9e\u306e\u30c6\u30b9\u30c8\u3067\u3059");
        Assert.assertEquals(24, new IdeogramsWordCounter().countWords(note));
        note.setTitle("?");
        Assert.assertEquals(19, new IdeogramsWordCounter().countWords(note));
    }

    @Test
    public void getChecklistWords() {
        String content = (((CHECKED_SYM) + "\u9019\u662f\u4e2d\u6587\u6e2c\u8a66\n") + (UNCHECKED_SYM)) + "????????????";
        Note note = getNote(1L, "??????", content);
        note.setChecklist(true);
        Assert.assertEquals(24, new IdeogramsWordCounter().countWords(note));
    }
}

