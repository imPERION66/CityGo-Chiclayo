package com.greencix.citygo.data.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.model.EducaInsignia;
import com.greencix.citygo.data.model.EducaParticipacion;
import com.greencix.citygo.data.model.EducaPublicacion;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EducaRepository {

    private static EducaRepository instance;
    private final SupabaseApiClient apiClient;
    private final Gson gson;

    private EducaRepository() {
        this.apiClient = SupabaseApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public static synchronized EducaRepository getInstance() {
        if (instance == null) {
            instance = new EducaRepository();
        }
        return instance;
    }

    /**
     * Obtiene las publicaciones activas de Chiclayo Educa, opcionalmente filtradas por tipo
     */
    public void getPublicaciones(String tipoFiltro, Callback<List<EducaPublicacion>> callback) {
        String url = SupabaseConfig.REST_BASE + "educa_publicaciones?activo=eq.true&order=creado_en.desc";
        if (tipoFiltro != null && !tipoFiltro.isEmpty() && !tipoFiltro.equalsIgnoreCase("todos")) {
            url += "&tipo=eq." + tipoFiltro.toLowerCase();
        }

        apiClient.get(url, false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<EducaPublicacion>>() {}.getType();
                    List<EducaPublicacion> list = gson.fromJson(result, listType);
                    if (list == null || list.isEmpty()) {
                        list = getPublicacionesSemilla(tipoFiltro);
                    }
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onSuccess(getPublicacionesSemilla(tipoFiltro));
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getPublicacionesSemilla(tipoFiltro));
            }
        });
    }

    /**
     * Registra un Like / Asistencia y suma puntos de gamificación al usuario
     */
    public void sumarPuntosInteraccion(int puntosASumar, boolean esCharla, Callback<Integer> callback) {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onSuccess(puntosASumar);
            return;
        }

        // Consultar participación actual del usuario
        String urlGet = SupabaseConfig.REST_BASE + "educa_participacion_usuarios?usuario_id=eq." + userId;
        apiClient.get(urlGet, true, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<EducaParticipacion>>() {}.getType();
                    List<EducaParticipacion> list = gson.fromJson(result, listType);
                    if (list != null && !list.isEmpty()) {
                        EducaParticipacion part = list.get(0);
                        int nuevoPuntaje = part.getPuntosAcumulados() + puntosASumar;
                        int nuevosLikes = part.getLikesInteracciones() + (esCharla ? 0 : 1);
                        int nuevasCharlas = part.getCharlasAsistidas() + (esCharla ? 1 : 0);

                        JsonObject update = new JsonObject();
                        update.addProperty("puntos_acumulados", nuevoPuntaje);
                        update.addProperty("likes_interacciones", nuevosLikes);
                        update.addProperty("charlas_asistidas", nuevasCharlas);

                        String patchUrl = SupabaseConfig.REST_BASE + "educa_participacion_usuarios?usuario_id=eq." + userId;
                        apiClient.patch(patchUrl, update.toString(), true, new Callback<String>() {
                            @Override
                            public void onSuccess(String r) {
                                callback.onSuccess(nuevoPuntaje);
                            }

                            @Override
                            public void onError(String err) {
                                callback.onSuccess(nuevoPuntaje);
                            }
                        });
                    } else {
                        // Crear registro inicial
                        JsonObject insert = new JsonObject();
                        insert.addProperty("usuario_id", userId);
                        insert.addProperty("puntos_acumulados", puntosASumar);
                        insert.addProperty("likes_interacciones", esCharla ? 0 : 1);
                        insert.addProperty("charlas_asistidas", esCharla ? 1 : 0);

                        String postUrl = SupabaseConfig.REST_BASE + "educa_participacion_usuarios";
                        apiClient.post(postUrl, insert.toString(), true, new Callback<String>() {
                            @Override
                            public void onSuccess(String r) {
                                callback.onSuccess(puntosASumar);
                            }

                            @Override
                            public void onError(String err) {
                                callback.onSuccess(puntosASumar);
                            }
                        });
                    }
                } catch (Exception e) {
                    callback.onSuccess(puntosASumar);
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(puntosASumar);
            }
        });
    }

    /**
     * Obtiene el progreso de gamificación del usuario
     */
    public void getMiParticipacion(Callback<EducaParticipacion> callback) {
        String userId = SessionManager.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            EducaParticipacion fallback = new EducaParticipacion("anonimo", 120);
            fallback.setLikesInteracciones(5);
            fallback.setCharlasAsistidas(1);
            callback.onSuccess(fallback);
            return;
        }

        String url = SupabaseConfig.REST_BASE + "educa_participacion_usuarios?usuario_id=eq." + userId;
        apiClient.get(url, true, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<EducaParticipacion>>() {}.getType();
                    List<EducaParticipacion> list = gson.fromJson(result, listType);
                    if (list != null && !list.isEmpty()) {
                        callback.onSuccess(list.get(0));
                    } else {
                        EducaParticipacion nuevo = new EducaParticipacion(userId, 80);
                        callback.onSuccess(nuevo);
                    }
                } catch (Exception e) {
                    EducaParticipacion fallback = new EducaParticipacion(userId, 80);
                    callback.onSuccess(fallback);
                }
            }

            @Override
            public void onError(String errorMessage) {
                EducaParticipacion fallback = new EducaParticipacion(userId, 80);
                callback.onSuccess(fallback);
            }
        });
    }

    /**
     * Obtiene la lista de insignias según el rol
     */
    public void getInsignias(String rol, Callback<List<EducaInsignia>> callback) {
        String rolSanitizado = (rol != null && !rol.isEmpty()) ? rol.toLowerCase() : "ciudadano";
        String url = SupabaseConfig.REST_BASE + "educa_insignias?rol_aplicable=eq." + rolSanitizado + "&order=puntos_requeridos.asc";
        apiClient.get(url, false, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Type listType = new TypeToken<List<EducaInsignia>>() {}.getType();
                    List<EducaInsignia> list = gson.fromJson(result, listType);
                    if (list == null || list.isEmpty()) {
                        list = getInsigniasSemilla(rolSanitizado);
                    }
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onSuccess(getInsigniasSemilla(rolSanitizado));
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getInsigniasSemilla(rolSanitizado));
            }
        });
    }

    /**
     * Publicar nuevo contenido desde el panel CMS de Administrador
     */
    public void crearPublicacion(String titulo, String descripcion, String tipo, String multimediaUrl, String enlaceReunion, Callback<Void> callback) {
        String userId = SessionManager.getInstance().getUserId();
        JsonObject body = new JsonObject();
        body.addProperty("titulo", titulo);
        body.addProperty("descripcion", descripcion);
        body.addProperty("tipo", tipo != null ? tipo.toLowerCase() : "aviso");
        body.addProperty("multimedia_url", multimediaUrl != null ? multimediaUrl : "");
        body.addProperty("enlace_reunion", enlaceReunion != null ? enlaceReunion : "");
        if (userId != null) body.addProperty("creado_por", userId);
        body.addProperty("activo", true);

        String url = SupabaseConfig.REST_BASE + "educa_publicaciones";
        apiClient.post(url, body.toString(), true, new Callback<String>() {
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
     * Eliminar o desactivar publicación obsoleta (Admin CMS)
     */
    public void eliminarPublicacion(String id, Callback<Void> callback) {
        JsonObject body = new JsonObject();
        body.addProperty("activo", false);
        String url = SupabaseConfig.REST_BASE + "educa_publicaciones?id=eq." + id;
        apiClient.patch(url, body.toString(), true, new Callback<String>() {
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

    // Datos semilla para Chiclayo Educa
    private List<EducaPublicacion> getPublicacionesSemilla(String filtro) {
        List<EducaPublicacion> list = new ArrayList<>();
        list.add(new EducaPublicacion("1", "🚛 Horario Especial de Feriados en Chiclayo", "Durante las festividades patronales el servicio diurno de recolección operará con normalidad en Chiclayo Cercado, JLO y La Victoria.", "aviso", ""));
        list.add(new EducaPublicacion("2", "♻️ Guía Práctica de Segregación en la Fuente", "Aprende a clasificar botellas plásticas PET, latas y residuos orgánicos para facilitar el trabajo de los recicladores urbanos.", "guia_reciclaje", ""));
        list.add(new EducaPublicacion("3", "🎥 Video: El Viaje de tus Residuos en Chiclayo", "Conoce cómo funciona el relleno sanitario municipal y la ruta de los camiones compactadores Volvo.", "video", ""));
        list.add(new EducaPublicacion("4", "🎙️ Charla en Vivo: Compostaje Urbano Casero", "Únete a la sesión virtual este sábado a las 10:00 AM con especialistas de la Gerencia de Gestión Ambiental.", "charla_vivo", "https://meet.google.com/citygo-chiclayo"));

        if (filtro == null || filtro.isEmpty() || filtro.equalsIgnoreCase("todos")) {
            return list;
        }
        List<EducaPublicacion> filtradas = new ArrayList<>();
        for (EducaPublicacion p : list) {
            if (p.getTipo().equalsIgnoreCase(filtro)) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    private List<EducaInsignia> getInsigniasSemilla(String rol) {
        List<EducaInsignia> list = new ArrayList<>();
        if (rol.equalsIgnoreCase("colaborador")) {
            list.add(new EducaInsignia("c1", "Ruta Puntual", "Cumplimiento del 95% de tramos a tiempo en Chiclayo.", "", 100, "colaborador"));
            list.add(new EducaInsignia("c2", "Conductor Ecológico", "Cero incidentes viales y eficiencia en consumo de combustible.", "", 250, "colaborador"));
            list.add(new EducaInsignia("c3", "Maestro Operativo", "Líder de cuadrilla con más de 50 rutas completadas sin observaciones.", "", 500, "colaborador"));
        } else {
            list.add(new EducaInsignia("1", "Semilla Verde", "Primeros pasos segregando residuos en Chiclayo.", "", 50, "ciudadano"));
            list.add(new EducaInsignia("2", "Guardián Ecológico", "Participación activa en avisos y reporte de puntos críticos.", "", 150, "ciudadano"));
            list.add(new EducaInsignia("3", "Héroe Ambiental Chiclayo", "Máximo nivel de compromiso y asistencia a charlas ambientales.", "", 300, "ciudadano"));
        }
        return list;
    }
}
