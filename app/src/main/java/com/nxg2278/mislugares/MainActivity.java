package com.nxg2278.mislugares;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener{

    public static LugaresBD lugares;
    private Button bAcercaDe;
    private Button preferencias;
    private Button mostrarLugares;

    //Atributo para reproduccion de sonido
    MediaPlayer mp;
    int pos; //posicion musica cuando cambio de orientacion

    //Atributos para Posicionamiento
    final static String TAG = "Mis Lugares";
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 0;
    private LocationManager manejador;
    private Location mejorLocaliz;
    protected static GeoPunto posicionActual = new GeoPunto(0,0);
    private static final long DOS_MINUTOS = 2 * 60 * 1000;

    //Atributo para organizar lugares segun preferencias
    static final int RESULTADO_PREFERENCIAS = 0;

    //Atributo para fragments
    private VistaLugarFragment fragmentVista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lugares = new LugaresBD(this);

        //Configuracion reproduccion sonido
        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();

        //Configuracion fragment VistaLugar
        fragmentVista = (VistaLugarFragment) getSupportFragmentManager().findFragmentById(R.id.vista_lugar_fragment);

        //Configuracion boton emergente
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long _id = lugares.nuevo();
                Intent i = new Intent(MainActivity.this,EdicionLugarActivity.class);
                i.putExtra("_id", _id);
                startActivity(i);
            }
        });

        //Configuracion Localizacion
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(manejador.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER));
            }
            if(manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                actualizaMejorLocaliz(manejador.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER));
            } else {
                solicitarPermisoLocalizacion();
            }
        }

    }

    public void mostrarPreferencias() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "notificaciones: " + pref.getBoolean("notificaciones", true) + ", maximo a listar: " + pref.getString("maximo", "?") + ", recibir correo: "
        + pref.getBoolean("notifications", true) + ", correo: " + pref.getString("correo", "?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivityForResult(i, RESULTADO_PREFERENCIAS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULTADO_PREFERENCIAS) {
            SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
            SelectorFragment.adaptador.notifyDataSetChanged();
        }
    }

    public void lanzarVistaLugar(View view){
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Selección de lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(MainActivity.this, VistaLugarActivity.class);
                        i.putExtra("id", id);
                        startActivity(i);
                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void salir(View view){
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }

        if (id == R.id. acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }

        if (id == R.id.menu_buscar) {
            lanzarVistaLugar(null);
            return true;
        }

        if (id == R.id.menu_mapa) {
            Intent i = new Intent(this, MapaActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() {
        super.onResume();
        activarProveedores();

        //Configurar primer VistaLugar cuando estamos en una tablet
        if (fragmentVista!=null && SelectorFragment.adaptador.getItemCount()>0) {
            fragmentVista.actualizarVistas(0);
        }
    }
    private void activarProveedores() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, this);
            }
            if(manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, this);
            }
        }
    }


    @Override protected void onPause() {
        super.onPause();
        mp.pause();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manejador.removeUpdates(this);
        }
    }

    //Retomar musica donde esta al cambiar de orientacion la pantalla

    @Override
    protected void onSaveInstanceState(Bundle guardarEstado) {
        super.onSaveInstanceState(guardarEstado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            guardarEstado.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int pos = savedInstanceState.getInt("posicion");
        mp.seekTo(pos);
    }

    //Metodos implementados con LocationListener (Ubicacion)
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: "+location);
        actualizaMejorLocaliz(location);
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(TAG, "Se deshabilita: "+proveedor);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(TAG, "Se habilita: "+proveedor);
        activarProveedores();
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(TAG, "Cambia estado: "+proveedor);
        activarProveedores();
    }

    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz !=null && (mejorLocaliz == null
                || localiz.getAccuracy() < 2*mejorLocaliz.getAccuracy()
                || localiz.getTime() - mejorLocaliz.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización");
            mejorLocaliz = localiz;
            posicionActual.setLatitud(localiz.getLatitude());
            posicionActual.setLongitud(localiz.getLongitude());
        }
    }

    void solicitarPermisoLocalizacion(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(findViewById(R.id.recycler_view), "Sin el permiso de localización"
                            +" no puedo mostrar la distancia a los lugares.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                                    SOLICITUD_PERMISO_LOCALIZACION);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    SOLICITUD_PERMISO_LOCALIZACION);
        }
    }

    //Muestra lugar en el fragment de la derecha
    public void muestraLugar(long id) {
        if (fragmentVista != null) {
            fragmentVista.actualizarVistas(id);
        } else {
            Intent i = new Intent(this, VistaLugarActivity.class);
            i.putExtra("id", id);
            startActivityForResult(i, 0);
        }
    }
}