package com.almadistefano.finantrack.data

import com.almadistefano.finantrack.model.Cuenta
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.almadistefano.finantrack.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.http.*

class FinantrackAPI {

    companion object {
        const val BASE_URL = "http://10.0.2.2:8081/"

        fun getRetrofit2Api(): FinantrackAPIInterface {
            // Añade logging
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // Usa el cliente con logging
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FinantrackAPIInterface::class.java)
        }
    }
}



interface FinantrackAPIInterface {

    // --- Cuentas ---
    @GET("api/cuentas")
    suspend fun getAllCuentas(): List<Cuenta>

    @POST("api/cuentas")
    suspend fun postCuenta(@Body cuenta: Cuenta): Cuenta

    @PUT("api/cuentas/{id}")
    suspend fun updateCuenta(@Path("id") id: Int, @Body cuenta: Cuenta): Cuenta

    @DELETE("api/cuentas/{id}")
    suspend fun deleteCuenta(@Path("id") id: Int): Unit

    // --- Categorías ---
    @GET("api/categorias")
    suspend fun getAllCategorias(): List<Categoria>

    @POST("api/categorias")
    suspend fun postCategoria(@Body categoria: Categoria): Categoria

    @PUT("api/categorias/{id}")
    suspend fun updateCategoria(@Path("id") id: Int, @Body categoria: Categoria): Categoria

    @DELETE("api/categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Int): Unit

    // --- Presupuestos ---
    @GET("api/presupuestos")
    suspend fun getAllPresupuestos(): List<Presupuesto>

    @POST("api/presupuestos")
    suspend fun postPresupuesto(@Body presupuesto: Presupuesto): Presupuesto

    @PUT("api/presupuestos/{id}")
    suspend fun updatePresupuesto(@Path("id") id: Int, @Body presupuesto: Presupuesto): Presupuesto

    @DELETE("api/presupuestos/{id}")
    suspend fun deletePresupuesto(@Path("id") id: Int): Unit

    // --- Transacciones ---
    @GET("api/transacciones")
    suspend fun getAllTransacciones(): List<Transaccion>

    @POST("api/transacciones")
    suspend fun postTransaccion(@Body transaccion: Transaccion): Transaccion

    @PUT("api/transacciones/{id}")
    suspend fun updateTransaccion(@Path("id") id: Int, @Body transaccion: Transaccion): Transaccion

    @DELETE("api/transacciones/{id}")
    suspend fun deleteTransaccion(@Path("id") id: Int): Unit

    // --- Usuarios ---
    @GET("api/usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<Usuario>

    @POST("api/usuarios/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Usuario?

    @POST("api/usuarios")
    suspend fun postUsuario(@Body usuario: Usuario): Usuario

    @PUT("/api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: Usuario
    ): Response<Usuario>

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Unit
}
