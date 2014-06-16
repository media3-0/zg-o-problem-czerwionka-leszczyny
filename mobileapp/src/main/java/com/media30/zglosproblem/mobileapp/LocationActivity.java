package com.media30.zglosproblem.mobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mark = null;
    private boolean backToSummary = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_all);
        if(getIntent().getExtras() != null)
            backToSummary = getIntent().getExtras().getBoolean("back_to_summary", false);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        if(sp.getLong(MainActivity.POS_LAT, 0) != 0) {
            LatLng ll = new LatLng(Double.longBitsToDouble(sp.getLong(MainActivity.POS_LAT, 0)), Double.longBitsToDouble(sp.getLong(MainActivity.POS_LNG, 0)));
            setMapPosition(ll);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();

                View loc = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getView();
                final View map = loc.findViewById(1);

                View mapParent = (View)map.getParent();
                View locationButton = mapParent.findViewById(2);

                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                rlp.setMargins(30, 0, 0, 30);

                //pokaż przycisk "moja lokalizacja"
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                final Context context = LocationActivity.this;
                LocationManager lm;
                boolean gps_enabled = false, network_enabled = false;
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                try{
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                }catch(Exception ex){}
                try{
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }catch(Exception ex){}

                if(!gps_enabled && !network_enabled){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                            context.startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            //
                        }
                    });
                    dialog.show();
                    return true;
                }
                Toast toast = Toast.makeText(LocationActivity.this, "Trwa lokalizowanie", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setMapPosition(latLng);
            }
        });
    }

    private void setMapPosition(LatLng latLng){
        if(mark == null){
            mark = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        }else{
            mark.setPosition(latLng);
        }
        SharedPreferences sp = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(MainActivity.POS_LAT, Double.doubleToLongBits(mark.getPosition().latitude));
        editor.putLong(MainActivity.POS_LNG, Double.doubleToLongBits(mark.getPosition().longitude));
        editor.commit();
    }

    public void wsteczClick(View view){
        //przycisk wstecz
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img7_shadow, R.drawable.img7);
        this.finish();
    }

    public void homeClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.domekok_shadow, R.drawable.domekczerwony);
        SummaryActivity.cancelWizard(this);
    }

    public void dalejClick(View view){
        //przycisk dalej
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.img16_shadow, R.drawable.img16);
        if(backToSummary){
            Intent intent = new Intent(this, SummaryActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, DescriptionActivity.class);
            startActivity(intent);
        }
    }
}
