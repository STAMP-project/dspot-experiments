package org.jabref.logic.exporter;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.logic.cleanup.Cleanups;
import org.jabref.logic.formatter.IdentityFormatter;
import org.jabref.logic.formatter.bibtexfields.NormalizeDateFormatter;
import org.jabref.logic.formatter.bibtexfields.NormalizePagesFormatter;
import org.jabref.logic.formatter.casechanger.LowerCaseFormatter;
import org.jabref.model.cleanup.FieldFormatterCleanup;
import org.jabref.model.cleanup.FieldFormatterCleanups;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldFormatterCleanupsTest {
    private BibEntry entry;

    @Test
    public void checkSimpleUseCase() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("title[identity]"));
        FieldFormatterCleanup identityInTitle = new FieldFormatterCleanup("title", new IdentityFormatter());
        Assertions.assertEquals(Collections.singletonList(identityInTitle), actions.getConfiguredActions());
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("Educational session 1"), entry.getField("title"));
    }

    @Test
    public void invalidSaveActionSting() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("title"));
        Assertions.assertEquals(Collections.emptyList(), actions.getConfiguredActions());
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("Educational session 1"), entry.getField("title"));
    }

    @Test
    public void checkLowerCaseSaveAction() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("title[lower_case]"));
        FieldFormatterCleanup lowerCaseTitle = new FieldFormatterCleanup("title", new LowerCaseFormatter());
        Assertions.assertEquals(Collections.singletonList(lowerCaseTitle), actions.getConfiguredActions());
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("educational session 1"), entry.getField("title"));
    }

    @Test
    public void checkTwoSaveActionsForOneField() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("title[lower_case,identity]"));
        FieldFormatterCleanup lowerCaseTitle = new FieldFormatterCleanup("title", new LowerCaseFormatter());
        FieldFormatterCleanup identityInTitle = new FieldFormatterCleanup("title", new IdentityFormatter());
        Assertions.assertEquals(Arrays.asList(lowerCaseTitle, identityInTitle), actions.getConfiguredActions());
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("educational session 1"), entry.getField("title"));
    }

    @Test
    public void checkThreeSaveActionsForOneField() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("title[lower_case,identity,normalize_date]"));
        FieldFormatterCleanup lowerCaseTitle = new FieldFormatterCleanup("title", new LowerCaseFormatter());
        FieldFormatterCleanup identityInTitle = new FieldFormatterCleanup("title", new IdentityFormatter());
        FieldFormatterCleanup normalizeDatesInTitle = new FieldFormatterCleanup("title", new NormalizeDateFormatter());
        Assertions.assertEquals(Arrays.asList(lowerCaseTitle, identityInTitle, normalizeDatesInTitle), actions.getConfiguredActions());
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("educational session 1"), entry.getField("title"));
    }

    @Test
    public void checkMultipleSaveActions() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("pages[normalize_page_numbers]title[lower_case]"));
        List<FieldFormatterCleanup> formatterCleanups = actions.getConfiguredActions();
        FieldFormatterCleanup normalizePages = new FieldFormatterCleanup("pages", new NormalizePagesFormatter());
        FieldFormatterCleanup lowerCaseTitle = new FieldFormatterCleanup("title", new LowerCaseFormatter());
        Assertions.assertEquals(Arrays.asList(normalizePages, lowerCaseTitle), formatterCleanups);
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("educational session 1"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("1--7"), entry.getField("pages"));
    }

    @Test
    public void checkMultipleSaveActionsWithMultipleFormatters() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("pages[normalize_page_numbers,normalize_date]title[lower_case]"));
        List<FieldFormatterCleanup> formatterCleanups = actions.getConfiguredActions();
        FieldFormatterCleanup normalizePages = new FieldFormatterCleanup("pages", new NormalizePagesFormatter());
        FieldFormatterCleanup normalizeDatesInPages = new FieldFormatterCleanup("pages", new NormalizeDateFormatter());
        FieldFormatterCleanup lowerCaseTitle = new FieldFormatterCleanup("title", new LowerCaseFormatter());
        Assertions.assertEquals(Arrays.asList(normalizePages, normalizeDatesInPages, lowerCaseTitle), formatterCleanups);
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.of("educational session 1"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("1--7"), entry.getField("pages"));
    }

    @Test
    public void clearFormatterRemovesField() {
        FieldFormatterCleanups actions = new FieldFormatterCleanups(true, Cleanups.parse("mont[clear]"));
        actions.applySaveActions(entry);
        Assertions.assertEquals(Optional.empty(), entry.getField("mont"));
    }
}

