package com.nxg2278.mislugares;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by nxg2278 on 18/05/2016.
 */
public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
