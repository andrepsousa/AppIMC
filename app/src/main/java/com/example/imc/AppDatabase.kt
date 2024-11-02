package com.example.imc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.imc.model.Usuario

@Database(entities = [Usuario::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // Adicione a migração aqui
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Crie uma nova tabela com a nova estrutura
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS usuarios_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        idade INTEGER NOT NULL,
                        altura REAL NOT NULL,
                        peso REAL NOT NULL,
                        sexo TEXT NOT NULL,
                        imc REAL NOT NULL
                    )
                """.trimIndent())

                // Copie os dados da tabela antiga para a nova tabela
                database.execSQL("""
                    INSERT INTO usuarios_new (nome, idade, altura, peso, sexo, imc)
                    SELECT nome, idade, altura, peso, sexo, imc FROM usuarios
                """.trimIndent())

                // Exclua a tabela antiga
                database.execSQL("DROP TABLE usuarios")

                // Renomeie a nova tabela para o nome da tabela original
                database.execSQL("ALTER TABLE usuarios_new RENAME TO usuarios")
            }
        }
    }
}
