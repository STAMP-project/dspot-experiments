package org.jabref.model.entry.identifier;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class EprintTest {
    @Test
    public void acceptPlainEprint() {
        Assertions.assertEquals("0706.0001", new Eprint("0706.0001").getEprint());
    }

    @Test
    public void acceptLegacyEprint() {
        Assertions.assertEquals("astro-ph.GT/1234567", new Eprint("astro-ph.GT/1234567").getEprint());
        Assertions.assertEquals("math/1234567", new Eprint("math/1234567").getEprint());
    }

    @Test
    public void acceptPlainEprintWithVersion() {
        Assertions.assertEquals("0706.0001v1", new Eprint("0706.0001v1").getEprint());
    }

    @Test
    public void ignoreLeadingAndTrailingWhitespaces() {
        Assertions.assertEquals("0706.0001v1", new Eprint("  0706.0001v1 ").getEprint());
    }

    @Test
    public void rejectEmbeddedEprint() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Eprint("other stuff 0706.0001v1 end"));
    }

    @Test
    public void rejectInvalidEprint() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Eprint("https://thisisnouri"));
    }

    @Test
    public void acceptArxivPrefix() {
        Assertions.assertEquals("0706.0001v1", new Eprint("arXiv:0706.0001v1").getEprint());
    }

    @Test
    public void acceptURLEprint() {
        // http
        Assertions.assertEquals("0706.0001v1", new Eprint("http://arxiv.org/abs/0706.0001v1").getEprint());
        // https
        Assertions.assertEquals("0706.0001v1", new Eprint("https://arxiv.org/abs/0706.0001v1").getEprint());
        // other domains
        Assertions.assertEquals("0706.0001v1", new Eprint("https://asdf.org/abs/0706.0001v1").getEprint());
    }

    @Test
    public void constructCorrectURLForEprint() {
        Assertions.assertEquals("https://arxiv.org/abs/0706.0001v1", new Eprint("0706.0001v1").getURIAsASCIIString());
    }
}

