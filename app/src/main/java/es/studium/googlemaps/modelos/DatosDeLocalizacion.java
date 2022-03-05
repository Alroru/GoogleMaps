package es.studium.googlemaps.modelos;

public class DatosDeLocalizacion {
    private  Integer bateria;
    private double latitud,longitud;

    private Integer id; // El ID de la BD

    public DatosDeLocalizacion(Double latitud,Double longitud,Integer bateria)
    {
        this.latitud=latitud;
        this.longitud=longitud;
        this.bateria=bateria;
    }

    // Constructor para cuando instanciamos desde la BD
    public DatosDeLocalizacion(Integer id,Double latitud,Double longitud, Integer bateria) {
        this.latitud=latitud;
        this.longitud=longitud;
        this.bateria = bateria;
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Integer getBateria() {
        return bateria;
    }

    public void setBateria(Integer bateria) {
        this.bateria = bateria;
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
