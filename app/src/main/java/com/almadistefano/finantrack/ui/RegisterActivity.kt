package com.almadistefano.finantrack.ui

import RemoteDataSource
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Usuario
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
                db.cuentasDao(),
                db.categoriasDao(),
                db.presupuestosDao(),
                db.transaccionesDao(),
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
                val exists = repository.remote.getUsuarioByNombre(username)
                if (exists != null) {
                    Toast.makeText(this@RegisterActivity, "Usuario ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    repository.remote.postUsuario(Usuario(nombre = username, correo = correo, password = password))
                    Toast.makeText(this@RegisterActivity, "Registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
