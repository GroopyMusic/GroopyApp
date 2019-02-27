package adri.suys.un_mutescan.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import adri.suys.un_mutescan.R;

public abstract class Activity extends AppCompatActivity {

    /**
     * Remove the title and the elevation (in order to remove the shadow underneath it) from the Action Bar
     */
    void configActionBar(){
        getSupportActionBar().setTitle("");
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
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display a Toast on the screen
     * @param msg the message that will be displayed in the toast
     */
    void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
