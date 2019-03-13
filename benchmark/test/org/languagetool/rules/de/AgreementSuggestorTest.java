/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.de;


import org.junit.Test;


public class AgreementSuggestorTest {
    @Test
    public void testSuggestions() throws Exception {
        assertSuggestion("das/der/ART:DEF:NOM:SIN:NEU Haus/Haus/SUB:NOM:SIN:NEU", "[das Haus]");
        assertSuggestion("der/der/ART:DEF:NOM:SIN:MAS Haus/Haus/SUB:NOM:SIN:NEU", "[das Haus]");
        assertSuggestion("die/der/ART:DEF:NOM:PLU:FEM Haus/Haus/SUB:NOM:SIN:NEU", "[das Haus]");
        assertSuggestion("das/der/ART:DEF:NOM:PLU:FEM H?user/Haus/SUB:NOM:PLU:NEU", "[die H?user]");
        assertSuggestion("das/der/ART:DEF:NOM:PLU:FEM H?usern/Haus/SUB:DAT:PLU:NEU", "[den H?usern]");
        assertSuggestion("dieser/dies/PRO:DEM:GEN:PLU:NEU:B/S Buch/Buch/SUB:NOM:SIN:NEU", "[dieser B?cher, dieses Buch]");
        assertSuggestion("die/der/PRO:IND:NOM:PLU:NEU:B/S Kabels/Kabel/SUB:GEN:PLU:NEU", "[die Kabel]");
        assertSuggestion("die/der/PRO:IND:NOM:PLU:NEU:B/S LAN-Kabels/LAN-Kabel/SUB:GEN:PLU:NEU", "[die LAN-Kabel]");
        assertSuggestion("mehrere/mehrer/PRO:IND:NOM:PLU:NEU:B/S Kabels/Kabel/SUB:GEN:SIN:MAS", "[mehrere Kabel]");
        assertSuggestion("mehrere/mehrer/PRO:IND:NOM:PLU:NEU:B/S LAN-Kabels/LAN-Kabel/SUB:GEN:SIN:MAS", "[mehrere LAN-Kabel]");
        assertSuggestion("mehrere/mehrer/PRO:IND:NOM:PLU:NEU:B/S WLAN-LAN-Kabels/WLAN-LAN-Kabel/SUB:GEN:SIN:MAS", "[mehrere WLAN-LAN-Kabel]");
    }
}

