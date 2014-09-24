package stamboom.console;

public enum MenuItem {

    EXIT("exit"),
    NEW_PERS("registreer persoon"),
    NEW_ONGEHUWD_GEZIN("registreer ongehuwd gezin"),
    NEW_HUWELIJK("registreer huwelijk"),
    SCHEIDING("registreer scheiding"),
    SHOW_PERS("toon gegevens persoon"),
    SHOW_GEZIN("toon gegevens gezin"),
    GET_ADMIN("ophalen van den administratie uit een bestand"),
    SET_ADMIN("bewaren van de administratie in een bestand"),
    REQ_STAM_PERSOON("opvragen van stamboomgegevens voor een persoon");
    
    private final String omschr;

    private MenuItem(String omschr) {
        this.omschr = omschr;
    }

    /**
     * @return  the omschr
     * @uml.property  name="omschr"
     */
    public String getOmschr() {
        return omschr;
    }
}
