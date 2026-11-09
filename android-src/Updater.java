package com.adrian.piegemelos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Actualización automática desde GitHub Releases (igual que AVD jump).
 *
 * Cada versión publicada incluye "version.json". Al abrir la app (como máximo cada 3 horas)
 * se consulta la última; si es más nueva se ofrece actualizar: se descarga el APK y Android
 * pide confirmar. Todas las versiones van firmadas con la misma clave, así que se instala
 * encima sin perder los datos (checks, cargas, tests, fecha de inicio).
 * Solo se consulta GitHub; no se envía ningún dato del usuario.
 */
public final class Updater {

    static final String REPO = "adrianvillarroeldelgado-max/AVD-FOOT";
    static final String BASE = "https://github.com/" + REPO + "/releases/latest/download";
    private static final String PREFS = "avdfoot";
    private static final String KEY_LAST = "updateCheckMs";
    private static final String KEY_SKIP = "updateSkipCode";
    private static final long EVERY_MS = 3L * 60 * 60 * 1000;

    static final class Info { long code; String name; String apk; String notes; }

    private static Info pending;
    private static volatile boolean busy;

    private Updater() {}

    static long currentCode(Context c) {
        try {
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return Build.VERSION.SDK_INT >= 28 ? pi.getLongVersionCode() : pi.versionCode;
        } catch (Exception e) { return 0; }
    }

    static String currentName(Context c) {
        try { return c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName; }
        catch (Exception e) { return "?"; }
    }

    /** Busca una versión nueva. manual = el usuario pulsó "Buscar actualizaciones". */
    public static void check(final Activity a, final boolean manual) {
        if (busy) return;
        final SharedPreferences p = a.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        // Volvió de dar el permiso de instalación: seguir con la descarga.
        if (pending != null && canInstall(a)) { Info i = pending; pending = null; download(a, i); return; }

        long now = System.currentTimeMillis();
        if (!manual && now - p.getLong(KEY_LAST, 0L) < EVERY_MS) return;
        p.edit().putLong(KEY_LAST, now).apply();

        busy = true;
        new Thread(() -> {
            Info info = null;
            try { info = fetch(); } catch (Exception ignored) {}
            busy = false;
            final Info fi = info;
            a.runOnUiThread(() -> {
                if (a.isFinishing() || a.isDestroyed()) return;
                if (fi == null) { if (manual) toast(a, "No se pudo comprobar. Revisa la conexión a internet."); return; }
                if (fi.code <= currentCode(a)) { if (manual) toast(a, "Ya tienes la última versión (" + currentName(a) + ")."); return; }
                if (!manual && p.getLong(KEY_SKIP, -1) == fi.code) return;
                offer(a, fi);
            });
        }).start();
    }

