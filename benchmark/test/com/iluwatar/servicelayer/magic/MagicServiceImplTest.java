/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.servicelayer.magic;


import com.iluwatar.servicelayer.spell.Spell;
import com.iluwatar.servicelayer.spell.SpellDao;
import com.iluwatar.servicelayer.spellbook.Spellbook;
import com.iluwatar.servicelayer.spellbook.SpellbookDao;
import com.iluwatar.servicelayer.wizard.Wizard;
import com.iluwatar.servicelayer.wizard.WizardDao;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/29/15 - 12:06 AM
 *
 * @author Jeroen Meulemeester
 */
public class MagicServiceImplTest {
    @Test
    public void testFindAllWizards() {
        final WizardDao wizardDao = Mockito.mock(WizardDao.class);
        final SpellbookDao spellbookDao = Mockito.mock(SpellbookDao.class);
        final SpellDao spellDao = Mockito.mock(SpellDao.class);
        final MagicServiceImpl service = new MagicServiceImpl(wizardDao, spellbookDao, spellDao);
        Mockito.verifyZeroInteractions(wizardDao, spellbookDao, spellDao);
        service.findAllWizards();
        Mockito.verify(wizardDao).findAll();
        Mockito.verifyNoMoreInteractions(wizardDao, spellbookDao, spellDao);
    }

    @Test
    public void testFindAllSpellbooks() throws Exception {
        final WizardDao wizardDao = Mockito.mock(WizardDao.class);
        final SpellbookDao spellbookDao = Mockito.mock(SpellbookDao.class);
        final SpellDao spellDao = Mockito.mock(SpellDao.class);
        final MagicServiceImpl service = new MagicServiceImpl(wizardDao, spellbookDao, spellDao);
        Mockito.verifyZeroInteractions(wizardDao, spellbookDao, spellDao);
        service.findAllSpellbooks();
        Mockito.verify(spellbookDao).findAll();
        Mockito.verifyNoMoreInteractions(wizardDao, spellbookDao, spellDao);
    }

    @Test
    public void testFindAllSpells() throws Exception {
        final WizardDao wizardDao = Mockito.mock(WizardDao.class);
        final SpellbookDao spellbookDao = Mockito.mock(SpellbookDao.class);
        final SpellDao spellDao = Mockito.mock(SpellDao.class);
        final MagicServiceImpl service = new MagicServiceImpl(wizardDao, spellbookDao, spellDao);
        Mockito.verifyZeroInteractions(wizardDao, spellbookDao, spellDao);
        service.findAllSpells();
        Mockito.verify(spellDao).findAll();
        Mockito.verifyNoMoreInteractions(wizardDao, spellbookDao, spellDao);
    }

    @Test
    public void testFindWizardsWithSpellbook() throws Exception {
        final String bookname = "bookname";
        final Spellbook spellbook = Mockito.mock(Spellbook.class);
        final Set<Wizard> wizards = new HashSet<>();
        wizards.add(Mockito.mock(Wizard.class));
        wizards.add(Mockito.mock(Wizard.class));
        wizards.add(Mockito.mock(Wizard.class));
        Mockito.when(spellbook.getWizards()).thenReturn(wizards);
        final SpellbookDao spellbookDao = Mockito.mock(SpellbookDao.class);
        Mockito.when(spellbookDao.findByName(ArgumentMatchers.eq(bookname))).thenReturn(spellbook);
        final WizardDao wizardDao = Mockito.mock(WizardDao.class);
        final SpellDao spellDao = Mockito.mock(SpellDao.class);
        final MagicServiceImpl service = new MagicServiceImpl(wizardDao, spellbookDao, spellDao);
        Mockito.verifyZeroInteractions(wizardDao, spellbookDao, spellDao, spellbook);
        final List<Wizard> result = service.findWizardsWithSpellbook(bookname);
        Mockito.verify(spellbookDao).findByName(ArgumentMatchers.eq(bookname));
        Mockito.verify(spellbook).getWizards();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Mockito.verifyNoMoreInteractions(wizardDao, spellbookDao, spellDao);
    }

    @Test
    public void testFindWizardsWithSpell() throws Exception {
        final Set<Wizard> wizards = new HashSet<>();
        wizards.add(Mockito.mock(Wizard.class));
        wizards.add(Mockito.mock(Wizard.class));
        wizards.add(Mockito.mock(Wizard.class));
        final Spellbook spellbook = Mockito.mock(Spellbook.class);
        Mockito.when(spellbook.getWizards()).thenReturn(wizards);
        final SpellbookDao spellbookDao = Mockito.mock(SpellbookDao.class);
        final WizardDao wizardDao = Mockito.mock(WizardDao.class);
        final Spell spell = Mockito.mock(Spell.class);
        Mockito.when(spell.getSpellbook()).thenReturn(spellbook);
        final String spellName = "spellname";
        final SpellDao spellDao = Mockito.mock(SpellDao.class);
        Mockito.when(spellDao.findByName(ArgumentMatchers.eq(spellName))).thenReturn(spell);
        final MagicServiceImpl service = new MagicServiceImpl(wizardDao, spellbookDao, spellDao);
        Mockito.verifyZeroInteractions(wizardDao, spellbookDao, spellDao, spellbook);
        final List<Wizard> result = service.findWizardsWithSpell(spellName);
        Mockito.verify(spellDao).findByName(ArgumentMatchers.eq(spellName));
        Mockito.verify(spellbook).getWizards();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Mockito.verifyNoMoreInteractions(wizardDao, spellbookDao, spellDao);
    }
}

