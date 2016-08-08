package com.nxg2278.mislugares;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nxg2278 on 20/05/2016.
 */
public class VistaLugarFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private static final int RESULT_OK = -1;
    private long id;
    private Lugar lugar;
    private View v;
    private final int SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE = 0;

    //atributos anadir imagenes
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;
    private Uri uriFoto;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.vista_lugar, container, false);
        setHasOptionsMenu(true);
        LinearLayout pDir = (LinearLayout) vista.findViewById(R.id.barra_direccion);
        pDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verMapa(null);
            }
        });
        LinearLayout pTlf = (LinearLayout) vista.findViewById(R.id.barra_telefono);
        pTlf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamadaTelefono(null);
            }
        });
        //Configuracion onClicks vista_lugar.xml
        LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.barra_url);
        pUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgWeb(null);
            }
        });
        ImageView menuCamera = (ImageView) vista.findViewById(R.id.ic_camera);
        menuCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto(null);
            }
        });
        ImageView menuGaleria = (ImageView) vista.findViewById(R.id.ic_galery);
        menuGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galeria(null);
            }
        });
        ImageView menuEliminar = (ImageView) vista.findViewById(R.id.ic_delete);
        menuEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFoto(null);
            }
        });
        ImageView iconoHora = (ImageView) vista.findViewById(R.id.logo_hora);
        iconoHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarHora();
            }
        });
        ImageView iconoFecha = (ImageView) vista.findViewById(R.id.logo_fecha);
        iconoFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarFecha();
            }
        });
        return vista;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id", -1);
            if (id != -1){
                actualizarVistas(id);
            }
        }
    }

    public void cambiarHora() {
        DialogoSelectorHora dialogoHora = new DialogoSelectorHora();
        dialogoHora.setOnTimeSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogoHora.setArguments(args);
        dialogoHora.show(getActivity().getSupportFragmentManager(), "selectorHora");
    }

    public void cambiarFecha() {

    }

    public void actualizarVistas(final long id) {
        this.id = id;
        lugar = SelectorFragment.adaptador.lugarPosicion((int) id);

        if (lugar != null) {

            Log.v("id", String.valueOf(id));
            Log.v("lugar", lugar.toString());

            TextView nombre = (TextView) v.findViewById(R.id.nombre);
            nombre.setText(lugar.getNombre());

            ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
            logo_tipo.setImageResource(lugar.getTipo().getRecurso());

            TextView tipo = (TextView) v.findViewById(R.id.tipo);
            tipo.setText(lugar.getTipo().getTexto());

            if (lugar.getDireccion().isEmpty()) {
                v.findViewById(R.id.barra_direccion).setVisibility(View.GONE);
            } else {
                TextView direccion = (TextView) v.findViewById(R.id.direccion);
                direccion.setText(lugar.getDireccion());
            }

            if (lugar.getTelefono() == 0) {
                v.findViewById(R.id.barra_telefono).setVisibility(View.GONE);
            } else {
                TextView telefono = (TextView) v.findViewById(R.id.telefono);
                telefono.setText(Long.toString(lugar.getTelefono()));
            }

            if (lugar.getUrl().isEmpty()) {
                v.findViewById(R.id.barra_url).setVisibility(View.GONE);
            } else {
                TextView url = (TextView) v.findViewById(R.id.url);
                url.setText(lugar.getUrl());
            }

            if (lugar.getComentario().isEmpty()) {
                v.findViewById(R.id.barra_comentario).setVisibility(View.GONE);
            } else {
                TextView comentario = (TextView) v.findViewById(R.id.comentario);
                comentario.setText(lugar.getComentario());
            }

            TextView fecha = (TextView) v.findViewById(R.id.fecha);
            fecha.setText(DateFormat.getDateInstance().format(
                    new Date(lugar.getFecha())));

            TextView hora = (TextView) v.findViewById(R.id.hora);
            hora.setText(DateFormat.getTimeInstance().format(
                    new Date(lugar.getFecha())));

            RatingBar valoracion = (RatingBar) v.findViewById(R.id.valoracion);
            valoracion.setOnRatingBarChangeListener(null);
            valoracion.setRating(lugar.getValoracion());
            valoracion.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                            lugar.setValoracion(valor);
                            actualizaLugar();
                        }
                    });

            //Configurar imagen lugar desde base de datos
            /*if (lugar.getNombre().contains("Escuela")) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.foto_epsg, null));
            } else {*/
            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
            //}
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vista_lugar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Configuracion menu de arriba
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i;

        switch (item.getItemId()){
            case R.id.accion_compartir:
                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(i);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                i = new Intent(getActivity(), EdicionLugarActivity.class);
                i.putExtra("id", (long) id);
                startActivityForResult(i, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Borrado de lugar")
                        .setMessage("Estas seguro que quieres eliminar este lugar?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Obtengo el id en bbdd
                                int _id = SelectorFragment.adaptador.idPosicion((int) id);
                                //Borro el lugar
                                MainActivity.lugares.borrar(_id);
                                //Actualizo el cursor de busqueda de bbdd
                                SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
                                //Notifico cambio para que se refleje en recyclerView
                                SelectorFragment.adaptador.notifyDataSetChanged();
                                SelectorFragment selectorFragment = (SelectorFragment) getActivity().
                                        getSupportFragmentManager().findFragmentById(R.id.selector_fragment);
                                if (selectorFragment == null) {
                                    getActivity().finish();
                                } else {
                                    ((MainActivity) getActivity()).muestraLugar(0);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:"+ lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
    }

    //Configuracion cuando retornamos de una actividad
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(id);
            v.findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA && resultCode == RESULT_OK) {
            lugar.setFoto(data.getDataString());
            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
            actualizaLugar();
        }else if (requestCode == RESULTADO_FOTO && resultCode == RESULT_OK) {
            lugar.setFoto(uriFoto.toString());
            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
            actualizaLugar();
        }
    }

    public void actualizaLugar() {
        int _id = SelectorFragment.adaptador.idPosicion((int) id);
        MainActivity.lugares.actualiza(_id,lugar);
        SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
        SelectorFragment.adaptador.notifyItemChanged((int) id);
    }

    protected void ponerFoto (ImageView imageView, String uri){
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(getActivity(), uri, 1024, 1024));
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public static Bitmap reduceBitmap (Context contexto, String uri, int maxAncho, int maxAlto) {
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            //Toast.makeText(contexto, "Fichero/recurso no encontrado",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    //Elegir foto de la galeria
    public void galeria (View view) {
        //Comprobar permiso de acceder a alamacenamiento externo para pedir permiso (Version sup a 6.0)
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULTADO_GALERIA);
        } else {
            solicitarPermisoAlmacenamientoExterno();
        }

    }
    public void solicitarPermisoAlmacenamientoExterno () {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(v, "Sin el permiso administrar llamadas no puedo"
                    +" escoger una foto de tu Galeria", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{ Manifest.permission. READ_EXTERNAL_STORAGE},
                                    SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE);
        }
    }

    //Hacer foto desde la camara
    public void tomarFoto (View view) {
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator +
                "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        i.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(i, RESULTADO_FOTO);
    }

    //Eliminar foto
    public void eliminarFoto (View view) {
        lugar.setFoto(null);
        ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
        actualizaLugar();
    }

    //Configurar calendario
    @Override
    public void onTimeSet(TimePicker vista, int hora, int minuto) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.HOUR_OF_DAY, hora);
        calendario.set(Calendar.MINUTE, minuto);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tHora = (TextView) getView().findViewById(R.id.hora);
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm",
                java.util.Locale.getDefault());
        tHora.setText(formato.format(new Date(lugar.getFecha())));
    }
}
