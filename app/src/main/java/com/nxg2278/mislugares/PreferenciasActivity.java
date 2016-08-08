package com.nxg2278.mislugares;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nxg2278 on 18/05/2016.
 */
public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenciasFragment()).commit();
    }
}
