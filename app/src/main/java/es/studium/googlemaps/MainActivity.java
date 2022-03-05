package es.studium.googlemaps;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.studium.googlemaps.controller.DatosController;
import es.studium.googlemaps.modelos.DatosDeLocalizacion;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

    GoogleMap mapa;
    Double latitud;
    Double longitud;
    private Location loc;
    DatosController datosController;
    DatosDeLocalizacion datosDeLocalizacion;
    LatLng posicion;
    private List<DatosDeLocalizacion>list;
    int bateria;
    String coordenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        datosController=new DatosController(MainActivity.this);
        list=new ArrayList<>();


        //Localizacion
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {

            locationStart();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Mapa incial
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);


        }

    }



    public float Bateria(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (level / (float) scale) * 100;
    }



    private void locationStart()
    {//Localizacion inicial
        LocationManager mlocManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled)
        {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, (LocationListener)
                Local);

    }

    public void setLocation(Location loc)//Repite cada cuando seteamos localizacion
    {   actualizacionMarcadores();

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0)
        {
            try
            {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty())
                {
                    float bateria=Bateria();
                    posicion=new LatLng(loc.getLatitude(),loc.getLongitude());
                    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
                    mapa.addMarker(new MarkerOptions().position(posicion).title(""+bateria).snippet(coordenadas)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokemon))
                    .anchor(0.5f,1f));

                    DatosDeLocalizacion datosDeLocalizacion=new DatosDeLocalizacion(latitud,longitud,(int)bateria);


                    long id = datosController.nuevaUbicacion(datosDeLocalizacion);

                    if (id == -1) {

                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(MainActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void actualizacionMarcadores() {//Este método actualiza los marcadores cada vez que seteamos una
        //Lista con los datos de la BD
        list = datosController.obtenerPosicion();
        //Si la lista no esta vacia
        if (list != null) {

            for (int i = 0; i < list.size(); i++) {

                Double latitud1 = list.get(i).getLatitud();
                Double longitud1 = list.get(i).getLongitud();
                String coordenadas1 = latitud1 + ", " + longitud1;
                String bateria1 = list.get(i).getBateria() +"";
                //Ponemos Marcador en posición Guardada
                LatLng nuevaPosicion = new LatLng(latitud1, longitud1);
                mapa.addMarker(new MarkerOptions()
                        .position(nuevaPosicion)
                        .title(bateria1 + "%")
                        .snippet(coordenadas1)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pokemon))
                        .anchor(0.5f, 1f));
            }
        }
    }



    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener
    {
        MainActivity mainActivity;


        public MainActivity getMainActivity()
        {
            return mainActivity;
        }
        public void setMainActivity(MainActivity mainActivity)
        {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc)
        {
// Este método se ejecuta cada vez que el GPS recibe nuevas coordenadas
// debido a la detección de un cambio de ubicación
           latitud= loc.getLatitude();
            longitud=loc.getLongitude();

            coordenadas=loc.getLatitude() +", "+loc.getLongitude();
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Toast.makeText(mainActivity, "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Toast.makeText(mainActivity, "GPS Activado", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            switch (status)
            {
                case 0:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case 1:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
                case 2:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
            }
        }
    }
}