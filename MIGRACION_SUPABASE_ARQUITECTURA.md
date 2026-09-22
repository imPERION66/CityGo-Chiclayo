# 📑 Arquitectura y Plan de Migración: Firebase a Supabase (CityGo)

**Proyecto:** CityGo - Gestor de Residuos Urbanos (Chiclayo, Perú)  
**Versión de Documento:** 1.0  
**Fecha:** Septiembre 2026  
**Autor:** Antigravity AI & Equipo de Desarrollo CityGo  

---

## 1. 🌆 Resumen y Dominio de la Aplicación

### 1.1. Propósito y Contexto
**CityGo** es una solución móvil orientada a la gestión inteligente, monitoreo y tratamiento de residuos sólidos urbanos para la ciudad de **Chiclayo**. Su objetivo es modernizar la interacción entre la ciudadanía, el personal operativo de limpieza pública (conductores y recolectores) y los administradores municipales/organizacionales.

### 1.2. Módulos y Roles del Sistema

```
                            ┌────────────────────────┐
                            │    ROLES EN CITYGO     │
                            └───────────┬────────────┘
         ┌──────────────────────────────┼──────────────────────────────┐
         ▼                              ▼                              ▼
┌──────────────────┐          ┌──────────────────┐          ┌──────────────────┐
│ 1. CIUDADANO     │          │ 2. COLABORADOR   │          │ 3. ADMINISTRADOR │
│ (Público)        │          │ (Conductor)      │          │ (Gestión Total)  │
├──────────────────┤          ├──────────────────┤          ├──────────────────┤
│• Registro Libre  │          │• Código Activac. │          │• Código Activac. │
│• Monitoreo GPS   │          │• Recorrido y Ruta│          │• Gestión Choferes│
│  Camión en vivo  │          │  Asignada        │• Asignar Rutas   │
│• Reporte Basura/ │          │• Control Tiempos │• Régimen Sanción │
│  Incidentes      │          │  por Calle/Tramo │  (Cláusula C3)   │
│• Puntos Limpios  │          │• Alerta Retrasos │• Generar Códigos │
│• Notificaciones  │          │• Extensión Tiempo│• Reportes Flota  │
└──────────────────┘          └──────────────────┘          └──────────────────┘
```

#### A. Rol Ciudadano (Usuario Común)
- **Registro:** Acceso libre creando cuenta con datos personales.
- **Módulo Horario & Recorrido:** Visualización en tiempo real sobre el mapa de la ubicación actual del camión recolector en su sector o cuadrante de Chiclayo (evitando la acumulación prolongada de basura en las calles).
- **Reportes Ciudadanos:** Notificación de puntos críticos de basura o desmonte con geolocalización y evidencia fotográfica.
- **Educación Ambiental:** Consulta de horarios de recolección y puntos de reciclaje cercanos.

#### B. Rol Colaborador (Conductor / Personal Operativo)
- **Acceso:** Restringido mediante validación previa de código de activación empresarial otorgado por la administración.
- **Módulo Horario & Ruta de Trabajo:**
  - Visualización del itinerario predeterminado (calles, avenidas y zonas de Chiclayo: Centro, José Leonardo Ortiz, La Victoria, etc.).
  - Tiempos estipulados por tramo o calle.
  - **Sistema de Monitoreo de Tiempos en Vivo:** Si el conductor experimenta retrasos respecto a la ventana horaria estimada, la app genera una alerta automática.
  - **Extensión de Tiempo:** Opción interactiva para justificar y solicitar prórrogas temporales (tráfico pesado, fallas mecánicas, saturación de tolva, etc.).
  - **Transmisión de Ubicación:** Envío constante de coordenadas GPS a la base de datos mientras el turno de recolección esté activo.

#### C. Rol Administrador (Supervisores y Autoridades)
- **Acceso:** Restringido mediante código de seguridad de alto nivel.
- **Gestión de Personal y Flota:** Alta, baja y asignación de colaboradores a camiones y rutas.
- **Generación y Auditoría de Códigos de Activación:** Creación de tokens temporales de un solo uso para autorizar nuevos colaboradores y administradores.
- **Régimen Disciplinario y Sanciones (según Cláusula C3 de Términos de Uso):**
  - *Infracción Leve:* Emisión de amonestaciones digitales dentro de la app.
  - *Infracción Grave:* Inhabilitación temporal de la cuenta (3 a 15 días calendario) por incumplimientos reiterados o falsificación de reportes.
  - *Infracción Crítica:* Suspensión definitiva e irrevocable de la cuenta por fraude, sabotaje o suplantación de credenciales.

---

### 1.3. Estado Actual de Pantallas y Componentes (Android UI)

