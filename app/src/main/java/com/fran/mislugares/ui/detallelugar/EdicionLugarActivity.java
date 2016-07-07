package com.fran.mislugares.ui.detallelugar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fran.mislugares.R;
import com.fran.mislugares.ui.utils.SelectorFragment;
import com.fran.mislugares.entities.Lugar;
import com.fran.mislugares.entities.TipoLugar;
import com.fran.mislugares.ui.mainview.MainActivity;

/**
 * Created by fran on 27/4/16.
 */
public class EdicionLugarActivity extends AppCompatActivity {

    private long _id;

    private long id;
    private Lugar lugar;

    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        _id = extras.getLong("_id", -1);
        if (_id!=-1) {
            lugar = MainActivity.lugares.elemento((int) _id);
        } else {
            lugar = SelectorFragment.adaptador.lugarPosicion((int) id);
        }
        nombre = (EditText) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());

        // direccón
        if ( lugar.getDireccion() == null || lugar.getDireccion().isEmpty()) {
            findViewById(R.id.barra_direccion).setVisibility(View.GONE);
        } else {
            direccion = (EditText) findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
        }

        // teléfono
        telefono = (EditText) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));

        // url
        url = (EditText) findViewById(R.id.url);
        url.setText(lugar.getUrl());

        // comentario
        comentario = (EditText) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());

        // tipo de lugar
        tipo = (Spinner) findViewById(R.id.tipo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_cancelar:
                if (_id!=-1) {
                    MainActivity.lugares.borrar((int) _id);
                }
                finish();
                return true;
            case R.id.accion_guardar:
                lugar.setNombre(nombre.getText().toString());
                lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                lugar.setDireccion(direccion.getText().toString());
                lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comentario.getText().toString());
                if (_id==-1) {
                    _id = SelectorFragment.adaptador.idPosicion((int) id);
                }
                MainActivity.lugares.actualiza((int)_id,lugar);
                SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
                if (id!=-1) {
                    SelectorFragment.adaptador.notifyItemChanged((int) id);
                } else {
                    SelectorFragment.adaptador.notifyDataSetChanged();
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
