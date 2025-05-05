import android.util.Log
import com.almadistefano.finantrack.data.FinantrackAPI
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario


/*class RemoteDataSource() {

    private val api = FinantrackAPI.getRetrofit2Api()

    suspend fun getCuentasFromApi(): List<Cuenta> {
        try{
            return api.getAllCuentas()
        }catch ( e: Exception){
           Log.d("RemoteDataSource", "Error al obtener cuentas de la API: ${e.message}")
        }
        return emptyList()
    }

}*/
class RemoteDataSource {

    private val api = FinantrackAPI.getRetrofit2Api()

    suspend fun getCuentas() = safeApiCall { api.getAllCuentas() }
    suspend fun postCuenta(cuenta: Cuenta) = safeApiCall { api.postCuenta(cuenta) }

    suspend fun getCategorias() = safeApiCall { api.getAllCategorias() }
    suspend fun postCategoria(categoria: Categoria) = safeApiCall { api.postCategoria(categoria) }

    suspend fun getPresupuestos() = safeApiCall { api.getAllPresupuestos() }
    suspend fun postPresupuesto(presupuesto: Presupuesto) = safeApiCall { api.postPresupuesto(presupuesto) }

    suspend fun getTransacciones() = safeApiCall { api.getAllTransacciones() }
    suspend fun postTransaccion(transaccion: Transaccion) = safeApiCall { api.postTransaccion(transaccion) }

    suspend fun getUsuarios() = safeApiCall { api.getAllUsuarios() }
    suspend fun postUsuario(usuario: Usuario) = safeApiCall { api.postUsuario(usuario) }

    private inline fun <T> safeApiCall(call: () -> T): T? {
        return try {
            call()
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "API Error: ${e.message}")
            null
        }
    }
}
