package com.fran.mislugares2016;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
 * Created by fran on 27/4/16.
 */
public class VistaLugarFragment extends Fragment implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private long id;
    private Lugar lugar;
    private Uri uriFoto;

    final static int RESULTADO_EDITAR= 1;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.vista_lugar, contenedor, false);
        setHasOptionsMenu(true);

        LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.barra_url);
        pUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pgWeb(null);
            }
        });

        LinearLayout pMapa = (LinearLayout) vista.findViewById(R.id.barra_direccion);
        pMapa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verMapa(null);
            }
        });

        LinearLayout pTelefono = (LinearLayout) vista.findViewById(R.id.barra_telefono);
        pTelefono.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                llamadaTelefono(null);
            }
        });

        ImageView pCamara = (ImageView) vista.findViewById(R.id.imagen_camara);
        pCamara.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                tomarFoto(null);
            }
        });

        ImageView pGaleria = (ImageView) vista.findViewById(R.id.imagen_galeria);
        pGaleria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                galeria(
                        null);
            }
        });

        ImageView pEliminar = (ImageView) vista.findViewById(R.id.imagen_eliminar);
        pEliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                eliminarFoto(null);
            }
        });

        ImageView iconoFecha = (ImageView) vista.findViewById(R.id.logo_fecha);
        iconoFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFecha();
            }
        });

        ImageView iconoHora = (ImageView) vista.findViewById(R.id.logo_hora);
        iconoHora.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarHora();
            }
        });

        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
            id = extras.getLong("id", -1);
            if(id != -1) {
                actualizarVistas(id);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vista_lugar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                Intent i = new Intent(getActivity(), EdicionLugarActivity.class);
                i.putExtra("id", id);
                startActivityForResult(i, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                int _id = SelectorFragment.adaptador.idPosicion((int) id);
                borrarLugar((int) _id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(id);
            v.findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA
                && resultCode == Activity.RESULT_OK) {
            lugar.setFoto(data.getDataString());
            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
            actualizaLugar();
        } else if(requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK
                && lugar!=null && uriFoto!=null) {
            lugar.setFoto(uriFoto.toString());
            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
            actualizaLugar();
        }
    }

    private void borrarLugar(final int id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro de que quieres eliminar este lugar?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.lugares.borrar((int) id);
                        SelectorFragment.adaptador.setCursor(
                                MainActivity.lugares.extraeCursor());
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
    }

    public void actualizarVistas(final long id) {
        this.id = id;
        lugar= SelectorFragment.adaptador.lugarPosicion((int) id);
        if(lugar != null) {
            TextView nombre = (TextView) v.findViewById(R.id.nombre);
            nombre.setText(lugar.getNombre());
            ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
            logo_tipo.setImageResource(lugar.getTipo().getRecurso());
            TextView tipo = (TextView) v.findViewById(R.id.tipo);
            tipo.setText(lugar.getTipo().getTexto());

            // direccón
            if ( lugar.getDireccion() == null || lugar.getDireccion().isEmpty()) {
                v.findViewById(R.id.barra_direccion).setVisibility(View.GONE);
            } else {
                TextView direccion = (TextView) v.findViewById(R.id.direccion);
                direccion.setText(lugar.getDireccion());
            }

            // teléfono
            if (lugar.getTelefono() == 0) {
                v.findViewById(R.id.barra_telefono).setVisibility(View.GONE);
            } else {
                TextView telefono = (TextView) v.findViewById(R.id.telefono);
                telefono.setText(Integer.toString(lugar.getTelefono()));
            }

            // url
            if ( lugar.getUrl() == null || lugar.getUrl().isEmpty()) {
                v.findViewById(R.id.barra_url).setVisibility(View.GONE);
            } else {
                TextView url = (TextView) v.findViewById(R.id.url);
                url.setText(lugar.getUrl());
            }

            // comentario
            if ( lugar.getComentario() == null || lugar.getComentario().isEmpty()) {
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
                        @Override public void onRatingChanged(RatingBar ratingBar,
                                                              float valor, boolean fromUser) {
                            lugar.setValoracion(valor);
                            actualizaLugar();
                        }
                    });

            ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(getActivity(), uri, 1024, 1024));
        } else{
            imageView.setImageBitmap(null);
        }
    }

    public void tomarFoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto((ImageView) v.findViewById(R.id.foto), null);
        actualizaLugar();
    }

    public static Bitmap reduceBitmap(Context contexto, String uri,
                                      int maxAncho, int maxAlto) {
        try {
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
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    void actualizaLugar(){
        int _id = SelectorFragment.adaptador.idPosicion((int) id);
        MainActivity.lugares.actualiza(_id, lugar);
        SelectorFragment.adaptador.setCursor(MainActivity.lugares.extraeCursor());
        SelectorFragment.adaptador.notifyItemChanged((int) id);
    }

    public void cambiarFecha() {
        DialogoSelectorFecha dialogoFecha = new DialogoSelectorFecha();
        dialogoFecha.setOnDateSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogoFecha.setArguments(args);
        dialogoFecha.show(getActivity().getSupportFragmentManager(), "selectorFecha");
    }


    public void cambiarHora() {
        DialogoSelectorHora dialogoHora = new DialogoSelectorHora();
        dialogoHora.setOnTimeSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogoHora.setArguments(args);
        dialogoHora.show(getActivity().getSupportFragmentManager(), "selectorHora");
    }

    @Override
    public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tFecha = (TextView) getView().findViewById(R.id.fecha);
        DateFormat formato =  DateFormat.getDateInstance();
        tFecha.setText(formato.format(new Date(lugar.getFecha())));
    }

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