| Componente / Pantalla | Archivo Fuente / Layout | Estado Actual | Función en el Flujo |
| :--- | :--- | :--- | :--- |
| **Splash Screen** | `SplashScreen.java`<br>`activity_splash_screen.xml` | Funcional (temporizador de 7s hacia Login) | Presentación con branding y animaciones. |
| **Login** | `MainActivity.java`<br>`activity_main.xml` | Bloqueado / Incompleto | Entrada con correo y clave. Actualmente solo muestra un Toast y redirecciona a Registro. |
| **Registro** | `Registro.java`<br>`activity_registro.xml` | UI Completa / Sin backend | Formulario de captura (Nombre, Apellido, Correo, Clave) con animación Lottie (`administrao.json`). Redirige a Selección de Rol. |
| **Selección de Rol** | `SeleccionRol.java`<br>`activity_seleccion_rol.xml` | UI Completa / Lógica parcial | Tarjetas seleccionables: *Ciudadano*, *Colaborador*, *Administrador*. Redirige a Términos o Validación según rol. |
| **Validación de Código** | `ValidacionCodigo.java`<br>`activity_validacion_codigo.xml` | Simulado con Strings duros (`ADMIN2025`, `COLAB2025`) | Interfaz para canjear código de activación requerido por colaboradores/admins antes de crear cuenta. |
| **Términos de Uso** | `TerminosUso.java`<br>`activity_terminos_uso.xml` | UI Completa con Cláusulas Legales | Contrato obligatorio con cláusulas C1 a C5 (incluye régimen de sanciones C3). |
| **Módulos Pendientes** | *Dashboard, Horario/Mapa en vivo, Reportes, Alertas, Panel Admin* | Por implementar tras la migración de datos | Núcleo operativo de la app. |

---

## 2. 🔍 Auditoría de la Implementación Firebase

### 2.1. Configuración y Dependencias Identificadas
- **Configuración:** `app/google-services.json` asociado al proyecto `gestorapp-citygo` (ID de app `1:876597260348:android:d2339132c3812f52691339`).
- **Librerías en `libs.versions.toml` y `app/build.gradle`:**
  - `firebase-bom:34.19.0`
  - `firebase-auth` (utilizado para autenticación básica)
  - `firebase-firestore` (utilizado para persistencia NoSQL)
  - `firebase-storage` (declarado para imágenes y videos de incidentes)
  - `firebase-analytics:23.2.0`
  - Plugin `com.google.gms.google-services` en `app/build.gradle`.

### 2.2. Estructuras NoSQL Previstas en el Código Existente
En el código fuente actual y los comentarios de diseño se detectaron las siguientes intenciones de colecciones NoSQL:
1. `usuarios`: Almacenamiento plano de nombre, apellido, correo, rol y estado.
2. `codigos_activacion`: Documentos con campo `codigo`, `rol`, `usado: boolean`.
3. `recorridos` / `horarios`: Documentos no estructurados para rastreo de rutas.
4. `reportes`: Documentos con URLs de imágenes en Firebase Storage.

### 2.3. Problemas y Limitaciones del Enfoque Firebase Actual
1. **Bloqueo en Pantalla de Login / Auth desincronizado:** Las llamadas a Firebase Auth quedaron a medio camino sin un manejador de sesión robusto ni sincronización relacional con el perfil de usuario.
2. **Falta de Integridad Relacional:** En Firestore, validar un código de activación de un solo uso o aplicar una sanción temporal (3 a 15 días) requiere transacciones del lado del cliente o Cloud Functions (las cuales requieren plan de pago Blaze).
3. **Complejidad en Consultas Geoespaciales:** Realizar consultas espaciales en tiempo real (ej. *camión más cercano a mi ubicación en Chiclayo*) en Firestore es complejo y requiere librerías auxiliares como GeoFirestore o hash-geos; mientras que PostgreSQL cuenta con soporte nativo mediante PostGIS / operadores de latitud y longitud indexados.
4. **Seguridad Frágil (Security Rules):** Las reglas de Firestore se vuelven difíciles de mantener cuando intervienen 3 roles con permisos condicionales por estado de cuenta (bloqueado/sancionado).

---

## 3. 🐘 Mapeo y Arquitectura Supabase (PostgreSQL)

La migración a Supabase proporciona una base de datos relacional robusta (**PostgreSQL 15+**), autenticación integrada (`auth.users`), seguridad a nivel de filas (**Row Level Security - RLS**), procedimientos almacenados seguros (**RPC en PL/pgSQL**) y almacenamiento de objetos (**Supabase Storage**) para evidencias fotográficas.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        SUPABASE AUTH (auth.users)                      │
│                (Gestiona UUID, Email, Password Seguro, JWT)            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ (Trigger Automático on INSERT)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      TABLA PÚBLICA: perfiles                           │
│  (id [FK auth.users], nombre, apellido, email, rol, estado_cuenta)     │
└──────┬──────────────┬───────────────────┬──────────────────────────────┘
       │              │                   │
       │ (Asignado)   │ (Aplica Sanción)  │ (Emite Reporte)
       ▼              ▼                   ▼
┌──────────────┐┌───────────────────┐┌───────────────────────────────────┐
│turnos_recorr.││infracciones_sanc. ││       reportes_ciudadanos         │
│(GPS en vivo, ││(Leve, Grave 3-15d,││(Foto en Storage, Tipo Incidente,  │
│tiempos ruta) ││Crítica-Suspensión)││ geolocalización, estado)          │
└──────┬───────┘└───────────────────┘└───────────────────────────────────┘
       │
       ▼ (Solicita prórroga)
