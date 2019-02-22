package adri.suys.un_mutescan.apirest;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Observable;

import adri.suys.un_mutescan.activities.MainActivity;
import adri.suys.un_mutescan.model.User;

public class RestUser extends Observable {

    private static final String BASE_URL = "http://192.168.1.33:8888/GroopyMusic/web/app_dev.php/loginuser?";
    private RestService rest;

    public RestUser(MainActivity mainActivity){
        rest = new RestService(mainActivity);
        this.addObserver(mainActivity);
    }

    public void loginUser(String username){
        String url = makeUserUrl(username);
        JsonObjectRequest userRequest = createUserRequest(Request.Method.GET, url);
        rest.getRequestQueue().add(userRequest);
    }

    private String makeUserUrl(String username) {
        return BASE_URL + "username=" + username;
    }

    private JsonObjectRequest createUserRequest(int requestMethod, String url) {
        return  new JsonObjectRequest(requestMethod, url, null,
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
