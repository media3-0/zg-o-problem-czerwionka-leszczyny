package com.media30.zglosproblem.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;


public class ListActivity extends FragmentActivity {

    private ProgressDialog pd;
    public boolean closing = false;
    public int actualFragmentId;
    public int actualTab = 0;
    public List<Report> reportList;
    public FragmentTabHost mTabHost;

    private int [][] resTab = new int [][] {
            { R.drawable.lista, R.drawable.listaczerwona },
            { R.drawable.pin, R.drawable.pinczerwony },
            { R.drawable.galeria, R.drawable.galeriaczerwona }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);

        closing = false;

        mTabHost = (FragmentTabHost) findViewById(R.id.tabHost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.readTabContent);

        addCustomTab(this, "1", getResources().getDrawable(resTab[0][1]), ReportsListFragment.class, mTabHost);
        addCustomTab(this, "2", getResources().getDrawable(resTab[1][0]), ReportsMapFragment.class, mTabHost);
        addCustomTab(this, "3", getResources().getDrawable(resTab[2][0]), ReportsThumbsFragment.class, mTabHost);

        mTabHost.setCurrentTab(actualTab);

        //mTabHost.addTab(mTabHost.newTabSpec("lista").setIndicator("",getResources().getDrawable(R.drawable.lista)),
        //        ReportsListFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("mapa").setIndicator("",getResources().getDrawable(R.drawable.pin)),
        //        ReportsMapFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("miniatury").setIndicator("",getResources().getDrawable(R.drawable.galeria)),
        //        ReportsThumbsFragment.class, null);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        pd = ProgressDialog.show(this, "Proszę czekać", "Trwa pobieranie listy zgłoszeń", true, false);
        httpClient.get(MainActivity.HOST + "list.php", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                if(response.length() > 0){
                    reportList = new ArrayList<Report>(response.length());
                    for(int i = 0; i < response.length(); i++){
                        try {
                            reportList.add(new Report(response.getJSONObject(i)));
                        }catch (JSONException e){
                            Log.e("JSONException", e.toString());
                        }
                    }
                    RefreshableFragment rf = (RefreshableFragment)getSupportFragmentManager().findFragmentById(actualFragmentId);
                    if(rf != null){
                        rf.refresh();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Brak zgłoszeń", Toast.LENGTH_LONG);
                    toast.show();
                }
                pd.dismiss();
                Log.d("onSuccessJson", response.toString());
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                pd.dismiss();
                Log.e("onFailureJson", responseBody);
                Toast toast = Toast.makeText(getApplicationContext(), "Błąd podczas pobierania listy", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void addCustomTab(Context context, final String labelId, Drawable drawable, Class<?> c, FragmentTabHost fth ) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_customtab, null);
        ImageView image = (ImageView) view.findViewById(R.id.ivTabImage);

        image.setImageDrawable(drawable);

        TabHost.TabSpec spec = fth.newTabSpec(labelId);
        spec.setIndicator(view);
        fth.addTab(spec, c, null);

        fth.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
/*                if(s.contains("0")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + numer));
                    startActivity(intent);
                    return;
                }*/
                for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
                {
                    ImageView ib = (ImageView)mTabHost.getTabWidget().getChildAt(i);
                    ImageView ibc = (ImageView)mTabHost.getCurrentTabView();
                    if(ib != ibc){
                        ib.setImageDrawable(getResources().getDrawable(resTab[i][0]));
                    }else{
                        ib.setImageDrawable(getResources().getDrawable(resTab[i][1]));
                    }
                }
                actualTab = mTabHost.getCurrentTab();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    public void homeClick(View view){
        final ImageButton ib = (ImageButton)view;
        HelperClass.onClickImageButton(this, ib, R.drawable.domekok_shadow, R.drawable.domekczerwony);
        Intent i = new Intent(ListActivity.this, WelcomeActivity.class);
        if(Build.VERSION.SDK_INT > 10) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
    }

    public void startClick(View view){
        MainActivity.startReport(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        closing = true;
        super.onDestroy();
    }

    interface RefreshableFragment {
        abstract void refresh();
    }

    public static class ReportsMapFragment extends SupportMapFragment implements RefreshableFragment {

        private GoogleMap mMap;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.fragment_map, container, false);
            setUpMapIfNeeded();
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if(!((ListActivity)getActivity()).closing) {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.map_fragment);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
                mMap = null;
            }
        }

        @Override
        public void onStart() {
            setupData();
            super.onStart();
        }

        @Override
        public void refresh() {
            setupData();
        }

        private void setupData(){
            ListActivity la = (ListActivity)getActivity();
            if(la.reportList != null && mMap != null) {
                mMap.clear();
                for(Report r : la.reportList){
                    int resource;
                    if(r.getCat() == 1000){
                        resource = R.drawable.inne;
                    }else {
                        resource = Categories.categoriesResources[r.getCat()];
                    }
                    mMap.addMarker(new MarkerOptions()
                        .position(r.getLocation())
                        .title("Zgłoszenie nr: " + r.getId())
                        .snippet(r.getDescription())
                        .icon(BitmapDescriptorFactory.fromResource(resource))
                    );
                }
            }
        }

        @Override
        public void onResume() {
            setUpMapIfNeeded();
            setupData();
            super.onResume();
        }

        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {

                    View loc = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getView();
                    @SuppressWarnings("ResourceType") View map = loc.findViewById(1);

                    View mapParent = (View)map.getParent();
                    @SuppressWarnings("ResourceType") View locationButton = mapParent.findViewById(2);

                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    rlp.setMargins(30, 0, 0, 30);

                    //pokaż przycisk "moja lokalizacja"
                    mMap.setMyLocationEnabled(true);
                    setupData();

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            String[] title = marker.getTitle().split(" ");
                            ListActivity la = (ListActivity)getActivity();
                            for(Report r : la.reportList){
                                if(r.getId() == Integer.valueOf(title[2])){
                                    Intent intent = new Intent(la, DetailsActivity.class);
                                    intent.putExtra("report", r);
                                    la.startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });

                    mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            final Context context = getActivity();
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

                            }
                            return false;
                        }
                    });
                }
            }
        }

        @Override
        public void onAttach(Activity activity) {
            ListActivity la = (ListActivity)activity;
            la.actualFragmentId = this.getId();
            super.onAttach(activity);
        }
    }

    public static class ReportsThumbsFragment extends Fragment implements RefreshableFragment {

        private GridView gridView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_thumbs, container, false);
            gridView = (GridView)v.findViewById(R.id.gridView);
            return v;
        }

        @Override
        public void onStart() {
            setupData();
            super.onStart();
        }

        @Override
        public void refresh() {
            setupData();
        }

        private void setupData(){
            ListActivity la = (ListActivity)getActivity();
            if(la.reportList != null) {
                ArrayAdapter adapter = new ListAdapter(this.getActivity(), R.layout.thumb_item, la.reportList);
                gridView.setAdapter(adapter);
            }
        }

        @Override
        public void onAttach(Activity activity) {
            ListActivity la = (ListActivity)activity;
            la.actualFragmentId = this.getId();
            super.onAttach(activity);
        }
    }

    public static class ReportsListFragment extends Fragment implements RefreshableFragment {

        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            listView = (ListView)view.findViewById(R.id.listView);
            return view;
        }

        @Override
        public void onStart() {
            setupData();
            super.onStart();
        }

        @Override
        public void refresh() {
            setupData();
        }

        private void setupData(){
            ListActivity la = (ListActivity)getActivity();
            if(la.reportList != null) {
                ArrayAdapter adapter = new ListAdapter(this.getActivity(), R.layout.list_item, la.reportList);
                listView.setAdapter(adapter);
            }
        }

        @Override
        public void onAttach(Activity activity) {
            ListActivity la = (ListActivity)activity;
            la.actualFragmentId = this.getId();
            super.onAttach(activity);
        }
    }

    /*public static class DummyFragment extends Fragment implements RefreshableFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dummy_layout, container, false);
            return view;
        }

        @Override
        public void onStart() {
            ListActivity la = (ListActivity)getActivity();
            la.mTabHost.setCurrentTab(la.actualTab);
            super.onStart();
        }

        @Override
        public void refresh() {

        }


        @Override
        public void onAttach(Activity activity) {
            ListActivity la = (ListActivity)activity;
            la.actualFragmentId = this.getId();
            super.onAttach(activity);
        }
    }*/
}


