package com.ubam.proyectoescuelajuan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            val userType = authenticateUser(username, password)

            if (userType != null) {
                when (userType) {
                    "admin" -> {
                        // Usuario es un administrador, redirigir a AdminActivity
                        val intent = Intent(this@MainActivity, admin::class.java)
                        startActivity(intent)
                    }
                    "docente" -> {
                        // Obtener información del docente
                        val docenteInfo = getDocenteInfo(username)

                        // Pasar la información del docente a DocenteActivity
                        val intent = Intent(this@MainActivity, docente::class.java).apply {
                            putExtra("docenteName", docenteInfo?.first)
                            putExtra("docenteSubject", docenteInfo?.second)
                            putExtra("docenteGroup", docenteInfo?.third)
                        }
                        startActivity(intent)
                    }
                    "alumno" -> {
                        // Usuario es un alumno, redirigir a AlumnoActivity
                        val intent = Intent(this@MainActivity, alumno::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateUser(username: String, password: String): String? {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.ADMIN_COLUMN_USERNAME, DatabaseHelper.ADMIN_COLUMN_PASSWORD, DatabaseHelper.ADMIN_COLUMN_HIERARCHY)
        val selection = "${DatabaseHelper.ADMIN_COLUMN_USERNAME} = ? AND ${DatabaseHelper.ADMIN_COLUMN_PASSWORD} = ?"
        val selectionArgs = arrayOf(username, password)

        val cursor = db.query(
            DatabaseHelper.TABLE_ADMIN,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val userType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ADMIN_COLUMN_HIERARCHY))
            cursor.close()
            return userType
        }

        // Si el usuario no es un administrador, verifica si es docente
        val projectionDocente = arrayOf(DatabaseHelper.DOCENTE_COLUMN_USERNAME, DatabaseHelper.DOCENTE_COLUMN_PASSWORD, DatabaseHelper.DOCENTE_COLUMN_HIERARCHY)
        val selectionDocente = "${DatabaseHelper.DOCENTE_COLUMN_USERNAME} = ? AND ${DatabaseHelper.DOCENTE_COLUMN_PASSWORD} = ?"
        val cursorDocente = db.query(
            DatabaseHelper.TABLE_DOCENTE,
            projectionDocente,
            selectionDocente,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursorDocente.moveToFirst()) {
            val userType = cursorDocente.getString(cursorDocente.getColumnIndexOrThrow(
                DatabaseHelper.DOCENTE_COLUMN_HIERARCHY))
            cursorDocente.close()
            return userType
        }

        // Si el usuario no es docente, verifica si es alumno
        val projectionAlumno = arrayOf(DatabaseHelper.ALUMNO_COLUMN_USERNAME, DatabaseHelper.ALUMNO_COLUMN_PASSWORD, DatabaseHelper.ALUMNO_COLUMN_HIERARCHY)
        val selectionAlumno = "${DatabaseHelper.ALUMNO_COLUMN_USERNAME} = ? AND ${DatabaseHelper.ALUMNO_COLUMN_PASSWORD} = ?"
        val cursorAlumno = db.query(
            DatabaseHelper.TABLE_ALUMNO,
            projectionAlumno,
            selectionAlumno,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursorAlumno.moveToFirst()) {
            val userType = cursorAlumno.getString(cursorAlumno.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_HIERARCHY))
            cursorAlumno.close()
            return userType
        }

        // Si no se encuentra el usuario en ninguna tabla, devuelve null
        return null
    }

    private fun getDocenteInfo(username: String): Triple<String?, String?, String?>? {
        // Obtener información del docente desde la base de datos
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.DOCENTE_COLUMN_FIRST_NAME,
            DatabaseHelper.DOCENTE_COLUMN_SUBJECT,
            DatabaseHelper.DOCENTE_COLUMN_GROUP_ID
        )
        val selection = "${DatabaseHelper.DOCENTE_COLUMN_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        val cursor = db.query(
            DatabaseHelper.TABLE_DOCENTE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var docenteName: String? = null
        var subject: String? = null
        var group: String? = null

        if (cursor.moveToFirst()) {
            docenteName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DOCENTE_COLUMN_FIRST_NAME))
            subject = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DOCENTE_COLUMN_SUBJECT))
            group = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DOCENTE_COLUMN_GROUP_ID)).toString()
        }

        cursor.close()

        return Triple(docenteName, subject, group)
    }
}
