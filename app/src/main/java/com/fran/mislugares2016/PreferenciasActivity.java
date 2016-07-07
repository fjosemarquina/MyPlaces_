package com.fran.mislugares2016;

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
