package tn.bfpme.models;

import java.util.List;

public class UserConge {
    private List<User> users;
    private List<Conge> conges;

    public UserConge(List<User> users, List<Conge> conges) {
        this.users = users;
        this.conges = conges;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Conge> getConges() {
        return conges;
    }
}