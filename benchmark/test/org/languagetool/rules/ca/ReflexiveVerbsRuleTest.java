/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Jaume Ortol?
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
package org.languagetool.rules.ca;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class ReflexiveVerbsRuleTest {
    private ReflexiveVerbsRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // TODO: se'n vola / s'envola
        // correct sentences:
        // assertCorrect("la festa de Rams es commemora anant a l'esgl?sia a beneir el palm?");
        // assertCorrect("les circumst?ncies m'obliguen a gloriar-me"); Cal buscar la concordan?a amb (m')
        // assertCorrect("es van agenollar i prosternar");
        // assertCorrect("Una equivocaci? tan gran no es pot callar.");
        // assertCorrect(" ?s del tot necessari si no es vol caure en una religi? alienant");
        assertCorrect("T'endur?s aix?.");
        assertCorrect("Alguns ens adon?rem que era veritat");
        assertCorrect("M'he baixat moltes imatges");
        assertCorrect("baixeu-vos l'Aspell des de http://aspell.net/win32/");
        assertCorrect("els fitxers de traducci? es baixaran autom?ticament");
        assertCorrect("Baixeu-vos el programa de l'enlla?");
        assertCorrect("No em plantejo anar a un altre partit");
        assertCorrect("-Deixa't caure al canal i prou");
        assertCorrect("Deixa't caure al canal i prou");
        assertCorrect("Durant el 2010 s'ha crescut molt");
        assertCorrect("de qu? tant ens queixem");
        assertCorrect("cada zona m?s meridional esdevingu? adient per als ?ssers ?rtics");
        assertCorrect("cereals, garrofers, vinya i olivar.");
        assertCorrect("m'aniria b? probablement posar els quilos");
        assertCorrect("tot m'aniria b?");
        assertCorrect("tot m'havia anat b?");
        assertCorrect("tot m'havia anat molt b? fins que m'ha passat");
        assertCorrect("el cor m'anava a cent per hora.");
        assertIncorrect("Jo m'anava a cent per hora.");
        assertIncorrect("M'anava a casa a cent per hora.");
        assertCorrect("Sempre li havia anat b?");
        assertCorrect("Em va b?");
        assertIncorrect("Sempre t'havies anat b?");
        assertCorrect("Sempre m'havia vingut b?");
        assertCorrect("Sempre m'havia anat b?");
        assertCorrect("T'agraeixo molt que m'hagis deixat robar-te una mica del teu temp");
        assertCorrect("sense haver-s'hi d'esfor?ar gaire");
        assertCorrect("cosa que li permetia, sense haver-s'hi d'esfor?ar gaire, seguir entre classe i classe");
        assertCorrect("fins que no em vingui la inspiraci?");
        assertCorrect("Si no ho trobes b?, v?s-te a queixar al director");
        assertCorrect("potser em vindria de gust fer un mossec");
        assertCorrect("li ho va fer empassar de cop");
        // assertCorrect("del lloc on m'havia caigut"); correcte o incorrecte segons quin sigui el subjecte
        assertCorrect("i matar-se caient de m?s de vuitanta peus d'altura");
        assertCorrect("Deixa de portar-me la contra.");
        // assertIncorrect("No deixis de portar-te el menjar.");
        assertCorrect("quan ja es tornava a envolar li va caure aquest");
        assertCorrect("Van fer agenollar els presos");
        assertCorrect("Deixa'm dir-t'ho a l'orella");
        assertCorrect("Em deixes demanar-te una cosa?");
        assertCorrect("havien fet desbocar un cavall sense brida");
        assertCorrect("quan el vent ja m'hauria portat les rondalles");
        assertCorrect("Llavors m'oloro les mans");
        assertCorrect("Hem de poder-nos queixar");
        assertCorrect("Ens hem de poder queixar");
        assertCorrect("Despr?s d'acomiadar-nos vam pujar a la nau");
        assertCorrect("li havia impedit defensar-se.");
        assertCorrect("L'instant que havia trigat a fer-lo li havia impedit defensar-se.");
        assertCorrect("quan ja s?olorava en l?aire la primavera");
        assertCorrect("que la vergonya em pug?s a les galtes");
        // assertCorrect("per on volia portar-me el mestre");
        // assertCorrect("?De qu? m?havia d?haver adonat?");
        // assertCorrect("i et costa empassar-te la saliva.");
        // assertCorrect("la recan?a que em feia haver-me d?allunyar d?ella");
        assertCorrect("En Feliu em fa dir-te que el dispensis");
        assertCorrect("i que ja em vindria la son");
        assertCorrect("La mort del pare m?havia portat la imatge d?aquests morts");
        assertCorrect("Una onada de foc em pujava del pit a la cara.");
        // donar-se compte
        assertCorrect("D'aquest Decret se n'ha donat compte al Ple de l'Ajuntament");
        assertCorrect("Encara em cal donar compte d'un altre recull");
        assertCorrect("Michael Kirby ens d?na compte a An?lisi estructural");
        // assertCorrect("tractant-se de cas d'urg?ncia i donant-se compte al Ple de l'Ajuntament");
        assertIncorrect("Ell es va donar compte de l'error");
        // assertIncorrect("Es va donar compte de l'error"); //cas dubt?s
        assertIncorrect("Joan es va donar compte de l'error");
        assertIncorrect("Alg? se n'hauria de donar compte.");
        assertIncorrect("Vas donar-te compte de l'error");
        assertIncorrect("llavors comenten discretament l'afer i es callen, tanmateix, els noms");
        // 
        assertCorrect("el qui amb mi delira est? lliure");
        assertCorrect("per venir-vos a veure ");
        assertCorrect("No li ho ensenyis, que el far?s delir.");
        assertCorrect("per a portar-te aigua");
        assertCorrect("que no em costi d'anar al llit");
        assertCorrect("el senyor Colomines s'an? progressivament reposant");
        assertCorrect("en sentir els plors s'encongeix autom?ticament,");
        assertCorrect("La penya de l'Ateneu es va de mica en mica reconstruint");
        assertCorrect("no m'he pogut endur l'espasa");
        assertCorrect("un llop es podria haver endut la criatura");
        assertCorrect("Quan se'l van haver endut a casa");
        assertCorrect("Ja et deus haver adonat que no");
        assertCorrect("fins que se'n va haver desempallegat");
        assertCorrect("per haver-se deixat endur per l'orgull");
        assertCorrect("i de venir-vos a trobar");
        assertCorrect("el sol s'havia post, li anaven portant tots els malalts");
        assertCorrect("que no em caigui la casa");
        assertCorrect("que no em caigui al damunt res");
        assertCorrect("Em queia b?.");
        assertCorrect("Els qui s'havien dispersat van anar pertot arreu");
        assertCorrect("Els qui volen enriquir-se cauen en temptacions");
        assertCorrect("Despr?s d'acomiadar-nos, vam pujar a la nau");
        assertCorrect("que em vingui a ajudar");
        assertCorrect("fins i tot us vendr?eu un amic");
        assertCorrect("ens hem esfor?at molt per venir-vos a veure");
        assertCorrect("Un altre dia s'anava a l'Ermita i un tercer dia se solia anar a altres indrets de car?cter comarcal.");
        assertCorrect("La nit de sant Joan es baixaven falles de la muntanya.");// solucions: marcar "la nit..." com a CC o comprovar la concordan?a subj/verb

        assertCorrect("que no pertanyen a ells mateixos es cau en una contradicci?.");
        assertCorrect("Els salts els fan impulsant-se amb les cames");
        assertCorrect("Zheng, adonant-se que gaireb? totes les forces singaleses");
        assertCorrect("que s'havien anat instal?lant");
        assertCorrect("gr?cies a la pres?ncia del Riu Set s'hi alberga una gran arboreda amb taules");
        assertCorrect("no fa gaires anys tamb? s'hi portaven alguns animals");
        assertCorrect("el s\u00f2lid es va \"descomponent\".");
        assertCorrect("la divisi? s'ha d'anar amb cura per evitar ambig?itats");
        assertCorrect("la senyera s'ha de baixar");
        assertCorrect("Es van t?mer assalts a altres edificis de la CNT ");
        assertCorrect("que Joan em dugu?s el mocador");
        // assertCorrect("que Joan es dugu?s el mocador"); // dubt?s
        assertCorrect("em dur?s un mocador de seda del teu color");
        assertCorrect("El va deixar per a dedicar-se a la m?sica");
        assertCorrect("Hermes s'encarregava de dur les ?nimes que acabaven de morir a l'Infram?n");
        assertCorrect("aquest nom ?s poc adequat ja que es poden portar les propostes de l'escalada cl?ssica");
        // assertCorrect("totes les comissions dels pa?sos vencedors en les guerres napole?niques es van portar els seus propis cuiners");
        assertCorrect("en fer-lo girar se'n podia observar el moviment");
        assertCorrect("el segon dia es duien a terme les carreres individuals");
        assertCorrect("Normalment no es duu un registre oficial extern");
        assertCorrect("Ens portem for?a b?");
        assertCorrect("Hem de portar-nos b?");
        assertCorrect("Ells es porten tres anys");
        assertCorrect("Fan que em malfi?.");
        assertCorrect("Em fan malfiar.");
        assertCorrect("El fan agenollar.");
        assertCorrect("ens anem a aferrissar");
        assertCorrect("anem a aferrissar-nos");
        assertCorrect("ens preparem per a anar");
        assertCorrect("comencen queixant-se");
        assertCorrect("comenceu a queixar-vos");
        assertCorrect("no em podia pas queixar");
        assertCorrect("em puc queixar");
        assertCorrect("en teniu prou amb queixar-vos");
        assertCorrect("ens en podem queixar");
        assertCorrect("es queixa");
        assertCorrect("es va queixant");
        assertCorrect("es va queixar");
        assertCorrect("has d'emportar-t'hi");
        assertCorrect("has de poder-te queixar");
        assertCorrect("t'has de poder queixar");
        assertCorrect("havent-se queixat");
        assertCorrect("haver-se queixat");
        assertCorrect("no es va poder emportar");
        assertCorrect("no has de poder-te queixar");
        assertCorrect("no has de queixar-te");
        assertCorrect("no podeu deixar de queixar-vos");
        assertCorrect("no t'has de queixar");
        assertCorrect("no us podeu deixar de queixar");
        assertCorrect("pareu de queixar-vos");
        assertCorrect("podent abstenir-se");
        assertCorrect("poder-se queixar");
        assertCorrect("podeu queixar-vos");
        assertCorrect("queixa't");
        assertCorrect("queixant-vos");
        assertCorrect("queixar-se");
        assertCorrect("queixeu-vos");
        assertCorrect("s'ha queixat");
        assertCorrect("se li ha queixat");
        assertCorrect("se li queixa");
        assertCorrect("se li va queixar");
        assertCorrect("va decidir su?cidar-se");
        assertCorrect("va queixant-se");
        assertCorrect("va queixar-se");
        assertCorrect("va queixar-se-li");
        assertCorrect("Se'n puj? al cel");
        assertCorrect("Se li'n va anar la m?");
        assertCorrect("El nen pot callar");
        assertCorrect("es va desfent");
        assertCorrect("s'ha anat configurant");
        assertCorrect("s'han anat fabricant amb materials");
        assertCorrect("la mat?ria que cau s'accelera");
        assertCorrect("Altres muntanyes foren pujades per pastors, ca?adors o aventurers.");
        assertCorrect("mai assol? ?xit social");
        assertCorrect("Aquests pol?mers s?n lineals i no ramificats.");
        assertCorrect("tornaven a assolar la Vall de l'Ebre.");
        assertCorrect("est? previst que s'acabin per a anar directament a la zona");
        assertCorrect("es deixaven caure");
        assertCorrect("es van deixar caure");
        assertCorrect("van deixar-se caure");
        assertCorrect("et deixaves pujar");
        assertCorrect("Els animals es feien t?mer amb cops secs de ferro");
        assertCorrect("es vei? obligat a marxar el 1512.");
        assertCorrect("Francesc III es va anar a asseure sobre el tron");
        assertCorrect("Va anar a dutxar-se");
        assertCorrect("es va anar a dutxar");
        assertCorrect("es van deixar anar molts empresonats.");
        assertCorrect("A Joan se li'n va anar la m?");
        assertCorrect("se'ns en va anar la m?");
        assertCorrect("ja que si l'arr?s se sega molt verd");
        assertCorrect("s'hi afegeixen bolets abans d'enfundar-la en l'intest?");
        assertCorrect("Joan ha anat a fer-se la prova.");
        // assertCorrect("Joan s'ha anat a fer la prova."); -->dubt?s
        // assertCorrect("Cada grup s'ha anat a fer la prova."); -->dubt?s
        assertCorrect("Cada grup s'ha anat a dutxar.");
        assertCorrect("Joan ha anat a dutxar-se.");
        assertCorrect("Joan s'ha anat a dutxar.");
        assertCorrect("amb els Confederats intentant burlar el bloqueig a Maryland.");
        // IMPERSONALS
        assertCorrect("l'altre es duu la m? al llavi inferior");
        assertCorrect("l'altre s'olora les mans");
        // assertCorrect("la impressi? que es va endavant");
        assertCorrect("Es pot baixar la darrera versi?.");
        assertCorrect("Se'l va fer callar.");
        assertCorrect("Se li va fer callar.");// incorrecta per una altra q?esti?

        assertCorrect("Se'ns va fer callar.");
        assertCorrect("Tamb? es canta quan es va a pasturar als animals");
        assertCorrect("Quan es baixa a l'ordinador de l'usuari,");
        assertCorrect("sin? que es baixa per parts a l'atzar.");
        assertCorrect("Es tem que la radioactivitat afecti la poblaci? local");
        assertCorrect("Despr?s de tot aix? es va t?mer la possibilitat");
        assertCorrect("probablement es vagi a destil?lar l'etanol");
        assertCorrect(", es podia anar a Madrid per aconseguir en Celebi");
        assertCorrect("Els soldats es preparen per a marxar a la guerra.");
        assertCorrect("Tu et prepares per marxar a la guerra.");
        assertCorrect("i que es temia que s'aconsegu?s el nombre previst.");
        assertCorrect("Des del principi es temia el pitjor");
        assertCorrect("La primera muntanya que es va pujar per motius purament esportius,");
        assertCorrect("Quan el so era via fora, s'anava a guerrejar fora de la terra.");
        assertCorrect("els algorismes, de manera que s'evita caure");
        assertCorrect("En acabar l'assalt, ?s com? que es pugi un banc");
        assertCorrect("Es va caure en la provocaci?.");
        assertCorrect("Abans d'aix? ja s'havien pujat muntanyes,");
        assertCorrect("a una representaci? de La Passi? no nom?s s'hi va a veure un espectacle sumptu?s");
        assertCorrect("A escola no s'hi va a plorar.");
        assertCorrect("A escola no es va a jugar.");
        assertCorrect("A escola no es va a plorar.");
        assertCorrect("Al nostre pis de la Torre es pujava per aquella llarga escala");
        assertCorrect("Joan no es va a jugar la feina.");
        assertCorrect("I aquella flaire que em pujava al cap");
        assertCorrect("el que no s'olora, el que no es tasta");
        // errors:
        assertIncorrect("Ells s'han crescut molt");
        assertIncorrect("Em vaig cr?ixer davant les dificultats");
        assertIncorrect("Joan s'ha crescut molt");
        assertIncorrect("Joana s'ha crescut molt");
        assertIncorrect("Ada Mart?nez s'ha crescut molt");
        assertIncorrect("Ada Colau s'ha crescut molt");
        assertIncorrect("Ha arribat l'hora de saltar-se la legalitat.");
        assertIncorrect("Delia per menjar-ne.");
        assertIncorrect("Ells es volen dur les ?nimes a l'Infram?n");
        assertIncorrect("Joan es va portar el carret?");
        assertIncorrect("en aquesta vida ens portem moltes sorpreses");
        assertIncorrect("Ens hem portat massa material al campament");
        assertIncorrect("Hem de dur-nos tot aix?.");
        assertIncorrect("L'has fet tornar-se vermell.");
        assertIncorrect("El fan agenollar-se.");
        assertIncorrect("Fes-lo agenollar-se.");
        assertIncorrect("Deixa'm agenollar-me.");
        assertIncorrect("l'havia fet ufanejar-se obertament");
        assertIncorrect("un dels pocs moviments que poden fer ?s intentar pujar-se al carro de la indignaci?.");
        assertIncorrect("?s intentar pujar-se al carro de la indignaci?.");
        assertIncorrect("Pujar-se al carro de la indignaci?.");
        assertIncorrect("Pujar-vos al carro de la indignaci?.");
        assertIncorrect("se li va caure la cara de vergonya");
        assertIncorrect("se'ns va caure la cara de vergonya");
        assertIncorrect("A mi se'm va caure la cara de vergonya");
        assertIncorrect("Joan no es va a l'escola");
        assertIncorrect("que el proc?s no se'ns vagi de les mans");
        assertIncorrect("Ho volen per a anar-se de la zona");
        assertIncorrect("Ho volen per anar-se de la zona");
        assertIncorrect("Ho desitgen per anar-se de la zona");
        assertIncorrect("els grups que es van caure del cartell");
        assertIncorrect("el nen que es va caure al pou");// --> Es pot tractar diferent: caure / anar

        assertCorrect("el dia que es va anar a la ciutat");
        // assertIncorrect("el dia que es va anar a la ciutat");
        assertIncorrect("tot l'auditori es call?");
        assertIncorrect("les gotes que es van caure fora");
        assertIncorrect("Ells s'han baixat del tren.");
        assertIncorrect("Ximo Puig i Rubalcaba no s'han baixat del cotxe oficial des del 79.");
        assertIncorrect("Se'ns va callar.");
        assertIncorrect("Tothom es va callar.");
        assertIncorrect("Els nens van poder-se caure");
        assertIncorrect("Aleshores ell es va anar a estudiar a Barcelona");// -->va anar a fer introspecci? :-)

        assertIncorrect("Joan es va anar a estudiar a Barcelona.");
        assertIncorrect("se'ns va anar la m?");
        assertIncorrect("A Joan se li va anar la m?");
        assertIncorrect("Al pare se li va anar la m?");
        assertIncorrect("Escriu que quan era mosso ?se li anaven els ulls?");
        assertIncorrect("Es van caure en la trampa.");
        assertIncorrect("Aleshores es van anar a la ciutat a presentar una queixa.");
        assertIncorrect("Va entrar l'avi que pujava del taller i es va seure.");
        // assertIncorrect("Aleshores es va anar a la ciutat a presentar una queixa.");
        // assertIncorrect("quan es pugen, permeten canviar de feina.");
        assertIncorrect("havent queixat");
        assertIncorrect("haver queixat");
        assertIncorrect("les membranes s'han anat fabricat amb materials sint?tics");
        assertIncorrect("s'han anat fabricat amb materials sint?tics");
        assertIncorrect("Holmes i Watson s'han anat d'acampada");
        assertIncorrect("L'independentisme s'ha anat a Brussel?les!");
        assertIncorrect("El seu marit s'ha anat a la Xina per negocios");
        assertIncorrect("L'home es marx? de seguida");
        assertIncorrect("L'home s'an? de seguida");
        assertIncorrect("A Joan se li va caure la cara de vergonya");
        assertIncorrect("El nen es cau");
        assertIncorrect("El nen se li cau");
        assertIncorrect("A la nena se li caigueren les arracades");
        assertIncorrect("El nen s'ha de caure");
        assertIncorrect("El nen pot caure's");
        assertIncorrect("Calleu-vos");
        // assertIncorrect("Es puj? al cel"); ->indecidible
        assertIncorrect("El berenar es puj? al cel");
        assertIncorrect("Va baixar-se del cotxe en marxa.");
        assertIncorrect("comencen queixant");
        assertIncorrect("comenceu a queixar-nos");
        assertIncorrect("et puc queixar");
        assertIncorrect("en teniu prou amb queixar");
        assertIncorrect("en podem queixar");
        assertIncorrect("et queixa");
        assertIncorrect("em va queixant");
        assertIncorrect("li va queixar");
        assertIncorrect("hem d'emportar-t'hi");
        assertIncorrect("heu de poder-te queixar");
        assertIncorrect("m'has de poder queixar");
        assertIncorrect("havent queixat");
        assertIncorrect("haver queixat");
        assertIncorrect("no es vam poder emportar");
        assertIncorrect("no has de poder-vos queixar");
        assertIncorrect("no has de queixar-ne");
        assertIncorrect("no podeu deixar de queixar-ne");
        assertIncorrect("no li has de queixar");
        assertIncorrect("no em podeu queixar");
        assertIncorrect("pareu de queixar-se'n");
        assertIncorrect("podent abstenir");
        assertIncorrect("poder queixar");
        assertIncorrect("podeu queixar");
        assertIncorrect("queixa'n");
        assertIncorrect("queixant");
        assertIncorrect("queixar");
        assertIncorrect("queixeu-se'n");
        assertIncorrect("de n'ha queixat");
        assertIncorrect("me li ha queixat");
        assertIncorrect("te li queixa");
        assertIncorrect("us li va queixar");
        assertIncorrect("va decidir su?cidar-me");
        assertIncorrect("va queixant");
        assertIncorrect("va queixar");
        assertIncorrect("va queixar-li");
        assertIncorrect("anem a aferrissar");
    }
}