┌───────────────────────────────────┐
│ solicitudes_extension_tiempo     │
│ (Minutos extras, justificación)   │
└───────────────────────────────────┘
```

---

### 3.1. Diseño Relacional Normalizado

#### Tipos ENUM Personalizados
- `rol_usuario`: `'ciudadano'`, `'colaborador'`, `'administrador'`
- `estado_cuenta`: `'activo'`, `'suspendido_temporal'`, `'bloqueado_definitivo'`, `'pendiente_activacion'`
- `tipo_infraccion`: `'leve'`, `'grave'`, `'critica'`
- `estado_turno`: `'programado'`, `'en_curso'`, `'completado'`, `'retrasado'`, `'cancelado'`
- `estado_solicitud`: `'pendiente'`, `'aprobada'`, `'rechazada'`
- `estado_reporte`: `'recibido'`, `'en_atencion'`, `'resuelto'`, `'descartado'`

#### Tablas del Sistema

1. **`perfiles` (Extensión de `auth.users`)**:
   - `id`: `UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE`
   - `nombre`: `VARCHAR(100) NOT NULL`
   - `apellido`: `VARCHAR(100) NOT NULL`
   - `correo`: `VARCHAR(150) UNIQUE NOT NULL`
   - `telefono`: `VARCHAR(20)`
   - `rol`: `rol_usuario NOT NULL DEFAULT 'ciudadano'`
   - `estado_cuenta`: `estado_cuenta NOT NULL DEFAULT 'activo'`
   - `terminos_aceptados`: `BOOLEAN NOT NULL DEFAULT TRUE`
   - `fecha_terminos_aceptados`: `TIMESTAMPTZ DEFAULT NOW()`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`
   - `actualizado_en`: `TIMESTAMPTZ DEFAULT NOW()`

2. **`codigos_activacion`**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `codigo`: `VARCHAR(20) UNIQUE NOT NULL`
   - `rol_destino`: `rol_usuario NOT NULL` (solo `colaborador` o `administrador`)
   - `creado_por`: `UUID REFERENCES perfiles(id)`
   - `usado_por`: `UUID REFERENCES perfiles(id)`
   - `usado`: `BOOLEAN DEFAULT FALSE`
   - `fecha_expiracion`: `TIMESTAMPTZ`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

3. **`camiones`**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `placa`: `VARCHAR(15) UNIQUE NOT NULL`
   - `modelo`: `VARCHAR(50)`
   - `capacidad_toneladas`: `NUMERIC(5,2) DEFAULT 5.0`
   - `activo`: `BOOLEAN DEFAULT TRUE`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

4. **`rutas`**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `nombre_ruta`: `VARCHAR(100) NOT NULL` (ej. "Ruta 01 - Chiclayo Centro", "Ruta 04 - José Leonardo Ortiz")
   - `zona_chiclayo`: `VARCHAR(100) NOT NULL`
   - `duracion_estimada_minutos`: `INTEGER NOT NULL DEFAULT 120`
   - `descripcion`: `TEXT`
   - `activo`: `BOOLEAN DEFAULT TRUE`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

5. **`tramos_ruta` (Puntos de control por calle para alertar retrasos)**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `ruta_id`: `UUID NOT NULL REFERENCES rutas(id) ON DELETE CASCADE`
   - `orden`: `INTEGER NOT NULL`
   - `nombre_calle`: `VARCHAR(150) NOT NULL`
   - `tiempo_estimado_minutos`: `INTEGER NOT NULL`
   - `latitud`: `DOUBLE PRECISION NOT NULL`
   - `longitud`: `DOUBLE PRECISION NOT NULL`

6. **`turnos_recorrido` (Seguimiento de camión y control en tiempo real)**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `ruta_id`: `UUID NOT NULL REFERENCES rutas(id)`
   - `camion_id`: `UUID NOT NULL REFERENCES camiones(id)`
   - `conductor_id`: `UUID NOT NULL REFERENCES perfiles(id)`
   - `fecha`: `DATE NOT NULL DEFAULT CURRENT_DATE`
   - `hora_inicio_programada`: `TIME NOT NULL`
   - `hora_fin_programada`: `TIME NOT NULL`
   - `hora_inicio_real`: `TIMESTAMPTZ`
   - `hora_fin_real`: `TIMESTAMPTZ`
   - `estado`: `estado_turno DEFAULT 'programado'`
   - `latitud_actual`: `DOUBLE PRECISION`
   - `longitud_actual`: `DOUBLE PRECISION`
   - `minutos_retraso_estimado`: `INTEGER DEFAULT 0`
   - `ultima_actualizacion_gps`: `TIMESTAMPTZ`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

7. **`solicitudes_extension_tiempo`**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `turno_id`: `UUID NOT NULL REFERENCES turnos_recorrido(id) ON DELETE CASCADE`
   - `conductor_id`: `UUID NOT NULL REFERENCES perfiles(id)`
   - `minutos_solicitados`: `INTEGER NOT NULL CHECK (minutos_solicitados > 0)`
   - `motivo`: `TEXT NOT NULL`
   - `estado`: `estado_solicitud DEFAULT 'pendiente'`
   - `revisado_por`: `UUID REFERENCES perfiles(id)`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

