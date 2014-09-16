package stamboom.domain;

import java.util.*;

public class Administratie {

    //************************datavelden*************************************
    private int nextGezinsNr;
    private int nextPersNr;
    private final List<Persoon> personen;
    private final List<Gezin> gezinnen;

    //***********************constructoren***********************************
    /**
     * er wordt een administratie gecreeerd met 0 personen en dus 0 gezinnen
     * personen en gezinnen die in de toekomst zullen worden gecreeerd, worden
     * elk opvolgend genummerd vanaf 1
     */
    public Administratie() {
        //todo opgave 1
        nextGezinsNr=1;
        nextPersNr=1;
        personen = new ArrayList<Persoon>();
        gezinnen = new ArrayList<Gezin>();
        //personen = null;
        //gezinnen = null;
    }

    //**********************methoden****************************************
    /**
     * er wordt een persoon met een gegeven geslacht, met als voornamen vnamen,
     * achternaam anaam, tussenvoegsel tvoegsel, geboortedatum gebdat,
     * geboorteplaats gebplaats en een gegeven ouderlijk gezin gecreeerd; de persoon
     * krijgt een uniek nummer toegewezen de persoon is voortaan ook bij het
     * ouderlijk gezin bekend. Voor de voornamen, achternaam en gebplaats geldt
     * dat de eerste letter naar een hoofdletter en de resterende letters naar
     * een kleine letter zijn geconverteerd; het tussenvoegsel is zo nodig in
     * zijn geheel geconverteerd naar kleine letters; overbodige spaties zijn 
     * verwijderd
     *
     * @param geslacht
     * @param vnamen vnamen.length>0; alle strings zijn niet leeg
     * @param anaam niet leeg
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats niet leeg
     * @param ouderlijkGezin mag de waarde null (=onbekend) hebben
     *
     * @return als de persoon al bekend was (op basis van combinatie van getNaam(),
     * geboorteplaats en geboortedatum), wordt er null geretourneerd, anders de 
     * nieuwe persoon
     */
    public Persoon addPersoon(Geslacht geslacht, String[] vnamen, String anaam,
            String tvoegsel, Calendar gebdat,
            String gebplaats, Gezin ouderlijkGezin) {

        if (vnamen.length == 0) {
            throw new IllegalArgumentException("ten minst 1 voornaam");
        }
        for (String voornaam : vnamen) {
            if (voornaam.trim().isEmpty()) {
                throw new IllegalArgumentException("lege voornaam is niet toegestaan");
            }
        }

        if (anaam.trim().isEmpty()) {
            throw new IllegalArgumentException("lege achternaam is niet toegestaan");
        }

        if (gebplaats.trim().isEmpty()) {
            throw new IllegalArgumentException("lege geboorteplaats is niet toegestaan");
        }

        //todo opgave 1
        List<String> voornamenVal = new ArrayList<String>();
        String[] voornamenRes;
        String achternaamVal = null;
        String tussenvoegselVal = null;
        String geboorteplaatsVal = null;
        Persoon newPersoon = null;
        int nr = 0;
        
        for (int i = 0; i < vnamen.length; i++)
        {
            voornamenVal.add(CapitalizeString(vnamen[i]));
        }
        
        tussenvoegselVal = tvoegsel.toLowerCase();
        achternaamVal = CapitalizeString(anaam);
        geboorteplaatsVal = CapitalizeString(gebplaats);
        voornamenRes = voornamenVal.toArray(new String[0]);
        
        if(!this.personen.isEmpty())
        {
            nr = this.personen.get(this.personen.size()-1).getNr() + 1;
        }
        
        for (int i = 0; i < this.personen.size(); i++)
        {
            Persoon currentPerson = this.personen.get(i);
            if(currentPerson.getAchternaam() == achternaamVal &&
               currentPerson.getVoornamen() == this.formatVoornamen(voornamenRes) &&
               currentPerson.getGebPlaats() == geboorteplaatsVal &&
               currentPerson.getGebDat() == gebdat)
            {
                return null;
            }
        }
        
        newPersoon = new Persoon(nextPersNr, voornamenRes, achternaamVal, tussenvoegselVal, gebdat, geboorteplaatsVal, geslacht, ouderlijkGezin);
        
        this.personen.add(newPersoon);
        nextPersNr += 1;
        return newPersoon;
    }
    
