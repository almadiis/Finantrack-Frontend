package com.almadistefano.finantrack.ui

import RemoteDataSource
import kotlinx.coroutines.flow.first
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
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("usuario_id", -1)

        if (userId != -1) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
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

                    SyncManager(repository).syncAll(user.id)

                    val cuentas = repository.getCuentas(user.id).first() // ✅ solo una vez
                    val primeraCuenta = cuentas.firstOrNull()
                    if (primeraCuenta != null) {
                        prefs.edit {
                            putInt("cuenta_id", primeraCuenta.id)
                        }
                    }

                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
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
