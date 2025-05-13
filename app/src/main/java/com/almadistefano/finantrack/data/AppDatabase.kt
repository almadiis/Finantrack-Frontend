import androidx.room.Database
import androidx.room.RoomDatabase
import com.almadistefano.finantrack.data.dao.CategoriaDao
import com.almadistefano.finantrack.data.dao.CuentaDao
import com.almadistefano.finantrack.data.dao.PresupuestoDao
import com.almadistefano.finantrack.data.dao.TransaccionDao
import com.almadistefano.finantrack.data.dao.UsuarioDao
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario

@Database(
    entities = [Cuenta::class, Categoria::class, Presupuesto::class, Transaccion::class, Usuario::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun presupuestoDao(): PresupuestoDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun usuarioDao(): UsuarioDao
}