    public String CapitalizeString(String str)
    {
        String tempVNaam = str.toLowerCase();
        tempVNaam = tempVNaam.substring(0, 1).toUpperCase() + tempVNaam.substring(1, tempVNaam.length());
        
        while (tempVNaam.contains("  "))
        {
            tempVNaam.replace("  ", " ");
        }
        
        if(tempVNaam.startsWith(" "))
        {
            tempVNaam = tempVNaam.substring(1, tempVNaam.length());
        }
        
        if(tempVNaam.endsWith(" "))
        {
            tempVNaam = tempVNaam.substring(0, tempVNaam.length()-1);
        }
        return tempVNaam;
    }

    /**
     * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin met
     * ouder1 en ouder2 als ouders gecreeerd; de huwelijks- en scheidingsdatum
     * zijn onbekend (null); het gezin krijgt een uniek nummer toegewezen; dit
     * gezin wordt ook bij de afzonderlijke ouders geregistreerd;
     *
     * @param ouder1
     * @param ouder2 mag null zijn
     *
     * @return null als ouder1 = ouder2 of als de volgende voorwaarden worden
     * overtreden: 1) een van de ouders is op dit moment getrouwd 2) het koppel
     * uit een ongehuwd gezin kan niet tegelijkertijd als koppel bij een ander
     * ongehuwd gezin betrokken zijn anders het gewenste gezin
     */
    public Gezin addOngehuwdGezin(Persoon ouder1, Persoon ouder2) {
        if (ouder1 == ouder2) {
            return null;
        }

        Calendar nu = Calendar.getInstance();
        if (ouder1.isGetrouwdOp(nu) || (ouder2 != null
                && ouder2.isGetrouwdOp(nu))
                || ongehuwdGezinBestaat(ouder1, ouder2)) {
            return null;
        }

        Gezin gezin = new Gezin(nextGezinsNr, ouder1, ouder2);
        nextGezinsNr++;
        gezinnen.add(gezin);

        ouder1.wordtOuderIn(gezin);
        if (ouder2 != null) {
            ouder2.wordtOuderIn(gezin);
        }

        return gezin;
    }

    /**
     * Als het ouderlijk gezin van persoon nog onbekend is dan wordt persoon een
     * kind van ouderlijkGezin en tevens wordt persoon als kind in dat gezin
     * geregistreerd; <br>
     * Als de ouders bij aanroep al bekend zijn, verandert er
     * niets
     *
     * @param persoon
     * @param ouderlijkGezin
     */
    public void setOuders(Persoon persoon, Gezin ouderlijkGezin) {
        persoon.setOuders(ouderlijkGezin);
    }

    /**
     * als de ouders van dit gezin gehuwd zijn en nog niet gescheiden en datum
     * na de huwelijksdatum ligt, wordt dit de scheidingsdatum. Anders gebeurt
     * er niets.
     *
     * @param gezin
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    public boolean setScheiding(Gezin gezin, Calendar datum) {
        return gezin.setScheiding(datum);
    }

    /**
     * registreert het huwelijk, mits gezin nog geen huwelijk is en beide ouders
     * op deze datum mogen trouwen (pas op: ook de toekomst kan hierbij een rol
     * spelen omdat toekomstige gezinnen eerder zijn geregisteerd)
     *
     * @param gezin
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Gezin gezin, Calendar datum) {
        return gezin.setHuwelijk(datum);
    }

    /**
     *
     * @param ouder1
     * @param ouder2
     * @return true als dit koppel (ouder1,ouder2) al een ongehuwd gezin vormt
     */
    boolean ongehuwdGezinBestaat(Persoon ouder1, Persoon ouder2) {
        return ouder1.heeftOngehuwdGezinMet(ouder2) != null;
    }

