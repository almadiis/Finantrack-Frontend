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
        setContentView(R.layout.activity_register)
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
            val username = etUsername.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString()

            if (username.isEmpty()) {
                etUsername.error = "Introduce un nombre de usuario"
                return@setOnClickListener
            }

            if (correo.isEmpty()) {
                etCorreo.error = "Introduce un correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val nuevoUsuario = Usuario(nombre = username, correo = correo, password = password)
                repository.registrarUsuario(nuevoUsuario)

                val user = repository.loginUsuario(username, password)
                if (user != null) {
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit { putInt("usuario_id", user.id) }

                    SyncManager(repository).syncAll(user.id)

                    Toast.makeText(this@RegisterActivity, "Registrado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al iniciar sesión después del registro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