    private static Info fetch() throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + "/version.json").openConnection();
        c.setConnectTimeout(8000); c.setReadTimeout(8000); c.setInstanceFollowRedirects(true);
        c.setRequestProperty("Accept", "application/json");
        try {
            if (c.getResponseCode() != 200) return null;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"))) {
                String line; while ((line = r.readLine()) != null) sb.append(line).append('\n');
            }
            JSONObject o = new JSONObject(sb.toString());
            Info i = new Info();
            i.code = o.getLong("versionCode");
            i.name = o.optString("versionName", "?");
            i.apk = o.optString("apk", "");
            if (i.apk.isEmpty()) i.apk = BASE + "/PieYGemelos.apk";
            i.notes = o.optString("notes", "");
            return i;
        } finally { c.disconnect(); }
    }

    private static void offer(final Activity a, final Info info) {
        StringBuilder msg = new StringBuilder("Tienes la " + currentName(a) + ". ")
            .append("La nueva versión se instala encima: no se pierden tus checks, cargas ni tests.");
        if (!info.notes.isEmpty()) msg.append("\n\nNovedades:\n").append(info.notes.length() > 600 ? info.notes.substring(0, 600) : info.notes);
        new AlertDialog.Builder(a)
            .setTitle("Nueva versión " + info.name)
            .setMessage(msg.toString())
            .setPositiveButton("Actualizar", (d, w) -> start(a, info))
            .setNegativeButton("Más tarde", null)
            .setNeutralButton("Omitir esta", (d, w) ->
                a.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_SKIP, info.code).apply())
            .show();
    }

    private static boolean canInstall(Context c) {
        return Build.VERSION.SDK_INT < 26 || c.getPackageManager().canRequestPackageInstalls();
    }

    private static void start(final Activity a, final Info info) {
        if (canInstall(a)) { download(a, info); return; }
        pending = info;
        new AlertDialog.Builder(a)
            .setTitle("Permitir actualizaciones")
            .setMessage("Para instalar la versión nueva, activa “Permitir de esta fuente” para Pie y Gemelos y vuelve a la app. Solo hay que hacerlo una vez.")
            .setPositiveButton("Abrir ajustes", (d, w) -> {
                try {
                    a.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + a.getPackageName())));
                } catch (Exception e) {
                    toast(a, "Abre Ajustes › Apps › Pie y Gemelos › Instalar apps desconocidas.");
                }
            })
            .setNegativeButton("Cancelar", (d, w) -> pending = null)
            .show();
    }

    private static void download(final Activity a, final Info info) {
        final TextView status = new TextView(a);
        int pad = (int) (24 * a.getResources().getDisplayMetrics().density);
        status.setPadding(pad, pad / 2, pad, pad / 3);
        status.setTextSize(15f);
        status.setText("Descargando 0 %");
        final AlertDialog dlg = new AlertDialog.Builder(a)
            .setTitle("Pie y Gemelos " + info.name).setView(status).setCancelable(false).show();
        busy = true;
        new Thread(() -> {
            File f = new File(a.getCacheDir(), "actualizacion.apk");
            String err = null;
            try {
                String url = info.apk;
                HttpURLConnection c;
                int hops = 0;
                while (true) {
                    c = (HttpURLConnection) new URL(url).openConnection();
                    c.setConnectTimeout(10000); c.setReadTimeout(20000); c.setInstanceFollowRedirects(true);
                    int code = c.getResponseCode();
                    if (code >= 300 && code < 400 && hops < 5) {
                        String loc = c.getHeaderField("Location");
                        if (loc == null) break;
                        url = loc; c.disconnect(); hops++; continue;
                    }
                    break;
                }
                if (c.getResponseCode() != 200) throw new Exception("HTTP " + c.getResponseCode());
                long total = c.getContentLengthLong();
                try (InputStream in = c.getInputStream(); OutputStream out = new FileOutputStream(f)) {
                    byte[] buf = new byte[64 * 1024];
                    long done = 0; int lastPct = -1, n;
                    while ((n = in.read(buf)) >= 0) {
                        out.write(buf, 0, n); done += n;
                        if (total > 0) {
                            final int pct = (int) (done * 100 / total);
                            if (pct != lastPct) { lastPct = pct; a.runOnUiThread(() -> status.setText("Descargando " + pct + " %")); }
                        }
                    }
                }
                c.disconnect();
                a.runOnUiThread(() -> status.setText("Preparando la instalación…"));
                install(a.getApplicationContext(), f);
            } catch (Exception e) {
                err = e.getMessage() != null ? e.getMessage() : "error desconocido";
            }
            busy = false;
            final String fe = err;
            a.runOnUiThread(() -> {
                try { dlg.dismiss(); } catch (Exception ignored) {}
                if (fe != null) toast(a, "No se pudo descargar la actualización: " + fe);
            });
        }).start();
    }

    private static void install(Context ctx, File apk) throws Exception {
        PackageInstaller pi = ctx.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(ctx.getPackageName());
        if (Build.VERSION.SDK_INT >= 31) {
            params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED);
        }
        int id = pi.createSession(params);
        try (PackageInstaller.Session s = pi.openSession(id)) {
            try (OutputStream out = s.openWrite("piegemelos.apk", 0, apk.length());
                 InputStream in = new FileInputStream(apk)) {
                byte[] buf = new byte[64 * 1024]; int n;
                while ((n = in.read(buf)) >= 0) out.write(buf, 0, n);
                s.fsync(out);
            }
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0);
            PendingIntent pend = PendingIntent.getBroadcast(ctx, id, new Intent(ctx, UpdateReceiver.class), flags);
            s.commit(pend.getIntentSender());
        }
    }

    static void toast(Context c, String msg) { Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); }
}
