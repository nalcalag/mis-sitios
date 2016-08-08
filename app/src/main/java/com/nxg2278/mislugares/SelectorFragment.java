package com.nxg2278.mislugares;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nxg2278 on 30/06/2016.
 */
public class SelectorFragment extends Fragment {
    private RecyclerView recyclerView;
    public static AdaptadorLugaresBD adaptador;

    //Configuramos las vistas y el recyclerView de MainActivity pero ahora para Fragments

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_selector, container, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        return vista;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true); //Quitar esta línea si da problemas
        adaptador = new AdaptadorLugaresBD(getContext(), MainActivity.lugares, MainActivity.lugares.extraeCursor());
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).muestraLugar(recyclerView.getChildAdapterPosition(v));
            }
        });

        recyclerView.setAdapter(adaptador);
    }
}
