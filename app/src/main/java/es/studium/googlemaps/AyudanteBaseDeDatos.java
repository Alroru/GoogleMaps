package es.studium.googlemaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AyudanteBaseDeDatos extends SQLiteOpenHelper
{
    private static final String NOMBRE_BASE_DE_DATOS = "PasitosAPP",
            NOMBRE_TABLA_FRASES = "DatosDeLocalizacion";
    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBaseDeDatos(@Nullable Context context, @Nullable String name,@Nullable SQLiteDatabase.CursorFactory factory, @Nullable int version)
    {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "latitud REAL, longitud REAL,bateria INTEGER)", NOMBRE_TABLA_FRASES));
    }
    //Para actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", NOMBRE_TABLA_FRASES));
        onCreate(db);
    }
}
