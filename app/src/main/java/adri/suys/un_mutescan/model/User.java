package adri.suys.un_mutescan.model;

import java.io.Serializable;

public class User implements Serializable {

    private final String id;
    private final String username;
    private final String password;
    private final String error;

    public User(int id, String username, String password, String error) {
        this.id = Integer.toString(id);
        this.username = username;
        this.password = password;
        this.error = error;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public String getPassword() {
        return password;
    }

    public String getError(){
        return error;
    }

    public String toString(){
        return id + "-" + username + "-" + password + "-" + error;
    }
}