8. **`infracciones_sanciones` (Aplicación de Cláusula C3 de Términos de Uso)**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `usuario_id`: `UUID NOT NULL REFERENCES perfiles(id)`
   - `tipo_infraccion`: `tipo_infraccion NOT NULL`
   - `motivo`: `TEXT NOT NULL`
   - `dias_suspension`: `INTEGER DEFAULT 0`
   - `fecha_inicio`: `TIMESTAMPTZ DEFAULT NOW()`
   - `fecha_fin`: `TIMESTAMPTZ`
   - `activo`: `BOOLEAN DEFAULT TRUE`
   - `aplicado_por`: `UUID REFERENCES perfiles(id)`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

9. **`reportes_ciudadanos`**:
   - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
   - `ciudadano_id`: `UUID NOT NULL REFERENCES perfiles(id)`
   - `tipo_incidente`: `VARCHAR(50) NOT NULL`
   - `descripcion`: `TEXT NOT NULL`
   - `foto_url`: `TEXT`
   - `latitud`: `DOUBLE PRECISION NOT NULL`
   - `longitud`: `DOUBLE PRECISION NOT NULL`
   - `direccion_referencia`: `TEXT`
   - `estado`: `estado_reporte DEFAULT 'recibido'`
   - `atendido_por`: `UUID REFERENCES perfiles(id)`
   - `creado_en`: `TIMESTAMPTZ DEFAULT NOW()`

10. **`puntos_reciclaje`**:
    - `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
    - `nombre`: `VARCHAR(100) NOT NULL`
    - `tipo_residuo`: `VARCHAR(100) NOT NULL`
    - `direccion`: `TEXT NOT NULL`
    - `latitud`: `DOUBLE PRECISION NOT NULL`
    - `longitud`: `DOUBLE PRECISION NOT NULL`
    - `horario_atencion`: `VARCHAR(100)`
    - `activo`: `BOOLEAN DEFAULT TRUE`

---

### 3.2. Script SQL de Inicialización Completo para Supabase

A continuación se encuentra el script DDL/DML que se ejecuta en la consola de Supabase (**SQL Editor**):

```sql
-- ====================================================================
-- SCRIPT DE MIGRACIÓN: PROYECTO CITYGO (CHICLAYO)
-- PLATAFORMA: SUPABASE / POSTGRESQL 15+
-- ====================================================================

-- 1. TIPOS ENUM PERSONALIZADOS
CREATE TYPE public.rol_usuario AS ENUM ('ciudadano', 'colaborador', 'administrador');
CREATE TYPE public.estado_cuenta AS ENUM ('activo', 'suspendido_temporal', 'bloqueado_definitivo', 'pendiente_activacion');
CREATE TYPE public.tipo_infraccion AS ENUM ('leve', 'grave', 'critica');
CREATE TYPE public.estado_turno AS ENUM ('programado', 'en_curso', 'completado', 'retrasado', 'cancelado');
CREATE TYPE public.estado_solicitud AS ENUM ('pendiente', 'aprobada', 'rechazada');
CREATE TYPE public.estado_reporte AS ENUM ('recibido', 'en_atencion', 'resuelto', 'descartado');

-- 2. TABLA DE PERFILES (SINCRONIZADA CON SUPABASE AUTH)
CREATE TABLE public.perfiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    rol public.rol_usuario NOT NULL DEFAULT 'ciudadano',
    estado_cuenta public.estado_cuenta NOT NULL DEFAULT 'activo',
    terminos_aceptados BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_terminos_aceptados TIMESTAMPTZ DEFAULT NOW(),
    creado_en TIMESTAMPTZ DEFAULT NOW(),
    actualizado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 3. TABLA DE CÓDIGOS DE ACTIVACIÓN (ROLES RESTRINGIDOS)
CREATE TABLE public.codigos_activacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(20) UNIQUE NOT NULL,
    rol_destino public.rol_usuario NOT NULL,
    creado_por UUID REFERENCES public.perfiles(id),
    usado_por UUID REFERENCES public.perfiles(id),
    usado BOOLEAN DEFAULT FALSE,
    fecha_expiracion TIMESTAMPTZ,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 4. TABLA DE CAMIONES RECOLECTORES
CREATE TABLE public.camiones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(15) UNIQUE NOT NULL,
    modelo VARCHAR(50),
    capacidad_toneladas NUMERIC(5,2) DEFAULT 5.0,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 5. TABLA DE RUTAS URBANAS EN CHICLAYO
CREATE TABLE public.rutas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre_ruta VARCHAR(100) NOT NULL,
    zona_chiclayo VARCHAR(100) NOT NULL,
    duracion_estimada_minutos INTEGER NOT NULL DEFAULT 120,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 6. TABLA DE TRAMOS DE RUTA (CONTROL DE TIEMPOS POR CALLE)
