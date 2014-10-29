/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters, ryan
 */
public class StamboomFXController extends StamboomController implements Initializable {

    private ObservableList<Persoon> personen;
    private ObservableList<Gezin> gezinnen;
    private ObservableList<Persoon> kinderen;
    private ObservableList<Gezin> alsOuderBetrokkenIn;
    
    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;
    
    //GEZIN
    @FXML ComboBox cbGezinnen;
    @FXML TextField tfGezinNr;
    @FXML TextField tfOuder1;
    @FXML TextField tfOuder2;
    @FXML TextField tfHuwelijk;
    @FXML TextField tfScheiding;
    @FXML ListView lvKinderen;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //INVOER PERSOON
    @FXML TextField tfVoornamenInvoer;
    @FXML TextField tfTussenvoegselInvoer;
    @FXML TextField tfAchternaamInvoer;
    @FXML ComboBox cbGeslachtInvoer;
    @FXML TextField tfGeboortedatumInvoer;
    @FXML TextField tfGeboorteplaatsInvoer;
    @FXML ComboBox cbOuderlijkGezinInvoer;
    @FXML Button btOKPersoonInvoer;
    @FXML Button btCancelPersoonInvoer;

    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
    }

    private void initComboboxes() {
        //todo opgave 3 
        
        personen = FXCollections.observableArrayList(this.getAdministratie().getPersonen());
        gezinnen = FXCollections.observableArrayList(this.getAdministratie().getGezinnen());
        
        this.cbGezinnen.setItems(this.getGezinnen());
        this.cbPersonen.setItems(this.getPersonen());
        this.cbOuderlijkGezinInvoer.setItems(this.getGezinnen());
        this.cbOuderlijkGezin.setItems(this.getGezinnen());
        this.cbOuder1Invoer.setItems(this.getPersonen());
        this.cbOuder2Invoer.setItems(this.getPersonen());
        this.cbGeslachtInvoer.setItems(FXCollections.observableArrayList(Geslacht.values()));
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }
            //todo opgave 3
            
            this.alsOuderBetrokkenIn = FXCollections.observableArrayList(persoon.getAlsOuderBetrokkenIn());
            
            ArrayList<Persoon> pList = new ArrayList<Persoon>();
            for(Gezin g : persoon.getAlsOuderBetrokkenIn())
            {
                pList.addAll(g.getKinderen());
            }
            
            this.kinderen = FXCollections.observableArrayList(pList);
            
            lvAlsOuderBetrokkenBij.setItems(this.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        this.personen.remove(p);
        Gezin g = getAdministratie().getGezin(ouderlijkGezin.getNr());
        this.gezinnen.remove(g);
        getAdministratie().setOuders(p, ouderlijkGezin);
        g = getAdministratie().getGezin(ouderlijkGezin.getNr());
        this.gezinnen.add(g);
        p = getAdministratie().getPersoon(nr);
        this.personen.add(p);
    }

    public void selectGezin(Event evt) {
        // todo opgave 3
        showGezin((Gezin) this.cbGezinnen.getSelectionModel().getSelectedItem());
    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        if (gezin == null) {
            clearTabGezin();
        } else {
            tfGezinNr.setText(gezin.getNr() + "");
            tfOuder1.setText(gezin.getOuder1().standaardgegevens());
            if(gezin.getOuder2() != null)
                tfOuder2.setText(gezin.getOuder2().standaardgegevens());
            if(gezin.getHuwelijksdatum() != null)
                tfHuwelijk.setText(StringUtilities.datumString(gezin.getHuwelijksdatum()));
            if(gezin.getScheidingsdatum() != null)
                tfScheiding.setText(StringUtilities.datumString(gezin.getScheidingsdatum()));
            
            this.kinderen = FXCollections.observableArrayList(gezin.getKinderen());
            
            lvKinderen.setItems(this.getKinderen());
        }
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        if(this.tfHuwelijk.getText().isEmpty())
        {
            Calendar c = Calendar.getInstance();
            c.clear();

            c = StringUtilities.datum(this.tfScheiding.getText());

            int i = Integer.parseInt(this.tfGezinNr.getText());

            Gezin g = this.getAdministratie().getGezin(i);
            this.gezinnen.remove(g);
            this.getAdministratie().setHuwelijk(g, c);
            g = this.getAdministratie().getGezin(i);
            this.gezinnen.add(g);
        }
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3
        if(this.tfScheiding.getText().isEmpty())
        {
            Calendar c = Calendar.getInstance();
            c.clear();

            c = StringUtilities.datum(this.tfHuwelijk.getText());

            int i = Integer.parseInt(this.tfGezinNr.getText());

            Gezin g = this.getAdministratie().getGezin(i);
            this.gezinnen.remove(g);
            this.getAdministratie().setScheiding(g, c);
            g = this.getAdministratie().getGezin(i);
            this.gezinnen.add(g);
        }
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        this.clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
        String voornamen = this.tfVoornamenInvoer.getText();
        String achternaam = this.tfAchternaamInvoer.getText();
        String tussenvoegsels = this.tfTussenvoegselInvoer.getText();
        Geslacht geslacht = (Geslacht) this.cbGeslachtInvoer.getSelectionModel().getSelectedItem(); //Geslacht.valueOf(this.cbGeslachtInvoer.getSelectionModel().getSelectedItem().toString());
        Calendar c = Calendar.getInstance();
        c.clear();
        c = StringUtilities.datum(this.tfGeboortedatumInvoer.getText());
        String geboorteplaats = this.tfGeboorteplaatsInvoer.getText();
        Gezin gezin;
        if(this.cbOuderlijkGezinInvoer.getSelectionModel().isEmpty())
            gezin = null;
        else
            gezin = (Gezin) this.cbOuderlijkGezinInvoer.getSelectionModel().getSelectedItem();
        
        this.personen.add(getAdministratie().addPersoon(geslacht, voornamen.split(" "), achternaam, tussenvoegsels, c, geboorteplaats, gezin));
        this.clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if(scheidingsdatum != null)
                        getAdministratie().setScheiding(g, scheidingsdatum);
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }
        
        if(g!=null)
        {
            this.gezinnen.add(g);
        }

        this.clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        this.clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // todo opgave 3
        showDialog("Stamboom", getAdministratie().getPersoon(Integer.parseInt(this.tfPersoonNr.getText())).stamboomAlsString());
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        // todo opgave 3
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Haal Stamboom op");
        File f = fileChooser.showOpenDialog(new Stage());
        try
        {
            this.deserialize(f);
            
            this.initComboboxes();
            
        } catch(IOException exc) {
            exc.fillInStackTrace();
        }
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sla Stamboom op");
        File f = fileChooser.showSaveDialog(new Stage());
        try
        {
        
            this.serialize(f);
        } catch(Exception exc) {
            exc.fillInStackTrace();
        }
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        //todo opgave 3
        tfVoornamenInvoer.clear();
        tfTussenvoegselInvoer.clear();
        tfAchternaamInvoer.clear();
        cbGeslachtInvoer.getSelectionModel().clearSelection();
        tfGeboortedatumInvoer.clear();
        tfGeboorteplaatsInvoer.clear();
        cbOuderlijkGezinInvoer.getSelectionModel().clearSelection();
    }

    
    private void clearTabGezinInvoer() {
        //todo opgave 3
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        // todo opgave 3
        cbGezinnen.getSelectionModel().clearSelection();
        tfGezinNr.clear();
        tfOuder1.clear();
        tfOuder2.clear();
        tfHuwelijk.clear();
        tfScheiding.clear();
        lvKinderen.setItems(FXCollections.emptyObservableList());
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }
    
    public ObservableList<Persoon> getPersonen()
    {
        return (ObservableList<Persoon>) FXCollections.unmodifiableObservableList(personen);
    }
    public ObservableList<Persoon> getKinderen()
    {
        return (ObservableList<Persoon>) FXCollections.unmodifiableObservableList(kinderen);
    }
    
    public ObservableList<Gezin> getAlsOuderBetrokkenIn()
    {
        return (ObservableList<Gezin>) FXCollections.unmodifiableObservableList(alsOuderBetrokkenIn);
    }
    public ObservableList<Gezin> getGezinnen()
    {
        return (ObservableList<Gezin>) FXCollections.unmodifiableObservableList(gezinnen);
    }
}
