package org.bukkit;


import Note.Tone;
import Note.Tone.B;
import Note.Tone.C;
import Note.Tone.D;
import Note.Tone.F;
import Note.Tone.G;
import com.google.common.collect.Lists;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NoteTest {
    @Test
    public void getToneByData() {
        for (Note.Tone tone : Tone.values()) {
            Assert.assertThat(Tone.getById(tone.getId()), CoreMatchers.is(tone));
        }
    }

    @Test
    public void verifySharpedData() {
        for (Note.Tone tone : Tone.values()) {
            if (!(tone.isSharpable()))
                return;

            Assert.assertFalse(tone.isSharped(tone.getId(false)));
            Assert.assertTrue(tone.isSharped(tone.getId(true)));
        }
    }

    @Test
    public void verifyUnknownToneData() {
        Collection<Byte> tones = Lists.newArrayList();
        for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); i++) {
            tones.add(((byte) (i)));
        }
        for (Note.Tone tone : Tone.values()) {
            if (tone.isSharpable())
                tones.remove(tone.getId(true));

            tones.remove(tone.getId());
        }
        for (Byte data : tones) {
            Assert.assertThat(Tone.getById(data), CoreMatchers.is(CoreMatchers.nullValue()));
            for (Note.Tone tone : Tone.values()) {
                try {
                    tone.isSharped(data);
                    Assert.fail((data + " should throw IllegalArgumentException"));
                } catch (IllegalArgumentException e) {
                    Assert.assertNotNull(e);
                }
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteBelowMin() {
        new Note(((byte) (-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteAboveMax() {
        new Note(((byte) (25)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteOctaveBelowMax() {
        new Note(((byte) (-1)), Tone.A, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteOctaveAboveMax() {
        new Note(((byte) (3)), Tone.A, true);
    }

    @Test
    public void createNoteOctaveNonSharpable() {
        Note note = new Note(((byte) (0)), Tone.B, true);
        Assert.assertFalse(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(C));
    }

    @Test
    public void createNoteFlat() {
        Note note = Note.flat(0, D);
        Assert.assertTrue(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(C));
    }

    @Test
    public void createNoteFlatNonFlattenable() {
        Note note = Note.flat(0, C);
        Assert.assertFalse(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(B));
    }

    @Test
    public void testFlatWrapping() {
        Note note = Note.flat(1, G);
        Assert.assertTrue(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(F));
    }

    @Test
    public void testFlatWrapping2() {
        Note note = flattened();
        Assert.assertTrue(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(F));
    }

    @Test
    public void testSharpWrapping() {
        Note note = sharped();
        Assert.assertTrue(note.isSharped());
        Assert.assertThat(note.getTone(), CoreMatchers.is(F));
        Assert.assertEquals(note.getOctave(), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSharpWrapping2() {
        new Note(2, Tone.F, true).sharped();
    }

    @Test
    public void testHighest() {
        Note note = new Note(2, Tone.F, true);
        Assert.assertEquals(note.getId(), ((byte) (24)));
    }

    @Test
    public void testLowest() {
        Note note = new Note(0, Tone.F, true);
        Assert.assertEquals(note.getId(), ((byte) (0)));
    }

    @Test
    public void doo() {
        for (int i = 1; i <= 24; i++) {
            Note note = new Note(((byte) (i)));
            int octave = note.getOctave();
            Note.Tone tone = note.getTone();
            boolean sharped = note.isSharped();
            Note newNote = new Note(octave, tone, sharped);
            Assert.assertThat(newNote, CoreMatchers.is(note));
            Assert.assertThat(newNote.getId(), CoreMatchers.is(note.getId()));
        }
    }
}

