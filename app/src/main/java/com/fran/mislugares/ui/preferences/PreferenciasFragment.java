package com.fran.mislugares.ui.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.fran.mislugares.R;

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
