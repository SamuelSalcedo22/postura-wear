# Documento de diseño

El trabajo siguió este orden: **contenido → jerarquía → estructura → estilo**.
Cada sección indica dónde quedó esa decisión en el código.

## 1. Contenido

Antes de dibujar nada se escribieron todos los textos reales, en español e inglés:
`core/src/main/res/values/strings.xml` y `values-en/strings.xml`. No hay texto fijo
en el código ni *lorem ipsum*.

### Voz y tono

La **voz** es siempre la misma: cercana, calmada y motivadora. Habla de tú, en
primera persona ("te aviso", "no pude conectar") y con frases cortas.
El **tono** cambia con el contexto:

| Contexto | Tono | Ejemplo |
|---|---|---|
| Todo va bien | Alentador | "Vas muy bien, sigue así." |
| Posible mala postura | Sereno, sin culpa | "Endereza suave la espalda y relaja los hombros." |
| Logro | Celebra sin exagerar | "¡Gran sesión!" |
| Resultado bajo | Compasivo | "Cada sesión cuenta" |
| Error | Claro y práctico | Ver abajo |

**Lenguaje responsable.** Es un prototipo de ergonomía, no un producto médico. Nunca
se afirma un diagnóstico: se dice "posible mala postura" y "recomendación general".
La misma regla está en la instrucción de sistema de Gemini (`backend/app/coach.py`).

### Mensajes de error en tres partes

Cada error tiene tres cadenas (`_what`, `_why`, `_next`) y se muestra con la molécula
`MessageBlock`. Nunca aparecen códigos internos.

| Parte | Regla | Ejemplo (sin conexión) |
|---|---|---|
| Qué pasó | En lenguaje del usuario | "No pude conectar con el análisis en la nube." |
| Por qué | Solo si ayuda | "Puede que no haya internet en este momento." |
| Qué hacer ahora | Siempre | "Tu sesión sigue con la detección básica del reloj." |

### Internacionalización

- Español por defecto e inglés incluido; cifras y fechas con formato del sistema.
- La app sigue el idioma del dispositivo y además permite elegir **Español** o
  **English** en Ajustes; el cambio se aplica al instante (`ui/LocalizedContent.kt`).
- Ningún botón tiene ancho fijo: `PosturaButton` ocupa el ancho disponible, pasa a
  dos líneas y solo entonces trunca. Las filas y etiquetas hacen lo mismo.
- Los plurales usan `plurals` ("1 aviso", "2 avisos").
- Donde tres opciones largas no caben en una fila, `ChoiceGroup` las apila.

## 2. Jerarquía

En una pantalla de 1,2 a 1,4 pulgadas solo cabe **una idea principal por pantalla**:

- **Principal:** una acción protagonista (la que toca ahora: calibrar, empezar o
  ver la sesión) y debajo los destinos secundarios.
- **Sesión:** de arriba abajo, origen de los datos → estado en palabras → nivel de
  burbuja (se entiende de un vistazo) → tiempo → pausa. Lo que se lee con calma
  (detalle, consejo, cifras, mensajes, terminar) queda al deslizar.
- **Estadísticas:** una cifra grande (promedio alineada) → su evolución (barras) →
  el detalle.

La jerarquía tipográfica sale de los tokens `type.*`:
`metric` (36 sp) > `metricSmall` > `title` > `label`/`body` > `caption` (13 sp) >
`overline` (12 sp, el mínimo).

## 3. Estructura: arquitectura de información

### Organización

El contenido se agrupa por lo que la persona quiere lograr, no por cómo está hecha
la app. En Ajustes: *Avisos*, *Patrones que avisan*, *Análisis*, *Datos*, *Acerca de*.

### Etiquetado

Etiquetas de una palabra y del lenguaje cotidiano: **Sesión, Estadísticas,
Calibración, Ajustes**. Cada destino de la pantalla principal muestra además un dato
("Última sesión: 82 % alineada", "Referencia guardada") que a menudo evita entrar.
"Empezar sesión" en lugar de "Iniciar sesión", para no confundirlo con un inicio de
cuenta.

### Navegación

Plana y mínima: todo está a un toque de la pantalla principal.

```
Principal
├── Sesión        elegir modo → en vivo → resumen
├── Estadísticas
├── Calibración   explicar → medir → confirmar
└── Ajustes
```

Volver es deslizar hacia la derecha, el gesto estándar de Wear OS
(`SwipeDismissableNavHost` en `core/ui/PosturaApp.kt`). No hay menús ocultos.

### Búsqueda

Con cinco destinos y poco contenido, un buscador estorbaría y escribir en un reloj
es incómodo. La necesidad de "encontrar" se resuelve con acceso directo en un toque
y con un **filtro** en Estadísticas (Todo, Estudio, Trabajo).

## 4. Estilo: design system

### Identidad

- **Nivel de burbuja.** La metáfora es un nivel de carpintero: una burbuja que
  descansa en el centro cuando estás alineado y se desplaza hacia donde te inclinas.
  Es el organismo `AlignmentDial`, el icono de la app y la marca en las cabeceras.
- **Calma.** La burbuja "respira" despacio y los cambios de color duran 600 ms: un
  aviso de postura no debe sentirse como una alarma.
- **Color con significado.** Menta = alineada; ámbar = atención amable (nunca rojo
  para la postura); azul = nube e información; violeta = aprendizaje y calibración;
  coral = solo errores y acciones destructivas.
