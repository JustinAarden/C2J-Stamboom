/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import stamboom.domain.Persoon;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!
public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn = null;
        public static DatabaseMediator db;


//   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
//   static final String DB_URL = "jdbc:mysql://localhost/STUDENTS";
//
//   //  Database credentials
//   static final String USER = "root";
//   static final String PASS = "";

    // statements allow to issue SQL queries to the database
    //statement = conn.createStatement();
    public void openConnection() {
                String dbHost = "jdbc:mysql://localhost/stamboom"; 
        String dbUsername = "root";
        String dbPassword = "";
        String dbName = "stamboom";
        String driver = "com.mysql.jdbc.Driver";
         try{
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(dbHost,dbUsername,dbPassword);
            
            
        }
        catch(Exception sqle){
            sqle.printStackTrace();
        }
    }
    
    public static DatabaseMediator getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        if (db == null) {
            db = new DatabaseMediator();  
        }
        //Datasource ds = new DataSource();
        return null;
    }
    
    @Override
    public Administratie load() throws IOException {
        this.openConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String query = "SELECT voornamen FROM personen WHERE persoonsNr = 1";
            rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                String voornaam = rs.getString("voornamen");
                System.out.println(voornaam);
            }
            this.closeConnection();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        this.openConnection();
        try {
            
            Persoon pers = admin.getPersoon(1);
            PreparedStatement pstatement = conn.prepareStatement("insert into personen (persoonNr,achternaam,voornamen,tussenvoegsel,geboortedatum,geboorteplaats,geslacht,ouders) values (?,?,?,?,?,?,?,?)");
            pstatement.setString(1, ""); //lege string ivm met Auto Increment EVT normaal persoons nummer meegeven gegeneerd door de applicatie?
            pstatement.setString(2, pers.getAchternaam());
            pstatement.setString(3, pers.getVoornamen());
            pstatement.setString(4, pers.getTussenvoegsel());
            pstatement.setString(5, pers.getGebDat().toString());
            pstatement.setString(6, pers.getGebPlaats());
            pstatement.setString(7, pers.getGeslacht().toString());
            pstatement.setString(8, ""); //leeg als test

            this.closeConnection();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public final boolean configure(Properties props) {
        this.props = props;

        try {
            initConnection();
            return isCorrectlyConfigured();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
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

    private void initConnection() throws SQLException {
        //opgave 4
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
