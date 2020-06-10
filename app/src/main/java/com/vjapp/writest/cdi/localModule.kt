import androidx.room.Room
import com.vjapp.writest.data.local.database.AppDatabase
import com.vjapp.writest.data.local.database.DatabaseConstants
import com.vjapp.writest.data.repository.datasource.LocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val localModule = module {

    single(DatabaseConstants.DATABASE_NAME) {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, DatabaseConstants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single("LOCAL_DATA_SOURCE") { LocalDataSource(get(DatabaseConstants.DATABASE_NAME)) }

}