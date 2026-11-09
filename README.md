# Pie y Gemelos ⚽ — App Android

Programación continua sobre fechas reales: semanas 1–10 de desarrollo (Base → Desarrollo I → Descarga → Desarrollo II → Descarga + test) y desde la semana 11 ciclos de 8 semanas que se repiten siempre (4 Mantenimiento → 3 Refuerzo con +2,5 kg → 1 Descarga + test). Registras la carga de cada martes y la app calcula la siguiente; incluye calendario semana a semana e historial de tests. Dosificado para partido los sábados.

GitHub compila el APK automáticamente cada vez que subes un cambio. No necesitas instalar Android Studio.

## Archivos
| Archivo | Para qué sirve |
|---|---|
| `index.html` | La app completa (puedes editarla) |
| `icon.png` | Icono de la app |
| `app.keystore` | Firma del APK. **No lo borres ni lo cambies**: sin él no podrás actualizar la app sin perder tus datos |
| `.github/workflows/build-apk.yml` | Las instrucciones que GitHub sigue para compilar el APK |

---

## Tutorial: de los archivos al APK en tu móvil

### 1. Crear el repositorio
1. Entra en **github.com** con tu cuenta (o créala gratis).
2. Arriba a la derecha: **+ → New repository**.
3. Nombre: `pie-y-gemelos`. Marca **Private** (recomendado, porque el repo incluye tu firma `app.keystore`).
4. No marques nada más → **Create repository**.

### 2. Subir los archivos
1. En la página del repo vacío, pulsa el enlace **"uploading an existing file"**.
2. Arrastra `index.html`, `icon.png`, `app.keystore` y `README.md`.
3. Abajo, pulsa **Commit changes**.

### 3. Crear el archivo de compilación
La carpeta `.github` empieza por punto, así que muchos sistemas la ocultan y no se sube bien arrastrándola. Es más seguro crearla a mano:
1. En el repo: **Add file → Create new file**.
2. En el nombre escribe exactamente: `.github/workflows/build-apk.yml`. Al escribir cada `/` GitHub crea la carpeta.
3. Abre `build-apk.yml` en tu ordenador, copia todo su contenido y pégalo en el editor de GitHub.
4. Pulsa **Commit changes… → Commit changes**.

En cuanto guardas el archivo, la compilación empieza sola.

### 4. Esperar la compilación (unos 5–8 min)
1. Ve a la pestaña **Actions** del repo.
2. Verás **"Compilar APK"** en marcha (círculo amarillo).
3. Cuando termine, aparecerá un ✅ verde.

Si sale una ❌ roja, entra en la ejecución, abre el paso en rojo y copia el error para revisarlo.

### 5. Descargar el APK
1. Vuelve a la pestaña **Code**. A la derecha verás **Releases** → pulsa la última versión (`Pie y Gemelos v1.X`).
2. En **Assets**, descarga **`PieYGemelos.apk`**.

Lo más cómodo es hacerlo directamente desde el móvil: abre github.com en Chrome con tu sesión iniciada (el repo es privado) o usa la app GitHub.

### 6. Instalar en Android
1. Abre el `PieYGemelos.apk` descargado (desde la notificación o la app **Archivos**).
2. Si Android lo pide, activa **"Permitir de esta fuente"** / **"Instalar apps desconocidas"** para Chrome o Archivos.
3. Pulsa **Instalar**.
4. Si aparece **Play Protect** ("app desconocida"), pulsa **Más detalles → Instalar de todos modos**. Es normal en apps que no vienen de Play Store.

---

## Actualizar la app
1. Edita `index.html` en GitHub (abre el archivo → ✏️ lápiz → cambia lo que quieras → **Commit changes**), o súbelo de nuevo con **Add file → Upload files**.
2. Se compila sola una nueva versión en **Releases**.
3. Instálala encima de la anterior. Como la firma es la misma, **conservas tus checks, tests y fecha de inicio**.

También puedes recompilar sin cambiar nada: **Actions → Compilar APK → Run workflow**.

## Problemas comunes
- **No aparece nada en Actions:** ve a **Settings → Actions → General**, marca **Allow all actions** y guarda. Después usa **Run workflow**.
- **"App no instalada" al actualizar:** cambió la firma. Ocurre si se borró o reemplazó `app.keystore`. Desinstala la versión vieja (perderás los checks guardados) e instala la nueva.
- **Los videos no abren:** se abren en el navegador o en la app de YouTube del móvil. Necesitas conexión a internet; el resto de la app funciona sin conexión.

## Editar la rutina
Todo está en el `<script>` de `index.html`:
- `EX`: los ejercicios y sus videos
- `INITIAL` y `CYCLE`: las fases iniciales y el ciclo continuo, con sus dosis
- `buildWeek()`: la plantilla de cada día de la semana

> Uso personal; no sustituye la valoración de un fisioterapeuta. Publicarla en Google Play exigiría una cuenta de desarrollador (25 USD) y un formato distinto (AAB). Para uso propio, el APK es suficiente.
