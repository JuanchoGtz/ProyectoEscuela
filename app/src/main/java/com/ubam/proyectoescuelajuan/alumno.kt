package com.ubam.proyectoescuelajuan

import android.database.Cursor
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class alumno : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        dbHelper = DatabaseHelper(this)

        // Obtener referencia a la tabla de alumnos
        val tableLayoutAlumnos = findViewById<TableLayout>(R.id.tableLayoutAlumnos)

        // Mostrar la lista de alumnos
        displayAlumnos(tableLayoutAlumnos)
    }

    private fun displayAlumnos(tableLayout: TableLayout) {
        // Mostrar la lista de alumnos en la tabla
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME,
            DatabaseHelper.ALUMNO_COLUMN_LAST_NAME,
            DatabaseHelper.ALUMNO_COLUMN_GROUP_ID,
            DatabaseHelper.ALUMNO_COLUMN_GRADE
        )

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_ALUMNO,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val alumnoFirstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME))
            val alumnoLastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_LAST_NAME))
            val alumnoGroupId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_GROUP_ID))
            val alumnoGrade = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_GRADE))

            // Obtener la materia del docente correspondiente al grupo del alumno
            val docenteSubject = getDocenteSubject(alumnoGroupId)

            // Crear una fila para cada alumno y añadirlo a la tabla
            val tableRow = TableRow(this)
            val textViewAlumnoInfo = TextView(this).apply {
                text = "$alumnoFirstName $alumnoLastName | Grupo $alumnoGroupId | $docenteSubject | Calificación: $alumnoGrade"
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                setPadding(8, 8, 8, 8)
            }

            // Agregar vista a la fila y la fila a la tabla
            tableRow.addView(textViewAlumnoInfo)
            tableLayout.addView(tableRow)
        }

        cursor.close()
    }

    private fun getDocenteSubject(groupId: Int): String? {
        // Obtener la materia del docente correspondiente al grupo del alumno
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.DOCENTE_COLUMN_SUBJECT)
        val selection = "${DatabaseHelper.DOCENTE_COLUMN_GROUP_ID} = ?"
        val selectionArgs = arrayOf(groupId.toString())

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_DOCENTE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var subject: String? = null
        if (cursor.moveToFirst()) {
            subject = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DOCENTE_COLUMN_SUBJECT))
        }

        cursor.close()
        return subject
    }
}