/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!
public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn = null;
    public static DatabaseMediator db;
    private Administratie adminLoader = new Administratie();
    private ArrayList<PersonInFamily> personenmetgezin = new ArrayList<PersonInFamily>();
    private StringUtilities util;

    public void initConnection() {
        String dbHost = "jdbc:mysql://localhost/stamboom";
        String dbUsername = "root";
        String dbPassword = "";
        String dbName = "stamboom";
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(dbHost, dbUsername, dbPassword);

        } catch (Exception sqle) {
            sqle.printStackTrace();
        }
    }

    public static DatabaseMediator getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (db == null) {
            db = new DatabaseMediator();
        }
        //Datasource ds = new DataSource();
        return null;
    }

    private void PersoonInGezin(int persoonNr, int gezinNr) {
        this.personenmetgezin.add(new PersonInFamily(persoonNr, gezinNr));
    }

    @Override
    public Administratie load() throws IOException {
        this.initConnection();
        try {

            ArrayList<Persoon> pers = new ArrayList<Persoon>();
            //Persoon pers = null;
            PreparedStatement p = conn.prepareStatement("Select * FROM personen");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Calendar c = Calendar.getInstance();
                String[] vnamen = rs.getString("voornamen").split(" ");
                String achternaam = rs.getString("achternaam");
                String tussenvoegsel = rs.getString("tussenvoegsel");
                java.sql.Date myDate = rs.getDate("geboortedatum");
                String geboorteplaats = rs.getString("geboorteplaats");
                c.setTime(myDate);
                Geslacht geslacht;

                if (rs.getString("geslacht").equals("MAN")) {
                    geslacht = Geslacht.MAN;
                } else {
                    geslacht = Geslacht.VROUW;
                }

                adminLoader.addPersoon(geslacht, vnamen, achternaam, tussenvoegsel, c, geboorteplaats, null);

                this.PersoonInGezin(rs.getInt("persoonsNr"), rs.getInt("ouders"));

            }
            Persoon[] pList = null;

            adminLoader.getPersonen().toArray(pList);

            Gezin[] gList = this.getGezinnen(pList);

            for (Persoon persoon : this.adminLoader.getPersonen()) {
                for (PersonInFamily fim : this.personenmetgezin) {
                    for (Gezin gezin : gList) {
                        if (persoon.getNr() == fim.PERSONNR && gezin.getNr() == fim.FAMILYNR) {
                            this.adminLoader.setOuders(persoon, gezin);
                        }
                    }
                }
                //todo
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {

        }
        return this.adminLoader;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        this.initConnection();
        try {

            for (Persoon pers : admin.getPersonen()) {
                //Persoon pers = admin.getPersoon();
                Calendar c = Calendar.getInstance();
                c.clear();
                c.setTime(pers.getGebDat().getTime());
                PreparedStatement pstatement = conn.prepareStatement(
                        "insert into personen ("
                        + "persoonsNr,"
                        + "achternaam,"
                        + "voornamen,"
                        + "tussenvoegsel,"
                        + "geboortedatum,"
                        + "geboorteplaats,"
                        + "geslacht,"
                        + "ouders)"
                        + " VALUES "
                        + "(?,?,?,?,?,?,?,?) "//(1,2,3,4,5,6,7,8)
                        + "ON DUPLICATE KEY UPDATE "
                        + "achternaam=?," //9
                        + "voornamen=?," //10
                        + "tussenvoegsel=?," //11
                        + "geboortedatum=?," //12
                        + "geboorteplaats=?," //13
                        + "geslacht=?," //14
                        + "ouders=?");          //15

                pstatement.setInt(1, pers.getNr());
                pstatement.setString(2, pers.getAchternaam());
                pstatement.setString(3, pers.getVoornamen());
                pstatement.setString(4, pers.getTussenvoegsel());
                pstatement.setDate(5, new java.sql.Date(pers.getGebDat().getTimeInMillis()));
                pstatement.setString(6, pers.getGebPlaats());
                pstatement.setString(7, pers.getGeslacht().toString());

                if (pers.getOuderlijkGezin() != null) {
                    for (Gezin gezin : admin.getGezinnen()) {
                        if (gezin.getOuder1().getNr() == pers.getNr() && gezin.getOuder1().getNr() == pers.getNr()) {
                            pstatement.setInt(8, gezin.getNr());
                        }
                    }
                } else {
                    pstatement.setString(8, null);
                }

                // Wanneer persoon al bestaat!
                pstatement.setString(9, pers.getAchternaam());
                pstatement.setString(10, pers.getVoornamen());
                pstatement.setString(11, pers.getTussenvoegsel());
                pstatement.setDate(12, new java.sql.Date(pers.getGebDat().getTimeInMillis()));
                pstatement.setString(13, pers.getGebPlaats());
                pstatement.setString(14, pers.getGeslacht().toString());
                if (pers.getOuderlijkGezin() != null) {

                    for (Gezin gezin : admin.getGezinnen()) {
                        if (gezin.getOuder1().getNr() == pers.getNr() && gezin.getOuder1().getNr() == pers.getNr()) {
                            pstatement.setInt(15, gezin.getNr());
                        }
                    }
                } else {
                    pstatement.setString(15, null);
                }

                pstatement.execute();

            }
            for (Gezin gezin : admin.getGezinnen()) {
                PreparedStatement pstatement1 = conn.prepareStatement(
                        "insert into gezinnen ("
                        + "gezinsNr, "
                        + "ouders1,"
                        + "ouders2,"
                        + "huwelijksdatum,"
                        + "scheidingdatum)"
                        + " values"
                        + "(?,?,?,?,?)"
                        + "ON DUPLICATE KEY UPDATE "
                        + "ouders1=?,"
                        + "ouders2=?,"
                        + "huwelijksdatum=?,"
                        + "scheidingdatum=?");
                pstatement1.setInt(1, gezin.getNr());
                pstatement1.setInt(2, gezin.getOuder1().getNr());
                pstatement1.setInt(3, gezin.getOuder2().getNr());
                if (gezin.getHuwelijksdatum() != null) {
                    pstatement1.setDate(4, new java.sql.Date(gezin.getHuwelijksdatum().getTimeInMillis()));
                } else {
                    pstatement1.setString(4, null);
                }

                if (gezin.getScheidingsdatum() != null) {
                    pstatement1.setDate(5, new java.sql.Date(gezin.getScheidingsdatum().getTimeInMillis()));
                } else {
                    pstatement1.setString(5, null);
                }

                //Wanneer gezin al bestaat of wordt geupdate!
                pstatement1.setInt(6, gezin.getOuder1().getNr());
                pstatement1.setInt(7, gezin.getOuder2().getNr());
                if (gezin.getHuwelijksdatum() != null) {
                    pstatement1.setDate(8, new java.sql.Date(gezin.getHuwelijksdatum().getTimeInMillis()));
                } else {
                    pstatement1.setString(8, null);
                }

                if (gezin.getScheidingsdatum() != null) {
                    pstatement1.setDate(9, new java.sql.Date(gezin.getScheidingsdatum().getTimeInMillis()));
                } else {
                    pstatement1.setString(9, null);
                }

                pstatement1.execute();

            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveGezin(Administratie admin) throws IOException, SQLException {
        for (Gezin gezin : admin.getGezinnen()) {
            PreparedStatement pstatement1 = conn.prepareStatement(
                    "insert into gezinnen ("
                    + "gezinsNr, "
                    + "ouders1,"
                    + "ouders2,"
                    + "huwelijksdatum,"
                    + "scheidingdatum)"
                    + " values"
                    + "(?,?,?,?,?)"
                    + "ON DUPLICATE KEY UPDATE "
                    + "ouders1=?,"
                    + "ouders2=?,"
                    + "huwelijksdatum=?,"
                    + "scheidingdatum=?");
            pstatement1.setInt(1, gezin.getNr());
            pstatement1.setInt(2, gezin.getOuder1().getNr());
            pstatement1.setInt(3, gezin.getOuder2().getNr());
            pstatement1.setDate(4, new java.sql.Date(gezin.getHuwelijksdatum().getTimeInMillis()));
            pstatement1.setDate(5, new java.sql.Date(gezin.getScheidingsdatum().getTimeInMillis()));

            //Wanneer gezin al bestaat of wordt geupdate!
            pstatement1.setInt(6, gezin.getOuder1().getNr());
            pstatement1.setInt(7, gezin.getOuder2().getNr());
            pstatement1.setDate(8, new java.sql.Date(gezin.getHuwelijksdatum().getTimeInMillis()));
            pstatement1.setDate(9, new java.sql.Date(gezin.getScheidingsdatum().getTimeInMillis()));

            pstatement1.execute();

            this.closeConnection();
        }

    }

    @Override
    public final boolean configure(Properties props
    ) {
        this.props = props;

        initConnection();
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    public Gezin[] getGezinnen(Persoon[] pList) throws SQLException {
        ArrayList<Gezin> gezinnen = new ArrayList<Gezin>();

        PreparedStatement p = conn.prepareStatement("SELECT * FROM gezinnen");
        ResultSet rs = p.executeQuery();
        //gehuwd/gescheiden
        if (rs.getObject("huwelijkdatum") != null) {

            Calendar huwelijkdat = Calendar.getInstance();
            huwelijkdat.clear();
            huwelijkdat.setTime(rs.getDate("huwelijkdatum"));

            Boolean hasBeenDivorsed = false;

            Calendar scheidingdat = Calendar.getInstance();
            if (rs.getObject("scheidingdatum") != null) {
                scheidingdat.clear();
                scheidingdat.setTime(rs.getDate("scheidingdatum"));

                hasBeenDivorsed = true;
            }

            Persoon ouder1 = null;
            Persoon ouder2 = null;

            for (Persoon persoon : pList) {
                for (PersonInFamily pif : this.personenmetgezin) {
                    if (rs.getInt("gezinsNr") == pif.FAMILYNR && persoon.getNr() == pif.PERSONNR) {
                        if (pif.PERSONNR == rs.getInt("ouders1")) {
                            ouder1 = persoon;
                        } else if (pif.PERSONNR == rs.getInt("ouders2")) {
                            ouder2 = persoon;
                        }
                    }
                }
            }
            gezinnen.add(this.adminLoader.addHuwelijk(ouder1, ouder2, huwelijkdat));

            if (hasBeenDivorsed) {
                this.adminLoader.setScheiding(this.adminLoader.getGezin(rs.getInt("gezinsNr")), scheidingdat);
            }
        } //gescheiden
        else {
            Persoon ouder1 = null;
            Persoon ouder2 = null;

            for (Persoon persoon : pList) {
                for (PersonInFamily pif : this.personenmetgezin) {
                    if (rs.getInt("gezinsNr") == pif.FAMILYNR && persoon.getNr() == pif.PERSONNR) {
                        if (pif.PERSONNR == rs.getInt("ouders1")) {
                            ouder1 = persoon;
                        } else if (pif.PERSONNR == rs.getInt("ouders2")) {
                            ouder2 = persoon;
                        }
                    }
                }
            }

            gezinnen.add(this.adminLoader.addOngehuwdGezin(ouder1, ouder2));
        }

        return (Gezin[]) gezinnen.toArray();
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