CREATE TABLE public.tramos_ruta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id UUID NOT NULL REFERENCES public.rutas(id) ON DELETE CASCADE,
    orden INTEGER NOT NULL,
    nombre_calle VARCHAR(150) NOT NULL,
    tiempo_estimado_minutos INTEGER NOT NULL,
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL
);

-- 7. TABLA DE TURNOS DE RECORRIDO (SEGUIMIENTO Y GPS EN VIVO)
CREATE TABLE public.turnos_recorrido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id UUID NOT NULL REFERENCES public.rutas(id),
    camion_id UUID NOT NULL REFERENCES public.camiones(id),
    conductor_id UUID NOT NULL REFERENCES public.perfiles(id),
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    hora_inicio_programada TIME NOT NULL,
    hora_fin_programada TIME NOT NULL,
    hora_inicio_real TIMESTAMPTZ,
    hora_fin_real TIMESTAMPTZ,
    estado public.estado_turno DEFAULT 'programado',
    latitud_actual DOUBLE PRECISION,
    longitud_actual DOUBLE PRECISION,
    minutos_retraso_estimado INTEGER DEFAULT 0,
    ultima_actualizacion_gps TIMESTAMPTZ,
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 8. TABLA DE SOLICITUDES DE EXTENSIÓN DE TIEMPO (COLABORADOR)
CREATE TABLE public.solicitudes_extension_tiempo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    turno_id UUID NOT NULL REFERENCES public.turnos_recorrido(id) ON DELETE CASCADE,
    conductor_id UUID NOT NULL REFERENCES public.perfiles(id),
    minutos_solicitados INTEGER NOT NULL CHECK (minutos_solicitados > 0),
    motivo TEXT NOT NULL,
    estado public.estado_solicitud DEFAULT 'pendiente',
    revisado_por UUID REFERENCES public.perfiles(id),
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 9. TABLA DE SANCIONES E INFRACCIONES (CLÁUSULA C3 TÉRMINOS DE USO)
CREATE TABLE public.infracciones_sanciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES public.perfiles(id),
    tipo_infraccion public.tipo_infraccion NOT NULL,
    motivo TEXT NOT NULL,
    dias_suspension INTEGER DEFAULT 0,
    fecha_inicio TIMESTAMPTZ DEFAULT NOW(),
    fecha_fin TIMESTAMPTZ,
    activo BOOLEAN DEFAULT TRUE,
    aplicado_por UUID REFERENCES public.perfiles(id),
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 10. TABLA DE REPORTES CIUDADANOS
CREATE TABLE public.reportes_ciudadanos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ciudadano_id UUID NOT NULL REFERENCES public.perfiles(id),
    tipo_incidente VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    foto_url TEXT,
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    direccion_referencia TEXT,
    estado public.estado_reporte DEFAULT 'recibido',
    atendido_por UUID REFERENCES public.perfiles(id),
    creado_en TIMESTAMPTZ DEFAULT NOW()
);

-- 11. TABLA DE PUNTOS LIMPIOS Y RECICLAJE
CREATE TABLE public.puntos_reciclaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    tipo_residuo VARCHAR(100) NOT NULL,
    direccion TEXT NOT NULL,
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    horario_atencion VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE
);

-- ====================================================================
-- TRIGGER AUTOMÁTICO: CREACIÓN DE PERFIL TRAS REGISTRO EN AUTH.USERS
-- ====================================================================
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    v_rol public.rol_usuario;
    v_nombre TEXT;
    v_apellido TEXT;
    v_codigo_activacion TEXT;
BEGIN
    -- Extraer metadatos enviados desde el cliente en la creación de usuario
    v_nombre := COALESCE(new.raw_user_meta_data->>'nombre', 'Usuario');
    v_apellido := COALESCE(new.raw_user_meta_data->>'apellido', 'Nuevo');
    v_rol := COALESCE((new.raw_user_meta_data->>'rol')::public.rol_usuario, 'ciudadano');
    v_codigo_activacion := new.raw_user_meta_data->>'codigo_activacion';

    -- Si se registró como colaborador o administrador, validar y quemar el código
    IF v_rol IN ('colaborador', 'administrador') THEN
        IF v_codigo_activacion IS NULL OR NOT EXISTS (
            SELECT 1 FROM public.codigos_activacion
            WHERE codigo = v_codigo_activacion 
              AND rol_destino = v_rol 
              AND usado = FALSE
              AND (fecha_expiracion IS NULL OR fecha_expiracion > NOW())
        ) THEN
            RAISE EXCEPTION 'El código de activación no es válido para el rol solicitado.';
        END IF;

        -- Marcar el código como usado
        UPDATE public.codigos_activacion
        SET usado = TRUE,
            usado_por = new.id
        WHERE codigo = v_codigo_activacion;
    END IF;

    -- Insertar en la tabla pública de perfiles
    INSERT INTO public.perfiles (id, nombre, apellido, correo, rol, estado_cuenta)
    VALUES (new.id, v_nombre, v_apellido, new.email, v_rol, 'activo');

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger asociado a auth.users
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ====================================================================
-- PROCEDIMIENTOS ALMACENADOS / RPCs (LÓGICA CRÍTICA DE NEGOCIO)
-- ====================================================================

