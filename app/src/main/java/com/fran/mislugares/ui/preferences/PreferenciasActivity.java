package com.fran.mislugares.ui.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by fran on 27/4/16.
 */
public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}
