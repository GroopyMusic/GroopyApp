package adri.suys.un_mutescan.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.fragments.AudienceFragment;
import adri.suys.un_mutescan.fragments.BarcodeManualInputFragment;
import adri.suys.un_mutescan.fragments.BarcodeScannerFragment;
import adri.suys.un_mutescan.fragments.BuyTicketOnSiteFragment;
import adri.suys.un_mutescan.fragments.EventStatFragment;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;

public class OneEventActivity extends Activity {

    private BottomNavigationView bottomNavigationView;
    private static final int STAT_FRAGMENT = 0;
    private static final int SCAN_FRAGMENT = 1;
    private static final int INPUT_FRAGMENT = 2;
    private static final int ADD_FRAGMENT = 3;
    private static final int PUBLIC_FRAGMENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_event);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        configActionBar();
        setNavigationActions();
        loadFragment(new EventStatFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (UnMuteDataHolder.getCurrentFragment()){
            case STAT_FRAGMENT :
                bottomNavigationView.setSelectedItemId(R.id.action_stat);
                loadFragment(new EventStatFragment());
                break;
            case SCAN_FRAGMENT :
                bottomNavigationView.setSelectedItemId(R.id.action_qrcode);
                loadFragment(new BarcodeScannerFragment());
                break;
            case INPUT_FRAGMENT :
                bottomNavigationView.setSelectedItemId(R.id.action_type_code);
                loadFragment(new BarcodeManualInputFragment());
                break;
            case ADD_FRAGMENT :
                bottomNavigationView.setSelectedItemId(R.id.action_add_ticket);
                loadFragment(new BuyTicketOnSiteFragment());
                break;
            case PUBLIC_FRAGMENT :
                bottomNavigationView.setSelectedItemId(R.id.action_get_guest);
                loadFragment(new AudienceFragment());
                break;
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, EventListActivity.class));
    }


    private void setNavigationActions(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_stat :
                        loadFragment(new EventStatFragment());
                        UnMuteDataHolder.setCurrentFragment(STAT_FRAGMENT);
                        return true;
                    case R.id.action_qrcode :
                        if (UnMuteDataHolder.getEvent().isToday()){
                            loadFragment(new BarcodeScannerFragment());
                            UnMuteDataHolder.setCurrentFragment(SCAN_FRAGMENT);
                        } else {
                            showAlertDialog(getResources().getString(R.string.nav_error_scan));
                        }
                        return true;
                    case R.id.action_type_code :
                        if (UnMuteDataHolder.getEvent().isToday()) {
                            loadFragment(new BarcodeManualInputFragment());
                            UnMuteDataHolder.setCurrentFragment(INPUT_FRAGMENT);
                        } else {
                            showAlertDialog(getResources().getString(R.string.nav_error_keyboard));
                        }
                        return true;
                    case R.id.action_add_ticket :
                        if (UnMuteDataHolder.getEvent().isToday()) {
                            loadFragment(new BuyTicketOnSiteFragment());
                            UnMuteDataHolder.setCurrentFragment(ADD_FRAGMENT);
                        } else {
                            showAlertDialog(getResources().getString(R.string.nav_error_sell));
                        }
                        return true;
                    case R.id.action_get_guest:
                        loadFragment(new AudienceFragment());
                        UnMuteDataHolder.setCurrentFragment(PUBLIC_FRAGMENT);
                        return true;
                    default :
                        // do noting
                        return false;
                }
            }
        });
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showAlertDialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder.setMessage(message).setTitle("");
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.setTitle(R.string.error);
        alert.show();
    }

}
