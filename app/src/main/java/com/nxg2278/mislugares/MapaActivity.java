package com.nxg2278.mislugares;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by nxg2278 on 30/05/2016.
 */
public class MapaActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa)).getMap();
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);

        //Add escuchador OnInfoWindowClickListener (*implements into class)
        mapa.setOnInfoWindowClickListener(this);
        //

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
        }

        if (SelectorFragment.adaptador.getItemCount() > 0) {
            GeoPunto p = SelectorFragment.adaptador.lugarPosicion(0).getPosicion();
            LatLng latLang = new LatLng(p.getLatitud(), p.getLongitud());
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 12));
        }

        for (int n=0; n<SelectorFragment.adaptador.getItemCount(); n++) {
            Lugar lugar = SelectorFragment.adaptador.lugarPosicion(n);
            GeoPunto p = lugar.getPosicion();
            if (p != null && p.getLatitud() != 0) {
                BitmapDrawable iconoDrawable = (BitmapDrawable) getResources()
                        .getDrawable(lugar.getTipo().getRecurso());
                Bitmap iGrande = iconoDrawable.getBitmap();
                Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                        iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
                mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(p.getLatitud(), p.getLongitud()))
                        .title(lugar.getNombre()).snippet(lugar.getDireccion())
                        .icon(BitmapDescriptorFactory.fromBitmap(icono)));
            }
        }
    }

    //implementation OnInfoWindowClickListener
    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int id=0; id < SelectorFragment.adaptador.getItemCount(); id++) {
            if(SelectorFragment.adaptador.lugarPosicion(id). getNombre().equals(marker.getTitle())) {
                Intent i = new Intent(this, VistaLugarActivity.class);
                i.putExtra("id", (long)id);
                startActivity(i);
                break;
            }
        }
    }
}
