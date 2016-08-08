package com.nxg2278.mislugares;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by nxg2278 on 24/05/2016.
 */
public class EdicionLugarActivity extends AppCompatActivity {

    private long id;
    private Lugar lugar;
    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;
    //id cuando anyadimos un nuevo lugar
    private long _id;

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

        nombre = (EditText) findViewById(R.id.et_nombre);
        nombre.setText(lugar.getNombre());

        tipo = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());

        direccion = (EditText) findViewById(R.id.et_dir);
        direccion.setText(lugar.getDireccion());

        telefono = (EditText) findViewById(R.id.et_tlf);
        telefono.setText(Long.toString(lugar.getTelefono()));

        url = (EditText) findViewById(R.id.et_url);
        url.setText(lugar.getUrl());

        comentario = (EditText) findViewById(R.id.et_coment);
        comentario.setText(lugar.getComentario());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.guardar:
                lugar.setNombre(nombre.getText().toString());
                lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                lugar.setDireccion(direccion.getText().toString());
                lugar.setTelefono(Long.parseLong(telefono.getText().toString()));
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comentario.getText().toString());
                new AlertDialog.Builder(this)
                        .setTitle("Edicion del lugar")
                        .setMessage("Estas seguro que quieres editar este lugar?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Actualiza el lugar en base de datos
                                if (_id==-1) {
                                    _id = SelectorFragment.adaptador.idPosicion((int) id);
                                }
                                MainActivity.lugares.actualiza((int) _id, lugar);
                                //Actualiza el lugar en VistaLugarActivity
                                SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
                                //Actualiza el lugar en MainActivity
                                if (_id!= -1) {
                                    SelectorFragment.adaptador.notifyItemChanged((int) id);
                                } else {
                                    SelectorFragment.adaptador.notifyDataSetChanged();
                                }

                                Intent i = new Intent(getBaseContext(), VistaLugarActivity.class);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;

            case R.id.cancelar:
                if (_id!=-1) {
                    MainActivity.lugares.borrar((int) _id);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Configurar boton atras para que borre el nuevo lugar creado
    @Override
    public void onBackPressed() {
        if (_id!=-1) {
            MainActivity.lugares.borrar((int) _id);
        }
        finish();
    }
}
