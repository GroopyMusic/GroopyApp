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
import java.util.ArrayList;
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
        List<String> requests = retrievePendingRequest();
        UnMuteDataHolder.setRequestURLs(requests);
        makePendingRequest();
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
            UnMuteDataHolder.reinit();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        makePendingRequest();
    }

    /**
     * Display a Toast on the screen
     * @param msg the message that will be displayed in the toast
     */
    public void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

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

    public List<String> retrievePendingRequest(){
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

    private void makePendingRequest(){
        if (UnMuteDataHolder.getRequestURLs() != null && UnMuteDataHolder.getRequestURLs().size() > 0){
            System.out.println("-------------ca passe par ici");
            RestService restService = new RestService(this);
            restService.makePendingRequest();
        }
    }
}
