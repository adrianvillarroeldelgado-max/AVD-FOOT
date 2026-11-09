package com.adrian.piegemelos;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(UpdaterPlugin.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al abrir la app (como máximo cada 3 horas) busca una versión nueva en GitHub.
        Updater.check(this, false);
    }
}
