package adri.suys.un_mutescan.model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String username;
    private String password;
    private String error;

    public User(int id, String username, String password, String error) {
        this.id = Integer.toString(id);
        this.username = username;
        this.password = password;
        this.error = error;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(int id) {
        String s = Integer.toString(id);
        this.id = s;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setError(String error){
        this.error = error;
    }

    public String getError(){
        return error;
    }

    public boolean hasRights(){
        return error.equals("");
    }

    public String toString(){
        return id + "-" + username + "-" + password + "-" + error;
    }
}
