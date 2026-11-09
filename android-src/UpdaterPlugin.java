package com.adrian.piegemelos;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/** Puente para que la app web muestre la versión y busque actualizaciones a mano. */
@CapacitorPlugin(name = "AppUpdater")
public class UpdaterPlugin extends Plugin {

    @PluginMethod
    public void info(PluginCall call) {
        JSObject r = new JSObject();
        r.put("version", Updater.currentName(getContext()));
        r.put("code", Updater.currentCode(getContext()));
        call.resolve(r);
    }

    @PluginMethod
    public void check(PluginCall call) {
        getActivity().runOnUiThread(() -> Updater.check(getActivity(), true));
        call.resolve();
    }
}
