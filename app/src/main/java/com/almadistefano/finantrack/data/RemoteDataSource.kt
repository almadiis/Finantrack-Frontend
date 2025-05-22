import android.content.Context
import android.util.Log
import com.almadistefano.finantrack.data.FinantrackAPI
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.LoginRequest
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario


class RemoteDataSource {

    private val api = FinantrackAPI.getRetrofit2Api()

    suspend fun getCuentas() = safeApiCall { api.getAllCuentas() }
    suspend fun postCuenta(cuenta: Cuenta) = safeApiCall { api.postCuenta(cuenta) }

    suspend fun getCategorias() = safeApiCall { api.getAllCategorias() }
    suspend fun postCategoria(categoria: Categoria): Categoria {
        val response = api.postCategoria(categoria)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía")
        } else {
            throw Exception("Error al guardar categoría: ${response.code()}")
        }
    }
    suspend fun eliminarCategoria(categoriaId: Int) = safeApiCall { api.deleteCategoria(categoriaId) }


    suspend fun getPresupuestos() = safeApiCall { api.getAllPresupuestos() }
    suspend fun postPresupuesto(presupuesto: Presupuesto) = safeApiCall { api.postPresupuesto(presupuesto) }
    suspend fun eliminarPresupuesto(presupuestoId: Int) = safeApiCall { api.deletePresupuesto(presupuestoId) }

    suspend fun getTransacciones() = safeApiCall { api.getAllTransacciones() }
    suspend fun postTransaccion(transaccion: Transaccion) = safeApiCall { api.postTransaccion(transaccion) }

    suspend fun getUsuarios() = safeApiCall { api.getAllUsuarios() }
    suspend fun getUsuarioByNombre(nombre: String) = safeApiCall { api.getAllUsuarios().find { it.nombre == nombre } }
    suspend fun loginUsuario(login : LoginRequest) = safeApiCall { api.loginUsuario(login) }
    suspend fun postUsuario(usuario: Usuario) = safeApiCall { api.postUsuario(usuario) }


    suspend fun actualizarUsuario(
        context: Context,
        nombre: String,
        correo: String,
        nuevaPassword: String?
    ): Boolean {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("usuario_id", -1)
        if (userId == -1) return false

        val usuarioResponse = api.getUsuario(userId)
        val usuarioActual = usuarioResponse.body() ?: return false

        val usuarioActualizado = usuarioActual.copy(
            nombre = nombre,
            correo = correo,
            password = nuevaPassword ?: usuarioActual.password
        )

        val response = api.actualizarUsuario(userId, usuarioActualizado)
        return response.isSuccessful
    }


    suspend fun verificarPassword(userId: Int, password: String): Boolean {
        val usuarioResponse = api.getUsuario(userId)
        val usuarioActual = usuarioResponse.body() ?: return false

        return if (usuarioActual.password == password) {
            true
        } else {
            Log.e("RemoteDataSource", "Contraseña incorrecta")
            false
        }
    }
    private inline fun <T> safeApiCall(call: () -> T): T? {
        return try {
            call()
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "API Error: ${e.message}")
            null
        }
    }
}
