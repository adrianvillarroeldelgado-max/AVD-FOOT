# Pie y Gemelos ⚽ — App Android (AVD FOOT)

Programación continua sobre fechas reales: semanas 1–10 de desarrollo (Base → Desarrollo I → Descarga → Desarrollo II → Descarga + test) y desde la semana 11 ciclos de 8 semanas que se repiten siempre (4 Mantenimiento → 3 Refuerzo con +2,5 kg → 1 Descarga + test). Registras la carga de cada martes y la app calcula la siguiente; incluye calendario semana a semana e historial de tests. Dosificado para partido los sábados.

Funciona igual que AVD jump: **cada cambio que llega a `main` publica una versión nueva, y la app instalada se actualiza sola sin perder datos.**

## Descargar
Enlace fijo, siempre a la última versión:
**https://github.com/adrianvillarroeldelgado-max/AVD-FOOT/releases/latest/download/PieYGemelos.apk**

### Primera instalación
1. Abre el enlace desde el teléfono y descarga el APK.
2. Ábrelo. Si Android lo pide, permite **"Instalar apps desconocidas"** para el navegador.
3. Pulsa **Instalar**. Si aparece **Play Protect**, pulsa **Más detalles → Instalar de todos modos**.

## Actualizaciones dentro de la app
- Al abrirse (como máximo cada 3 horas), la app consulta GitHub. Si hay una versión nueva, muestra **"Nueva versión"** con las novedades y el botón **Actualizar**.
- Descarga el APK y Android pide confirmar con un toque. Se instala **encima**: se conservan los checks, las cargas, los tests y la fecha de inicio.
- **La primera vez** Android pide activar **"Permitir de esta fuente"** para Pie y Gemelos. Solo hay que hacerlo una vez.
- Al final de la rutina hay un botón **Buscar actualizaciones** y se ve la versión instalada.
- **Quien tenga la v1.1** (la primera, sin actualizador) debe instalar a mano la 1.1.x una vez. A partir de ahí se actualiza sola.

## Publicar una versión (automático)
Cada cambio que llega a `main` hace que GitHub compile, firme y publique en **Releases** (unos 5–8 min) la versión `1.1.N`, donde `N` sube en cada publicación. Los cambios que solo tocan archivos `.md` no publican versión.

Cada versión incluye `PieYGemelos.apk`, `PieYGemelos-1.1.N.apk` y `version.json`, que es el archivo que consulta la app.

- **Cambiar 1.1 a 1.2:** edita el archivo `VERSION`.
- **Volver a publicar sin cambios:** **Actions → Publicar versión → Run workflow**.

## Archivos
| Archivo | Para qué sirve |
|---|---|
| `index.html` | La app completa (rutina, calendario, temporizador) |
| `android-src/` | Código Android del actualizador (`Updater.java`, `UpdateReceiver.java`, `UpdaterPlugin.java`, `MainActivity.java`) |
| `icon.png` | Icono de la app |
| `VERSION` | Los dos primeros números de la versión |
| `app.keystore` | Firma del APK. **No lo borres ni lo cambies**: sin él, las versiones nuevas no se instalan encima y se pierden los datos |
| `.github/workflows/release.yml` | Compila, firma y publica cada versión |

## Problemas comunes
- **No aparece nada en Actions:** ve a **Settings → Actions → General**, marca **Allow all actions** y guarda. Después usa **Run workflow**.
- **"App no instalada" al actualizar:** cambió la firma. Ocurre si se borró o reemplazó `app.keystore`.
- **Los videos no abren:** se abren en el navegador o en YouTube. Necesitan internet; el resto de la app funciona sin conexión.

## Editar la rutina
Todo está en el `<script>` de `index.html`:
- `EX`: los ejercicios y sus videos
- `INITIAL` y `CYCLE`: las fases iniciales y el ciclo continuo, con sus dosis
- `buildWeek()`: la plantilla de cada día de la semana

> Uso personal; no sustituye la valoración de un fisioterapeuta.
