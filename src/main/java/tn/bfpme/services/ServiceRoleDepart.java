package tn.bfpme.services;

import tn.bfpme.models.Departement;
import tn.bfpme.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceRoleDepart {
    private Connection cnx;

    public ServiceRoleDepart() {
        cnx = MyDataBase.getInstance().getCnx();
    }
    private List<Departement> getRelatedDepartments(int roleId) throws SQLException {
        if (cnx == null || cnx.isClosed()) {
            cnx = MyDataBase.getInstance().getCnx();
        }
        String query = "SELECT d.* FROM departement d JOIN role_departement rd ON d.ID_Departement = rd.ID_Departement WHERE rd.ID_Role = ?";
        List<Departement> departments = new ArrayList<>();
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID_Departement");
                String name = rs.getString("nom");
                String description = rs.getString("description");
                int parentDeptId = rs.getInt("Parent_Dept");
                int level = rs.getInt("Level");
                Departement dept = new Departement(id, name, description, parentDeptId, level);
                departments.add(dept);
            }
        }
        return departments;
    }



}
