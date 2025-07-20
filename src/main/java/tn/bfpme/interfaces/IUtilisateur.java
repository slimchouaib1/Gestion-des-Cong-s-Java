package tn.bfpme.interfaces;

import tn.bfpme.models.User;
import tn.bfpme.models.UserConge;

import java.sql.SQLException;
import java.util.List;

public interface IUtilisateur {

    List<User> show() throws SQLException;

    UserConge TriType();

    UserConge TriNom();

    UserConge TriPrenom();

    UserConge TriDateDebut();

    UserConge TriDateFin();

    UserConge AfficherApprove();

    UserConge AfficherReject();

    List<User> getUsersByDepartment(String departement);

    List<User> getAllUsers();

    void Add(User user);

    void Update(User user);

    void DeleteByID(int id);

    List<User> search(String query);

    List<User> ShowUnder();
}
