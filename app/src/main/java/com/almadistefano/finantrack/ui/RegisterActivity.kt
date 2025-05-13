package com.almadistefano.finantrack.ui

import RemoteDataSource
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.LoginRequest
import com.almadistefano.finantrack.model.Usuario
import com.almadistefano.finantrack.utils.SyncManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var repository: Repository

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Puedes reutilizar el layout
        val db = (application as FinantrackApplication).appDB

        repository = Repository(
            LocalDataSource(
                db.cuentaDao(),
                db.categoriaDao(),
                db.presupuestoDao(),
                db.transaccionDao(),
                db.usuarioDao()
            ),
            RemoteDataSource()
        )


        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnGoToRegister)

        btnRegister.text = "Registrarse"

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()

            lifecycleScope.launch {
                val nuevoUsuario = Usuario(nombre = username, correo = correo, password = password)
                repository.registrarUsuario(nuevoUsuario)

                //Ahora login el usuario
                val user = repository.loginUsuario(username, password)
                if (user != null) {
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit { putInt("usuario_id", user.id) }

                    // 🔁 Sincronización completa
                    SyncManager(repository).syncAll(user.id)

                    Toast.makeText(this@RegisterActivity, "Registrado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }

            }
        }
    }
}
