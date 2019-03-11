/**
 * Copyright (C) 2017 Lukas Zaruba, lukas.zaruba@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.xhtmlrenderer.fop;


import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Zaruba, lukas.zaruba@gmail.com
 */
public class PDFHyphenationTest {
    private static final String XML = "<html>\n" + ((((((((((((((((((((((((("\t<head>\n" + "\t\t<meta http-equiv=\"Content-Language\" content=\"cs\"/>") + "\t\t<style media=\"print\" type=\"text/css\">\n") + "\t\t\tbody {\n") + "\t\t\t\tbackground: gray;\n") + "\t\t\t\tmargin:0;\n") + "\t\t\t\tword-wrap: break-word; \n") + "\t\t\t\ttext-align: justify;\n") + "\t\t\t\tfont-size: 7.5pt;\n") + "\t\t\t\tline-height: 1;\n") + "               hyphens: auto\n") + "\t\t\t}\n") + "\n") + "\t\t\t@page {\n") + "\t\t\t\tsize: 43mm 25mm;\n") + "\t\t\t\tmargin-top:0cm; \n") + "\t\t\t    margin-left:0cm; \n") + "\t\t\t    margin-right:0cm; \n") + "\t\t\t    margin-bottom:0cm; \n") + "\t\t\t}\n") + "\t\t</style>\n") + "\t</head>\n") + "\t<body>\n") + "		Velice dlouhy text, ktery bude mit problemy se zalamovanim, pokud nebude perfektne nastaveno.") + "\t</body>\n") + "</html>");

    @Test
    public void testGenerator() throws Exception {
        Path temp = Files.createTempFile("pdfTest", ".pdf");
        OutputStream os = Files.newOutputStream(temp);
        generatePDF(PDFHyphenationTest.XML, os);
        System.out.println(temp);
    }
}

