package es.studium.googlemaps.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import es.studium.googlemaps.AyudanteBaseDeDatos;
import es.studium.googlemaps.modelos.DatosDeLocalizacion;

public class DatosController {
    AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "DatosDeLocalizacion";

    public DatosController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto,NOMBRE_TABLA,null,1);
    }

    public long nuevaUbicacion(DatosDeLocalizacion datos) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("latitud", datos.getLatitud());
        valoresParaInsertar.put("longitud", datos.getLongitud());
        valoresParaInsertar.put("bateria", datos.getBateria());
        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }


    public ArrayList<DatosDeLocalizacion> obtenerPosicion() {
        ArrayList<DatosDeLocalizacion> posiciones = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos =ayudanteBaseDeDatos.getReadableDatabase();

        // SELECT texto, autor, id
        String[] columnasAConsultar = {"id", "latitud", "longitud", "bateria"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor == null) {
            /*
            Salimos aquí porque hubo un error, regresar
            lista vacía
            */
            return posiciones;
        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst())
        {
            return posiciones;
        }

        // En caso de que sí haya, iteramos y vamos agregando los
        // datos a la lista de pocisiones
        do
        {
            // El 0 es el número de la columna, como seleccionamos
            // texto, autor,id entonces el texto es 0, autor 1 e id es 2

            Integer idPasitosAPP = Integer.parseInt((cursor.getString(0)));
            Double latitudPasitosAPP = Double.valueOf(cursor.getString(1));
            Double longitudPasitosAPP = Double.valueOf(cursor.getString(2));
            Integer bateriaPasitosAPP = Integer.parseInt((cursor.getString(3)));

            DatosDeLocalizacion datosDeLocalizacion = new DatosDeLocalizacion(idPasitosAPP, latitudPasitosAPP,longitudPasitosAPP, bateriaPasitosAPP );
            posiciones.add(datosDeLocalizacion);
        }while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista de frases :)
        cursor.close();
        return posiciones;
    }


}
