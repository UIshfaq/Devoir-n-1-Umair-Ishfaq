package sio.bulletin.Model;

public class Etudiant
{
    private String nomEtudiant;
    private double note;

    public Etudiant(String nomEtudiant, double note) {
        this.nomEtudiant = nomEtudiant;
        this.note = note;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public double getNote() {
        return note;
    }
}
