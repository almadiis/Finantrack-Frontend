package com.almadistefano.finantrack

import AppDatabase
import android.app.Application
import androidx.room.Room

class FinantrackApplication : Application(){
    lateinit var appDB : AppDatabase
        private set

    /**
     * Método que se ejecuta cuando la aplicación es creada.
     * Inicializa la base de datos utilizando Room con el nombre de archivo `motos.db`.
     */
    override fun onCreate() {
        super.onCreate()
        appDB = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "finantrack.db"
        ).build()
    }

}