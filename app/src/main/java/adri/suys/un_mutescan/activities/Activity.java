package adri.suys.un_mutescan.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;

public abstract class Activity extends AppCompatActivity {

    private static final String USER = "user-backup";
    private static final String EVENTS = "events-backup";
    private static final String URLS = "urls-backup";

    @Override
    protected void onCreate(Bundle state){
        super.onCreate(state);
    }

    /**
     * Remove the title and the elevation (in order to remove the shadow underneath it) from the Action Bar
     */
    void configActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setElevation(0);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.disconnect){
            makePendingRequest();
            UnMuteDataHolder.reinit();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Display a Toast on the screen
     * @param msg the message that will be displayed in the toast
     */
    public void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Checks if the device has an internet connexion
     * @return true if the device is connected to the internet, false otherwise
     */
    public boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Write all the data on the user in a internal storage file
     */
    public void backUpUser() {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = openFileOutput(USER, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(UnMuteDataHolder.getUser());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null){
                try {
                    oos.flush();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write all the data on the events in a internal storage file
     */
    public void backUpEvents() {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = openFileOutput(EVENTS, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(UnMuteDataHolder.getEvents());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null){
                try {
                    oos.flush();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write all the pending requests' url in a internal storage file.
     */
    public void backUpUrls() {
        try {
            FileOutputStream fos = openFileOutput(URLS, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(UnMuteDataHolder.getRequestURLs());
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the user's data stored in a internal storage file
     * @return the user
     */
    public User retrieveUser(){
        User user = null;
        try {
            FileInputStream fis = openFileInput(USER);
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (User) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    /**
     * Get the events' data stored in a internal storage file
     * @return the list of events
     */
    public List<Event> retrieveEvents(){
        List<Event> events = null;
        try {
            FileInputStream fis = openFileInput(EVENTS);
            ObjectInputStream ois = new ObjectInputStream(fis);
            events = (List<Event>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return events;
    }

    @SuppressWarnings("unchecked")
    /**
     * Get the pending requests' url stored in a internal storage file
     * @return the list of requests' url
     */
    private List<String> retrievePendingRequest(){
        List<String> requests = null;
        try {
            FileInputStream fis = openFileInput(URLS);
            ObjectInputStream ois = new ObjectInputStream(fis);
            requests = (List<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * If the device is connected to the internet, tries to connect with the server
     * and make all the pending requests.
     */
    void makePendingRequest(){
        if (isInternetConnected()) {
            List<String> pendingRequests = retrievePendingRequest();
            UnMuteDataHolder.setRequestURLs(pendingRequests);
            if (UnMuteDataHolder.getRequestURLs() != null && UnMuteDataHolder.getRequestURLs().size() > 0) {
                RestService restService = new RestService(this);
                restService.makePendingRequest();
            }
        }
    }
}
