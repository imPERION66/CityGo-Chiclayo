package com.greencix.citygo.data.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greencix.citygo.data.model.Ruta;
import com.greencix.citygo.data.model.TramoRuta;
import com.greencix.citygo.data.model.TurnoRecorrido;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import java.lang.reflect.Type;
import java.util.List;

public class RutasRepository {

    private static RutasRepository instance;
    private final SupabaseApiClient apiClient;
    private final Gson gson;

    private RutasRepository() {
        this.apiClient = SupabaseApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public static synchronized RutasRepository getInstance() {
        if (instance == null) {
            instance = new RutasRepository();
        }
        return instance;
    }

    /**
     * Obtiene la lista de rutas activas de Chiclayo
     */
    public void getRutas(Callback<List<Ruta>> callback) {
        String url = SupabaseConfig.REST_BASE + "rutas?activo=eq.true&order=nombre_ruta.asc";
        apiClient.get(url, false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<Ruta>>() {}.getType();
                    List<Ruta> list = gson.fromJson(result, listType);
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError("Error al parsear rutas: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Obtiene los tramos de una ruta específica ordenados por calle
     */
    public void getTramosRuta(String rutaId, Callback<List<TramoRuta>> callback) {
        String url = SupabaseConfig.REST_BASE + "tramos_ruta?ruta_id=eq." + rutaId + "&order=orden.asc";
        apiClient.get(url, false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<TramoRuta>>() {}.getType();
                    List<TramoRuta> list = gson.fromJson(result, listType);
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError("Error al parsear tramos: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Obtiene los turnos activos en tiempo real para visualización ciudadana
     */
    public void getTurnosActivos(Callback<List<TurnoRecorrido>> callback) {
        String url = SupabaseConfig.REST_BASE + "turnos_recorrido?estado=in.(en_curso,retrasado)&order=fecha.desc";
        apiClient.get(url, false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<TurnoRecorrido>>() {}.getType();
                    List<TurnoRecorrido> list = gson.fromJson(result, listType);
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onError("Error al parsear turnos activos: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Actualiza la ubicación GPS del camión y registra minutos de retraso
     */
    public void actualizarGpsTurno(String turnoId, double lat, double lng, int minutosRetraso, Callback<Void> callback) {
        JsonObject params = new JsonObject();
        params.addProperty("p_turno_id", turnoId);
        params.addProperty("p_lat", lat);
        params.addProperty("p_lng", lng);
        params.addProperty("p_minutos_retraso", minutosRetraso);

        apiClient.rpc("actualizar_gps_turno", params.toString(), true, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Solicita una extensión de tiempo con justificación (Colaborador)
     */
    public void solicitarExtensionTiempo(String turnoId, int minutos, String motivo, Callback<String> callback) {
        JsonObject params = new JsonObject();
        params.addProperty("p_turno_id", turnoId);
        params.addProperty("p_minutos", minutos);
        params.addProperty("p_motivo", motivo);

        apiClient.rpc("solicitar_extension_tiempo", params.toString(), true, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
