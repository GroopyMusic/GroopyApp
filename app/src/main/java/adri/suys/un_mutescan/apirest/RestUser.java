package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Observable;

import adri.suys.un_mutescan.activities.LoginActivity;
import adri.suys.un_mutescan.model.User;

/**
 * Handles all the communication with the API concerning the connection of an user
 */
public class RestUser extends Observable {

    private static final String BASE_URL = "http://192.168.1.216:8888/GroopyMusic/web/app_dev.php/loginuser?";
    //private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/loginuser?";
    private final RestService rest;

    public RestUser(LoginActivity loginActivity){
        rest = new RestService(loginActivity);
        this.addObserver(loginActivity);
    }

    /**
     * Complete the base url with the given param,
     * create a GET request to get the user that matches the given username
     * if the user is found, we notify the observers with the user object
     * else we give an error (String) to the observers
     *
     * @param username the username of the user
     */
    public void loginUser(String username){
        String url = makeUserUrl(username);
        JsonObjectRequest userRequest = createUserRequest(url);
        rest.getRequestQueue().add(userRequest);
    }

    /////////////////////
    // private methods //
    /////////////////////

    private String makeUserUrl(String username) {
        return BASE_URL + "username=" + username;
    }

    private JsonObjectRequest createUserRequest(String url) {
        return  new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", response.toString());
                        User user = getUserFromJSON(response);
                        setChanged();
                        notifyObservers(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", error.toString());
                        setChanged();
                        notifyObservers(error);
                    }
                }
        );
    }

    private User getUserFromJSON(JSONObject json){
        return rest.getGson().fromJson(json.toString(), User.class);
    }
}
