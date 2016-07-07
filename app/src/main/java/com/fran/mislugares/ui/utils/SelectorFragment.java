package com.fran.mislugares.ui.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fran.mislugares.R;
import com.fran.mislugares.ui.mainview.MainActivity;
import com.fran.mislugares.ui.mainview.adapters.AdaptadorLugaresBD;

/**
 * Created by fran on 31/5/16.
 */
public class SelectorFragment extends Fragment {

    private RecyclerView recyclerView;
    public static AdaptadorLugaresBD adaptador;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView =(RecyclerView) vista.findViewById(R.id.recycler_view);
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true); //Quitar esta línea si da problemas
        adaptador = new AdaptadorLugaresBD(getContext(),
                MainActivity.lugares,  MainActivity.lugares.extraeCursor());
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) getActivity()).muestraLugar(recyclerView.getChildAdapterPosition(v));
            }
        });
        recyclerView.setAdapter(adaptador);
    }
}
