package adri.suys.un_mutescan.apirest;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class RestService {

    private final RequestQueue requestQueue;
    private final Gson gson;

    public RestService(AppCompatActivity activity){
        requestQueue = Volley.newRequestQueue(activity);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public Gson getGson() {
        return gson;
    }
}
