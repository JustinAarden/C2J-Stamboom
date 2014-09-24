/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

//import java.io.File;
//import java.io.IOException;
import java.io.*;
import java.util.Properties;
import stamboom.domain.Administratie;

public class SerializationMediator implements IStorageMediator {

    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        ObjectInputStream in = null;
        FileInputStream stream = null;
        Administratie adminObject = null;
        // todo opgave 2
        try{
            stream = new FileInputStream("stamboomadministratie.txt");
            in = new ObjectInputStream(stream);
            
            adminObject = (Administratie) in.readObject();
            
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
        catch(ClassNotFoundException exc)
        {
            exc.printStackTrace();
        }
        finally{
            if(in != null)
                in.close();
            if(stream != null)
                stream.close();
        }
        
        return adminObject;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }

        // todo opgave 2
        ObjectOutputStream out = null;
        FileOutputStream stream = null;
        try{
            stream = new FileOutputStream("stamboomadministratie.txt");
            out = new ObjectOutputStream(stream);
            out.writeObject(admin);
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        finally{
            if(out != null)
                out.close();
            if(stream != null)
                stream.close();
        }
    }

    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     *
     * @return true if config() contains at least a key "file" and the
     * corresponding value is a File-object, otherwise false
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (props.containsKey("file")) {
            return props.get("file") instanceof File;
        } else {
            return false;
        }
    }
}
