package sio.bulletin;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sio.bulletin.Model.Etudiant;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class BulletinController implements Initializable {
    DecimalFormat df;
    HashMap<String, HashMap<String, HashMap<String, ArrayList<Etudiant>>>> lesBulletins;
    @FXML
    private AnchorPane apBulletin;
    @FXML
    private ListView lvMatieres;
    @FXML
    private ListView lvDevoirs;
    @FXML
    private ComboBox cboTrimestres;
    @FXML
    private TextField txtNomEtudiant;
    @FXML
    private TextField txtNote;
    @FXML
    private Button btnValider;
    @FXML
    private AnchorPane apMoyenne;
    @FXML
    private Button btnMenuBulletin;
    @FXML
    private Button btnMenuMoyenne;
    @FXML
    private ListView lvMatieresMoyenne;
    @FXML
    private TreeView tvMoyennesParDevoirs;
    @FXML
    private TextField txtMajor;
    @FXML
    private TextField txtNoteMaxi;
    TreeItem root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        apBulletin.toFront();
        df = new DecimalFormat("#.##");
        lesBulletins = new HashMap<>();
        lvMatieres.getItems().addAll("Maths", "Anglais", "Economie");
        lvDevoirs.getItems().addAll("Devoir n°1", "Devoir n°2", "Devoir n°3", "Devoir n°4");
        cboTrimestres.getItems().addAll("Trim 1", "Trim 2", "Trim 3");
        cboTrimestres.getSelectionModel().selectFirst();
        root = new TreeItem("Par devoir");
    }

    @FXML
    public void btnMenuClicked(Event event) {
        if (event.getSource() == btnMenuBulletin) {
            apBulletin.toFront();
        } else if (event.getSource() == btnMenuMoyenne) {
            apMoyenne.toFront();

            // A vous de jouer
            lvMatieresMoyenne.getItems().clear();
            lvMatieresMoyenne.refresh();
            for (String matiere : lesBulletins.keySet()) {
                boolean hasNotes = false;
                for (String devoir : lesBulletins.get(matiere).keySet()) {
                    for (String trimestre : lesBulletins.get(matiere).get(devoir).keySet()) {
                        if (!lesBulletins.get(matiere).get(devoir).get(trimestre).isEmpty()) {
                            hasNotes = true;
                            break;
                        }
                    }
                    if (hasNotes) break;
                }
                if (hasNotes) {
                    lvMatieresMoyenne.getItems().add(matiere);
                }
            }
        }

    }

    @FXML
    public void btnValiderClicked(Event event) {
        // A vous de jouer
        if (lvMatieres.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de sélection");
            alert.setHeaderText("");
            alert.setContentText("Veuillez sélectionner une matière");
            alert.showAndWait();
        } else if (lvDevoirs.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de sélection");
            alert.setHeaderText("");
            alert.setContentText("Veuillez sélectionner un devoir");
            alert.showAndWait();
        } else if (txtNomEtudiant.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("");
            alert.setContentText("Veuillez saisir le nom de l'étudiant");
            alert.showAndWait();
        } else if (txtNote.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("");
            alert.setContentText("Veuillez saisir la note de l'étudiant");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("");
            alert.setContentText("Note enregistrée");
            alert.showAndWait();

            String matiere = lvMatieres.getSelectionModel().getSelectedItem().toString();
            String devoir = lvDevoirs.getSelectionModel().getSelectedItem().toString();
            String trimestre = cboTrimestres.getSelectionModel().getSelectedItem().toString();
            String nom = txtNomEtudiant.getText();
            double note = Double.parseDouble(txtNote.getText());
            Etudiant etudiant = new Etudiant(nom, note);

            if (!lesBulletins.containsKey(matiere)) {
                HashMap<String, HashMap<String, ArrayList<Etudiant>>> lesDevoirs = new HashMap<>();
                ArrayList<Etudiant> lesEtudiants = new ArrayList<>();
                lesEtudiants.add(etudiant);
                HashMap<String, ArrayList<Etudiant>> lesTrimestres = new HashMap<>();
                lesTrimestres.put(trimestre, lesEtudiants);
                lesDevoirs.put(devoir, lesTrimestres);
                lesBulletins.put(matiere, lesDevoirs);
            } else if (!lesBulletins.get(matiere).containsKey(devoir)) {
                ArrayList<Etudiant> lesEtudiants = new ArrayList<>();
                lesEtudiants.add(etudiant);
                HashMap<String, ArrayList<Etudiant>> lesTrimestres = new HashMap<>();
                lesTrimestres.put(trimestre, lesEtudiants);
                lesBulletins.get(matiere).put(devoir, lesTrimestres);
            } else if (!lesBulletins.get(matiere).get(devoir).containsKey(trimestre)) {
                ArrayList<Etudiant> lesEtudiants = new ArrayList<>();
                lesEtudiants.add(etudiant);
                lesBulletins.get(matiere).get(devoir).put(trimestre, lesEtudiants);
            } else {
                lesBulletins.get(matiere).get(devoir).get(trimestre).add(etudiant);
            }
        }

    }

    @FXML
    public void lvMatieresMoyenneClicked(Event event) {
        // A vous de jouer
        RemplirTreeViewDesTaches();
        afficherMajor();
    }

    public void RemplirTreeViewDesTaches() {
        root.getChildren().clear();
        for (Object matiere : lvMatieresMoyenne.getSelectionModel().getSelectedItems()) {
            for (String devoir : lesBulletins.get(matiere).keySet()) {
                double totalNotes = 0;
                int conteNotes = 0;
                for (String trimestre : lesBulletins.get(matiere).get(devoir).keySet()) {
                    for (Etudiant unEtudiant : lesBulletins.get(matiere).get(devoir).get(trimestre)) {
                        totalNotes += unEtudiant.getNote();
                        conteNotes++;
                    }
                }
                double moyenne;
                if (conteNotes > 0) {
                    moyenne = totalNotes / conteNotes;
                } else {
                    moyenne = 0;
                }

                TreeItem devoirNode = new TreeItem(devoir + " : " + moyenne);
                root.getChildren().add(devoirNode);
            }
        }
        tvMoyennesParDevoirs.setRoot(root);
    }
    public void afficherMajor() {
        String major = "";
        double meuilleurNote = -1;

        for (String matiere : lesBulletins.keySet()) {
            for (String devoir : lesBulletins.get(matiere).keySet()) {
                for (String trimestre : lesBulletins.get(matiere).get(devoir).keySet()) {
                    for (Etudiant etudiant : lesBulletins.get(matiere).get(devoir).get(trimestre)) {
                        if (etudiant.getNote() > meuilleurNote) {
                            meuilleurNote = etudiant.getNote();
                            major = etudiant.getNomEtudiant();
                        }
                    }
                }
            }
        }
        txtMajor.setText(major);
        txtNoteMaxi.setText(String.valueOf(meuilleurNote));
    }
}