-- RPC 1: Validar código de activación antes del registro (Consulta Previa)
CREATE OR REPLACE FUNCTION public.validar_codigo_activacion(p_codigo VARCHAR, p_rol public.rol_usuario)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM public.codigos_activacion
        WHERE codigo = p_codigo 
          AND rol_destino = p_rol 
          AND usado = FALSE
          AND (fecha_expiracion IS NULL OR fecha_expiracion > NOW())
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- RPC 2: Actualización de Ubicación GPS en Vivo y Detección de Retraso (Colaborador)
CREATE OR REPLACE FUNCTION public.actualizar_gps_turno(
    p_turno_id UUID,
    p_lat DOUBLE PRECISION,
    p_lng DOUBLE PRECISION,
    p_minutos_retraso INTEGER DEFAULT 0
)
RETURNS VOID AS $$
BEGIN
    UPDATE public.turnos_recorrido
    SET latitud_actual = p_lat,
        longitud_actual = p_lng,
        minutos_retraso_estimado = p_minutos_retraso,
        estado = CASE 
                    WHEN p_minutos_retraso > 15 THEN 'retrasado'::public.estado_turno
                    ELSE 'en_curso'::public.estado_turno
                 END,
        ultima_actualizacion_gps = NOW()
    WHERE id = p_turno_id 
      AND conductor_id = auth.uid();
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- RPC 3: Solicitar Extensión de Tiempo por Conductor
CREATE OR REPLACE FUNCTION public.solicitar_extension_tiempo(
    p_turno_id UUID,
    p_minutos INTEGER,
    p_motivo TEXT
)
RETURNS UUID AS $$
DECLARE
    v_solicitud_id UUID;
BEGIN
    INSERT INTO public.solicitudes_extension_tiempo (turno_id, conductor_id, minutos_solicitados, motivo)
    VALUES (p_turno_id, auth.uid(), p_minutos, p_motivo)
    RETURNING id INTO v_solicitud_id;

    RETURN v_solicitud_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- RPC 4: Aplicación de Sanciones Disciplinarias (Administrador - Cláusula C3)
CREATE OR REPLACE FUNCTION public.aplicar_sancion(
    p_usuario_id UUID,
    p_tipo public.tipo_infraccion,
    p_motivo TEXT,
    p_dias INTEGER DEFAULT 0
)
RETURNS VOID AS $$
DECLARE
    v_fecha_fin TIMESTAMPTZ := NULL;
    v_nuevo_estado public.estado_cuenta;
BEGIN
    -- Validar que el ejecutor sea Administrador
    IF NOT EXISTS (SELECT 1 FROM public.perfiles WHERE id = auth.uid() AND rol = 'administrador') THEN
        RAISE EXCEPTION 'Acceso denegado: Solo administradores pueden aplicar sanciones.';
    END IF;

    IF p_tipo = 'leve' THEN
        v_nuevo_estado := 'activo'; -- Amonestación escrita registrada sin corte de acceso
    ELSIF p_tipo = 'grave' THEN
        v_nuevo_estado := 'suspendido_temporal';
        v_fecha_fin := NOW() + (p_dias || ' days')::INTERVAL;
    ELSIF p_tipo = 'critica' THEN
        v_nuevo_estado := 'bloqueado_definitivo';
    END IF;

    -- Registrar infracción
    INSERT INTO public.infracciones_sanciones (
        usuario_id, tipo_infraccion, motivo, dias_suspension, fecha_inicio, fecha_fin, aplicado_por
    ) VALUES (
        p_usuario_id, p_tipo, p_motivo, p_dias, NOW(), v_fecha_fin, auth.uid()
    );

    -- Actualizar estado de la cuenta del usuario
    UPDATE public.perfiles
    SET estado_cuenta = v_nuevo_estado
    WHERE id = p_usuario_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ====================================================================
-- POLÍTICAS DE SEGURIDAD (ROW LEVEL SECURITY - RLS)
-- ====================================================================
ALTER TABLE public.perfiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.codigos_activacion ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.camiones ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.rutas ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tramos_ruta ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.turnos_recorrido ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.solicitudes_extension_tiempo ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.infracciones_sanciones ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reportes_ciudadanos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.puntos_reciclaje ENABLE ROW LEVEL SECURITY;

-- Funciones Helper de RLS
CREATE OR REPLACE FUNCTION public.es_admin() RETURNS BOOLEAN AS $$
    SELECT EXISTS (SELECT 1 FROM public.perfiles WHERE id = auth.uid() AND rol = 'administrador');
$$ LANGUAGE sql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION public.es_colaborador() RETURNS BOOLEAN AS $$
    SELECT EXISTS (SELECT 1 FROM public.perfiles WHERE id = auth.uid() AND rol = 'colaborador');
$$ LANGUAGE sql SECURITY DEFINER;

-- 1. Políticas de Perfiles
CREATE POLICY "Lectura pública de perfiles" ON public.perfiles FOR SELECT USING (true);
CREATE POLICY "Modificación del propio perfil" ON public.perfiles FOR UPDATE USING (auth.uid() = id);
CREATE POLICY "Admins gestionan perfiles" ON public.perfiles FOR ALL USING (public.es_admin());