    /**
     * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het huwelijk
     * voltrokken, anders wordt er zo mogelijk (zie return) een (kinderloos)
     * gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd; de
     * scheidingsdatum is onbekend (null); het gezin krijgt een uniek nummer
     * toegewezen; dit gezin wordt ook bij de afzonderlijke ouders
     * geregistreerd;
     *
     * @param ouder1
     * @param ouder2
     * @param huwdatum
     * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
     * anders het gehuwde gezin
     */
    public Gezin addHuwelijk(Persoon ouder1, Persoon ouder2, Calendar huwdatum) {
        //todo opgave 1
        Gezin newFamily = null;
        for(Persoon p : personen){
            if(p.equals(ouder1))
            {
                if(ouder1.heeftOngehuwdGezinMet(ouder2) != null)
                        ouder1.heeftOngehuwdGezinMet(ouder2).isHuwelijkOp(huwdatum);
                else
                {
                    newFamily = new Gezin(this.nextGezinsNr, ouder1, ouder2);
                    newFamily.setHuwelijk(huwdatum);
                    
                    ouder1.wordtOuderIn(newFamily);
                    ouder2.wordtOuderIn(newFamily);
                    
                    this.gezinnen.add(newFamily);
                    this.nextGezinsNr++;
                }
            }
        }
        /*
        for(int i = 0; 0 < this.gezinnen.size(); i++)
        {
            Persoon currParent1 = this.gezinnen.get(i).getOuder1();
            Persoon currParent2 = this.gezinnen.get(i).getOuder2();
            
            if(currParent1 == ouder1 || currParent1 == ouder2)
                return null;
            if(currParent2 == ouder1 || currParent2 == ouder2)
                return null;
            if(ouder1 == ouder2)
                return null;
        }
        
        newFamily = new Gezin(this.nextGezinsNr, ouder1, ouder2);
        newFamily.setHuwelijk(huwdatum);
        this.nextGezinsNr++;
        
        this.gezinnen.add(newFamily);
        */
        return newFamily;
    }

    /**
     *
     * @return het aantal geregistreerde personen
     */
    public int aantalGeregistreerdePersonen() {
        return nextPersNr - 1;
    }

    /**
     *
     * @return het aantal geregistreerde gezinnen
     */
    public int aantalGeregistreerdeGezinnen() {
        return nextGezinsNr - 1;
    }

    /**
     *
     * @param nr
     * @return de persoon met nummer nr, als die niet bekend is wordt er null
     * geretourneerd
     */
    public Persoon getPersoon(int nr) {
        for (Persoon personen1 : this.personen) {
            if (personen1.getNr() == nr) {
                return personen1;
            }
        }
        return null;
    }

    /**
     * @param achternaam
     * @return alle personen met een achternaam gelijk aan de meegegeven
     * achternaam (ongeacht hoofd- en kleine letters)
     */
    public ArrayList<Persoon> getPersonenMetAchternaam(String achternaam) {
        //todo opgave 1
         ArrayList<Persoon> getPersonWithLastName = null;
            List<Persoon> personList;     
            personList = getPersonen();
        
        for (Persoon persoon: personList) {
            if (persoon.getAchternaam().equals(achternaam)) {
                getPersonWithLastName.add(persoon);

            }
            else
            {
                
            }
        }
        
        return getPersonWithLastName;
    }

    /**
     *
     * @return de geregistreerde personen
     */
    public List<Persoon> getPersonen() {
        // todo opgave 1
        return this.personen;
    }

    /**
     *
     * @param vnamen
     * @param anaam
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats
     * @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
     * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
     * anders null
     */
    public Persoon getPersoon(String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats) {
        //todo opgave 1
        Persoon retPerson = null;
        
        for (int i = 0; i < this.personen.size(); i++)
        {
            Persoon currPerson = this.personen.get(i);
            if( currPerson.getAchternaam() == anaam &&
                    currPerson.getGebDat() == gebdat &&
                    currPerson.getTussenvoegsel() == tvoegsel &&
                    currPerson.getGebPlaats() == gebplaats &&
                    currPerson.getVoornamen() == formatVoornamen(vnamen))
            {
                return currPerson;
            }
                    
        }
        
        return null;
    }
    
    public String formatVoornamen(String[] s2) {
        StringBuilder init = new StringBuilder();
        for (String s : s2) {
            init.append(s).append(' ');
        }
        return init.toString().trim();
    }

    /**
     *
     * @return de geregistreerde gezinnen
     */
    public List<Gezin> getGezinnen() {
        return this.gezinnen;
    }

    /**
     *
     * @param gezinsNr
     * @return het gezin met nummer nr. Als dat niet bekend is wordt er null
     * geretourneerd
     */
    public Gezin getGezin(int gezinsNr) {
        // aanname: er worden geen gezinnen verwijderd
        if (1 <= gezinsNr && 1 <= gezinnen.size()) {
            return gezinnen.get(gezinsNr - 1);
        }
        return null;
    }
}
