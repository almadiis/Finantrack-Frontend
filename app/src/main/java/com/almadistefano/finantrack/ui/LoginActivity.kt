package com.almadistefano.finantrack.ui

import RemoteDataSource
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.almadistefano.finantrack.model.LoginRequest
import com.almadistefano.finantrack.utils.SyncManager

class LoginActivity : AppCompatActivity() {

    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = (application as FinantrackApplication).appDB


        setContentView(R.layout.activity_login)

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
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnGoToRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            lifecycleScope.launch {
                val user = repository.loginUsuario(username, password)
                if (user != null) {
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit { putInt("usuario_id", user.id) }

                    // 🔁 Sincronizar después del login
                    SyncManager(repository).syncAll(user.id)

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
