package com.fran.mislugares2016;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by fran on 27/4/16.
 */
public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