-- 2. Políticas de Rutas y Tramos (Públicas para consulta)
CREATE POLICY "Cualquier usuario puede ver rutas y tramos" ON public.rutas FOR SELECT USING (activo = true);
CREATE POLICY "Cualquier usuario puede ver tramos" ON public.tramos_ruta FOR SELECT USING (true);
CREATE POLICY "Admins gestionan rutas" ON public.rutas FOR ALL USING (public.es_admin());
CREATE POLICY "Admins gestionan tramos" ON public.tramos_ruta FOR ALL USING (public.es_admin());

-- 3. Políticas de Turnos y GPS en Vivo
CREATE POLICY "Cualquier usuario ve turnos activos en mapa" ON public.turnos_recorrido FOR SELECT USING (true);
CREATE POLICY "Colaborador actualiza su propio turno" ON public.turnos_recorrido FOR UPDATE USING (conductor_id = auth.uid());
CREATE POLICY "Admins gestionan turnos" ON public.turnos_recorrido FOR ALL USING (public.es_admin());

-- 4. Políticas de Reportes Ciudadanos
CREATE POLICY "Ciudadano crea sus reportes" ON public.reportes_ciudadanos FOR INSERT WITH CHECK (auth.uid() = ciudadano_id);
CREATE POLICY "Ciudadano ve sus propios reportes" ON public.reportes_ciudadanos FOR SELECT USING (auth.uid() = ciudadano_id OR public.es_admin() OR public.es_colaborador());
CREATE POLICY "Admins y Colaboradores actualizan estado reporte" ON public.reportes_ciudadanos FOR UPDATE USING (public.es_admin() OR public.es_colaborador());

-- 5. Políticas de Puntos de Reciclaje
CREATE POLICY "Lectura libre de puntos limpios" ON public.puntos_reciclaje FOR SELECT USING (activo = true);
CREATE POLICY "Admins configuran puntos limpios" ON public.puntos_reciclaje FOR ALL USING (public.es_admin());

-- 6. Códigos de Activación (Solo Admins)
CREATE POLICY "Admins controlan codigos" ON public.codigos_activacion FOR ALL USING (public.es_admin());

-- ====================================================================
-- DATOS SEMILLA DE PRUEBA (CHICLAYO)
-- ====================================================================
INSERT INTO public.codigos_activacion (codigo, rol_destino) VALUES 
('ADMIN2025', 'administrador'),
('COLAB2025', 'colaborador'),
('CHICLAYO01', 'colaborador')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.camiones (placa, modelo, capacidad_toneladas) VALUES 
('M4X-810', 'Volvo FMX Recolector 15m3', 8.5),
('EGB-452', 'Hino 500 Compactador', 6.0)
ON CONFLICT (placa) DO NOTHING;

INSERT INTO public.rutas (nombre_ruta, zona_chiclayo, duracion_estimada_minutos, descripcion) VALUES
('Ruta 01 - Centro Histórico', 'Chiclayo Cercado', 90, 'Recorrido principal por Av. Balta, Elías Aguirre y Bolognesi'),
('Ruta 02 - José Leonardo Ortiz', 'Distrito JLO', 120, 'Av. Chiclayo, Av. Kennedy y sectores aledaños'),
('Ruta 03 - La Victoria', 'Distrito La Victoria', 100, 'Av. Los Incas, Av. Chinchaysuyo')
ON CONFLICT DO NOTHING;
```

---

## 4. 🚀 Plan de Migración e Implementación Paso a Paso

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       CRONOGRAMA DE EJECUCIÓN PASO A PASO                   │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. Consola Supabase    ──► Crear Proyecto & Ejecutar Script SQL DDL         │
│ 2. Storage Buckets     ──► Crear Buckets: 'reportes-fotos', 'avatares'      │
│ 3. Limpieza Gradle     ──► Remover Firebase BOM/Auth/Firestore/Storage      │
│ 4. SDK Supabase Android──► Configurar Supabase Kotlin + Ktor Client         │
│ 5. Capa de Datos / Rep ──► AuthRepository, RutasRepository, AdminRepository │
│ 6. Refactor Pantallas  ──► MainActivity (Login), Registro, SeleccionRol,    │
│                            ValidacionCodigo, TerminosUso                    │
│ 7. Módulos Operativos  ──► Dashboard Ciudadano (Mapa en Vivo),              │
│                            Dashboard Conductor (Alerta de Retraso / GPS),   │
│                            Panel Administrador (Sanciones / Códigos)        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Paso 1: Configuración en Supabase Dashboard
1. Ingresar a [https://supabase.com](https://supabase.com) y crear el proyecto `CityGo-Chiclayo`.
2. Abrir **SQL Editor** y ejecutar el script SQL provisto en la Sección 3.2.
3. Ir a **Storage** -> **New Bucket**:
   - Crear bucket público: `reportes-fotos` (para fotos de basura/incidentes).
   - Crear bucket público: `avatares` (para fotos de perfil).
4. En **Project Settings** -> **API**, copiar:
   - `Project URL` (ej. `https://xxxx.supabase.co`)
   - `anon public key` (clave pública anónima para la app Android).

