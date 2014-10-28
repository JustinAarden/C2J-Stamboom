/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import stamboom.domain.Administratie;
import stamboom.storage.IStorageMediator;
import stamboom.storage.DatabaseMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */

    public void serialize(File bestand) throws IOException {
        //todo opgave 2
        try{
            ObjectOutputStream out;
            FileOutputStream stream;
            stream = new FileOutputStream(bestand);
            out = new ObjectOutputStream(stream);
            out.writeObject(admin);
            
            out.close();
            stream.close();
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException {
        //todo opgave 2
        try{
            ObjectInputStream in;
            FileInputStream stream;
            stream = new FileInputStream(bestand);
            in = new ObjectInputStream(stream);
            Administratie adminObject;
            adminObject = (Administratie) in.readObject();
            
            admin = adminObject;
            
            
            in.close();
            stream.close();
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
        catch(ClassNotFoundException exc)
        {
            exc.printStackTrace();
        }
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {

            storageMediator = new DatabaseMediator();

    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        initDatabaseMedium();
         storageMediator.save(admin);
    }

}
