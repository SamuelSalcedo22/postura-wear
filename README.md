# Entrenador de Postura y Ergonomía Wearable

App de smartwatch (Wear OS) que acompaña la postura mientras estudias o trabajas.
Usa el acelerómetro y el giroscopio para reconocer patrones de postura y movimiento,
y vibra con suavidad cuando detecta un patrón que configuraste como incorrecto.

Proyecto académico de la materia **Diseño de Interfaces de Software**.
Aporta a los **ODS 3** (Salud y bienestar) y **ODS 8** (Trabajo decente).

> **Prototipo experimental de ergonomía.** No es un dispositivo médico ni ofrece
> diagnósticos. Habla de "posible mala postura" y da recomendaciones generales.
> Un reloj mide la orientación de la muñeca, no la columna: la detección es una
> aproximación respecto a la postura de referencia que calibra cada persona.

## Qué hace

| Función | Descripción |
|---|---|
| Calibración | Guarda en 5 s tu postura de referencia. |
| Detección | Clasifica ventanas de 3 s: alineada, inclinación hacia adelante, inclinación lateral o en movimiento. |
| Vibración | Avisa si el patrón se sostiene el tiempo que elijas (5 a 30 s), con descanso entre avisos. |
| Sesiones | De estudio o de trabajo, con pausa y resumen al terminar. |
| Estadísticas | Últimos 7 días, con filtro por tipo de sesión. |
| Patrón habitual | Aprende cómo sueles sentarte y puede avisar si te alejas de ello. |
| Ajustes | Sensibilidad, patrones que avisan, análisis en la nube y simulador. |

## Arquitectura

```
postura-wear/
├── core/      Librería compartida: tokens, dominio, sensores, red, estado e interfaz
├── wear/      App del reloj (Wear OS)
├── phone/     App de pruebas para celular: la misma interfaz en un marco redondo
├── backend/   API en FastAPI para Cloud Run (clasificador + Gemini en Vertex AI)
├── ml/        Datos sintéticos, entrenamiento y despliegue en Vertex AI
└── docs/      Documento de diseño
```

**IA combinada.** El reloj envía ventanas de sensores por HTTPS al backend, que las
clasifica (endpoint de Vertex AI → modelo local → reglas) y usa Gemini para redactar
el resumen de la sesión. Si el backend no responde, la app sigue con una detección
local por umbrales y lo dice en pantalla ("Detección básica").

**Sin secretos en el reloj.** La app no contiene claves ni credenciales. El proyecto,
la región y el modelo de Google Cloud son variables de entorno del backend.

El detalle de diseño (arquitectura de información, tokens, jerarquía atómica, voz y
tono) está en [docs/diseno.md](docs/diseno.md).

## Requisitos

- Android Studio reciente (incluye el JDK) y el SDK de Android 37.
- Python 3.12 o superior para el backend.
- Opcional: `gcloud` para desplegar en Google Cloud.

Versiones fijadas en `gradle/libs.versions.toml`: AGP 9.5.1, Kotlin 2.4.21,
Compose para Wear OS 1.7.1 (Material 3).

## Ejecutar el backend en local

```bash
python -m venv .venv
```

```bash
.venv\Scripts\python -m pip install -r backend/requirements-dev.txt
```

```bash
.venv\Scripts\python -m uvicorn app.main:app --app-dir backend --port 8080
```

Sin configurar Google Cloud, el backend clasifica por reglas y responde con textos
de respaldo. Compruébalo en `http://localhost:8080/health`.

Para usar el modelo entrenado, instala además `scikit-learn` y `joblib` y entrena:

```bash
.venv\Scripts\python -m pip install scikit-learn joblib
```

```bash
.venv\Scripts\python ml/train.py
```

## Ejecutar la app

Abre la carpeta en Android Studio y espera la sincronización de Gradle.

### a) Emulador de Wear OS

1. En **Device Manager** crea un dispositivo *Wear OS Large Round* (API 34 o superior).
2. Elige la configuración **wear** y pulsa **Run**.
3. El emulador llega al backend de tu computador en `http://10.0.2.2:8080`
   (valor por defecto, no hay que cambiar nada).
4. El emulador no tiene sensores útiles: en **Ajustes → Simulador de sensores**
   activa el simulador y elige un escenario.

### b) Celular Android por ADB

1. Activa las opciones de desarrollador y la depuración USB, y conecta el celular.
2. Para que el celular llegue al backend local, redirige el puerto y apunta la app a `localhost`:

```bash
adb reverse tcp:8080 tcp:8080
```

   En `local.properties` agrega la línea `postura.backendUrl=http://localhost:8080`.
3. Elige la configuración **phone** y pulsa **Run**, o desde la terminal:

```bash
gradlew.bat :phone:installDebug
```

La app muestra la interfaz del reloj dentro de un marco redondo y, debajo, un panel
para alternar entre los sensores del celular y el simulador, cambiar el escenario,
comprobar la conexión con el backend y probar la vibración. Para volver de una
pantalla se usa el gesto o botón **atrás** del sistema.

### c) Reloj real

1. En el reloj: **Ajustes → Opciones para desarrolladores → Depuración por Wi-Fi**.
2. Empareja y conecta con `adb pair <ip>:<puerto>` y `adb connect <ip>:<puerto>`.
3. Apunta la app a un backend con HTTPS (por ejemplo, el de Cloud Run) en
   `local.properties`: `postura.backendUrl=https://<tu-servicio>.run.app`
4. Instala:

```bash
gradlew.bat :wear:installDebug
```

Por seguridad, la app solo admite HTTP sin cifrar hacia `10.0.2.2` y `localhost`;
cualquier otro servidor debe usar HTTPS.

## Pruebas

```bash
gradlew.bat :core:testDebugUnitTest
```

```bash
.venv\Scripts\python -m pytest backend
```

Las pruebas de Kotlin cubren el dominio (características, calibración, patrón
habitual, avisos, sesiones, estadísticas) y los detectores de postura (por umbrales
e híbrido con respaldo sin red). Las de Python cubren la API.

## Desplegar en Google Cloud

1. Copia `.env.example` como `.env` para tener a mano los nombres de las variables.
   `.env` y los JSON de credenciales están en `.gitignore`: nunca se suben.
2. Despliega el backend en Cloud Run y define las variables en el servicio:

```bash
gcloud run deploy postura-backend --source backend --region <REGION> --allow-unauthenticated --set-env-vars GOOGLE_CLOUD_PROJECT=<PROYECTO>,GOOGLE_CLOUD_LOCATION=<REGION>,GEMINI_MODEL=<MODELO>
```

3. Da a la cuenta de servicio de Cloud Run el rol **Vertex AI User**. No hacen falta
   claves JSON: el backend usa las credenciales del propio servicio.
4. (Opcional) Despliega el clasificador en Vertex AI con `python ml/deploy_vertex.py`
   y agrega `VERTEX_ENDPOINT_ID` al servicio. Un endpoint tiene costo mientras exista.

`--allow-unauthenticated` deja la API abierta, que es lo que permite que el reloj no
guarde claves. Para algo más que un prototipo conviene añadir límites de uso
(por ejemplo, con API Gateway o Cloud Armor).

## Limitaciones conocidas

- La sesión analiza mientras la app está abierta y la pantalla encendida; no hay
  todavía un servicio en segundo plano.
- Los datos de entrenamiento son sintéticos. Un modelo útil necesita grabaciones
  reales de varias personas.
- Las estadísticas se guardan solo en el dispositivo.