---

### Paso 2: Limpieza de Dependencias Firebase en Android
1. En `gradle/libs.versions.toml`:
   - Eliminar `firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`.
2. En `app/build.gradle`:
   - Remover `id 'com.google.gms.google-services'`.
   - Remover las dependencias de Firebase.
3. Eliminar o archivar `app/google-services.json`.

---

### Paso 3: Integración del Cliente Supabase en Android
Se incorporará el SDK oficial de Supabase para Kotlin/Android (o cliente REST Ktor/Retrofit):
```toml
# libs.versions.toml
[versions]
supabase = "2.5.4"
ktor = "2.3.12"
kotlinx-serialization = "1.6.3"

[libraries]
supabase-gotrue = { module = "io.github.jan-tennert.supabase:gotrue-kt", version.ref = "supabase" }
supabase-postgrest = { module = "io.github.jan-tennert.supabase:postgrest-kt", version.ref = "supabase" }
supabase-storage = { module = "io.github.jan-tennert.supabase:storage-kt", version.ref = "supabase" }
supabase-realtime = { module = "io.github.jan-tennert.supabase:realtime-kt", version.ref = "supabase" }
ktor-client-android = { module = "io.ktor:ktor-client-android", version.ref = "ktor" }
```

---

### Paso 4: Arquitectura de Software en la App (Clean Architecture + MVVM)
Se organizará el código en paquetes claros:
- `com.greencix.citygo.data.network`: Cliente Supabase Singleton (`SupabaseClientProvider`).
- `com.greencix.citygo.data.model`: Data classes para `Perfil`, `TurnoRecorrido`, `Ruta`, `Reporte`, `Infraccion`.
- `com.greencix.citygo.data.repository`:
  - `AuthRepository`: Métodos `login(email, pass)`, `register(nombre, apellido, email, pass, rol, codigoActivacion)`, `logout()`, `obtenerPerfilActual()`.
  - `RutasRepository`: Métodos `obtenerRutas()`, `actualizarUbicacionGps(turnoId, lat, lng, retrasoMin)`, `solicitarExtension(turnoId, minutos, motivo)`.
  - `ReportesRepository`: Métodos `crearReporte(tipo, desc, lat, lng, imageUri)`, `obtenerReportes()`.
  - `AdminRepository`: Métodos `aplicarSancion(usuarioId, tipo, motivo, dias)`, `generarCodigo(rol)`.
- `com.greencix.citygo.ui`:
  - `auth`: `LoginActivity`, `RegistroActivity`, `SeleccionRolActivity`, `ValidacionCodigoActivity`, `TerminosUsoActivity`.
  - `ciudadano`: `DashboardCiudadanoActivity`, `MapaRecorridoFragment`, `CrearReporteActivity`.
  - `colaborador`: `DashboardColaboradorActivity`, `RutaEnCursoActivity`, `DialogExtensionTiempo`.
  - `admin`: `DashboardAdminActivity`, `GestionFlotaActivity`, `SancionesActivity`.

---

### Paso 5: Refactorización de las Actividades Existentes
1. **`ValidacionCodigo.java`**:
   - Reemplazar la verificación de texto estático (`"ADMIN2025"`, `"COLAB2025"`) por la llamada RPC `validar_codigo_activacion(codigo, rol)` en Supabase.
2. **`Registro.java` y `SeleccionRol.java`**:
   - Pasar los datos consolidados (Nombre, Apellido, Correo, Clave, Rol, Código) mediante Intent o State Holder hacia la pantalla de Términos.
3. **`TerminosUso.java`**:
   - Al pulsar *"ACEPTAR Y FINALIZAR"*, invocar `authRepository.register(...)` con los metadatos. El trigger `handle_new_user` de Postgres validará el código y creará el perfil.
4. **`MainActivity.java` (Login)**:
   - Conectar con `auth.signInWithPassword`.
   - Validar `estado_cuenta` del perfil (si está `suspendido_temporal` mostrar mensaje con fecha de rehabilitación según Cláusula C3; si está `bloqueado_definitivo` denegar el acceso).
   - Redireccionar al Dashboard correspondiente según el rol (`ciudadano`, `colaborador` o `administrador`).

---

### Paso 6: Verificación y Pruebas
1. **Pruebas de Autenticación y Trigger:**
   - Registro de Ciudadano (sin código).
   - Registro de Colaborador con código válido `COLAB2025` y con código inválido.
   - Registro de Administrador con `ADMIN2025`.
2. **Pruebas de RLS:**
   - Verificar que un Ciudadano no pueda invocar `aplicar_sancion`.
   - Verificar que un Colaborador solo pueda actualizar su propio turno de recolección.
3. **Pruebas de Recorrido en Tiempo Real:**
   - Emisión de GPS desde el módulo de Colaborador y recepción en el mapa del Ciudadano.
   - Disparo de alerta cuando el tiempo transcurrido en el tramo supera el tiempo estimado.