- **Tema oscuro por defecto** con fondo negro puro, que apaga píxeles en pantallas
  OLED y ahorra batería. El texto principal sobre negro supera la relación de
  contraste 15:1 y el secundario, 9:1.
- **Tema claro opcional** (Ajustes → Apariencia) para leer a plena luz del sol. Es
  otro juego de tokens semánticos (`LightSemanticColors`) sobre los mismos tokens
  globales: los componentes no cambian, solo los valores. Los acentos usan los pasos
  más oscuros de cada familia (por ejemplo `color.mint.800`) para mantener al menos
  4.5:1 de contraste sobre fondo claro, y cada color conserva su significado.
- **Háptica como parte del lenguaje.** Los patrones de vibración también son tokens,
  y cada uno declara su tipo: los avisos de postura van como *notificación*, para
  que se sientan aunque la persona haya apagado la vibración al tocar; la
  confirmación breve va como *respuesta táctil* y respeta ese ajuste.
- **Iconos propios** dibujados con trazos redondeados (`Glyph`).

### Tokens en tres niveles

Carpeta `core/src/main/java/com/posturawear/core/tokens/`. Regla: un nivel solo
consume el inmediatamente inferior, y los componentes solo leen tokens de componente
o semánticos.

| Nivel | Qué nombra | Ejemplo | Archivo |
|---|---|---|---|
| 1. Global | El valor crudo | `color.blue.500` → `GlobalColor.Blue500`; `spacing.4` → `GlobalSpacing.S4` (8 dp) | `global/` |
| 2. Semántico | La intención | `color.background.primary` → `SemanticColors.backgroundPrimary` | `semantic/` |
| 3. Componente | La parte y el estado | `button.primary.bg.default` → `ButtonTokens.primary.bgDefault` | `component/` |

Ejemplo de la cadena completa del botón principal:

```
GlobalColor.Mint500 (#3DE0AE)
  └─ SemanticColors.accentBrand              color.accent.brand
       └─ ButtonTokens.primary.bgDefault     button.primary.bg.default
            └─ PosturaButton (átomo)
```

Familias globales: color, espaciado, radio y trazo, tipografía, movimiento y háptica.
Los semánticos se entregan por `PosturaTheme` (`theme/PosturaTheme.kt`), que también
alinea Material 3 para Wear con la misma paleta.

### Atomic Design

Carpeta `core/src/main/java/com/posturawear/core/ui/`.

| Nivel | Componentes | Carpeta |
|---|---|---|
| Átomos | `Glyph`, `PosturaButton`, `PosturaIconButton`, `ToggleSwitch`, `ArcRing`, `SectionLabel`, `TitleText`, `BodyText`, `StatusDot` | `atoms/` |
| Moléculas | `NavRow`, `ToggleRow`, `ChoiceGroup`, `Badge`, `MessageBlock`, `StatBlock` | `molecules/` |
| Organismos | `AlignmentDial`, `SessionFace`, `WeekChart`, `HeroHeader`, `StatPair` | `organisms/` |
| Templates | `ListTemplate` (lista desplazable), `FaceTemplate` (cara completa + detalles) | `templates/` |
| Páginas | `HomePage`, `SessionPage`, `StatsPage`, `CalibrationPage`, `SettingsPage` | `pages/` |

Las páginas reciben estado y devuelven eventos; no conocen sensores ni red. Por eso
la app del celular (`phone/`) reutiliza exactamente las mismas páginas dentro de
`WatchFrame`, y su panel de pruebas está hecho con los mismos átomos y moléculas.

### Accesibilidad

- Áreas táctiles de 48 dp como mínimo; en filas con interruptor, toda la fila responde.
- Tamaño mínimo de texto de 12 sp; cifras con números tabulares para que no "bailen".
- El estado nunca depende solo del color: siempre hay texto ("Posible mala postura").
- Roles semánticos (botón, interruptor, opción) y descripciones para el nivel de
  burbuja, el gráfico y los botones de solo icono.
- Márgenes laterales extra para que la curva de la pantalla no recorte el contenido.

## 5. Arquitectura técnica

```
acelerómetro y giroscopio ──► ventanas de 3 s ──► detector híbrido ──► política de avisos ──► vibración
                                                         │        ▲
                                                 backend (HTTPS)  └─ respaldo local por umbrales
```

| Capa | Paquete en `core` | Responsabilidad |
|---|---|---|
| Dominio | `domain/` | Kotlin puro y probado: características, calibración, detector por umbrales, detector híbrido, patrón habitual, avisos, sesiones, estadísticas |
| Sensores | `sensors/` | Acelerómetro y giroscopio, y vibración |
| Red | `network/` | Cliente HTTP sin claves y adaptador al dominio |
| Estado | `state/` | `PosturaEngine` orquesta todo y expone `StateFlow`; DataStore guarda la configuración |
| Interfaz | `ui/`, `tokens/` | Design system y páginas |

**Patrón habitual.** `HabitModel` mantiene la media y la varianza de tu orientación
mientras estás alineado. Tras unos 10 minutos de datos marca como "inusual" lo que
se aleja más de tres desviaciones estándar, y puedes activar un aviso para ello.

**Privacidad.** Al backend solo viajan lecturas de movimiento, la referencia y el
umbral; ningún identificador. El backend no guarda lo que recibe.
