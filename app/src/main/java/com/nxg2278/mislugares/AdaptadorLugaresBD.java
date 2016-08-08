package com.nxg2278.mislugares;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by nxg2278 on 01/06/2016.
 */
public class AdaptadorLugaresBD extends AdaptadorLugares {

    private Cursor cursor;

    public AdaptadorLugaresBD(Context contexto, Lugares lugares, Cursor cursor) {
        super(contexto, lugares);
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Lugar lugarPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return LugaresBD.extraeLugar(cursor);
    }

    public int idPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return cursor.getInt(0);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lugar lugar = lugarPosicion(position);
        personalizaVista(holder, lugar);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
}
