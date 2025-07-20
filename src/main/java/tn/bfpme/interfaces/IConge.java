package tn.bfpme.interfaces;

import tn.bfpme.models.Conge;
import tn.bfpme.models.Statut;

import java.util.List;

public interface IConge<C> {
    List<Conge> afficher();

    void Add(C c);

    void updateConge(Conge conge);

    void updateStatutConge(int id, Statut statut);

    void deleteCongeByID(int id);

    List<Conge> TriparStatut();

    List<Conge> TriparType();

    List<Conge> TriparDateD();

    List<Conge> TriparDateF();

    List<Conge> TriparDesc();

    List<Conge> Rechreche(String recherche);

    void updateUserSolde(int userId, int typeCongeId, int congeDays);
}
