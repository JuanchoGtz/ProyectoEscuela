package com.ubam.proyectoescuelajuan

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class docente : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente)

        dbHelper = DatabaseHelper(this)

        // Obtener referencias a las vistas
        val textViewDocenteName = findViewById<TextView>(R.id.textViewDocenteName)
        val textViewDocenteSubject = findViewById<TextView>(R.id.textViewDocenteSubject)
        val textViewDocenteGroup = findViewById<TextView>(R.id.textViewDocenteGroup)
        val tableLayoutAlumnos = findViewById<TableLayout>(R.id.tableLayoutAlumnos)
        val buttonSaveGrades = findViewById<Button>(R.id.buttonSaveGrades)

        // Obtener información del docente desde el intent
        val docenteName = intent.getStringExtra("docenteName") ?: "Nombre del Docente"
        val docenteSubject = intent.getStringExtra("docenteSubject") ?: "Materia"
        val docenteGroup = intent.getStringExtra("docenteGroup") ?: "Grupo"

        // Mostrar la información del docente
        textViewDocenteName.text = docenteName
        textViewDocenteSubject.text = docenteSubject
        textViewDocenteGroup.text = docenteGroup

        // Mostrar la lista de alumnos
        displayAlumnos(tableLayoutAlumnos, docenteGroup)

        // Manejar clic en el botón de guardar calificaciones
        buttonSaveGrades.setOnClickListener {
            // Actualizar las calificaciones en la base de datos
            guardarCalificacionesEnBaseDeDatos(tableLayoutAlumnos, docenteGroup)
        }
    }

    private fun displayAlumnos(tableLayout: TableLayout, docenteGroup: String) {
        // Mostrar la lista de alumnos en la tabla
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME,
            DatabaseHelper.ALUMNO_COLUMN_LAST_NAME
        )
        val selection = "${DatabaseHelper.ALUMNO_COLUMN_GROUP_ID} = ?"
        val selectionArgs = arrayOf(docenteGroup) // Utilizar el grupo del docente actual

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_ALUMNO,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val alumnoFirstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME))
            val alumnoLastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_LAST_NAME))

            // Crear una fila para cada alumno y añadirlo a la tabla
            val tableRow = TableRow(this)
            val textViewAlumnoName = TextView(this).apply {
                text = "$alumnoFirstName $alumnoLastName"
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                setPadding(8, 8, 8, 8)
            }

            val editTextGrade = EditText(this).apply {
                // Configurar EditText para la calificación
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                setPadding(8, 8, 8, 8)
            }

            // Agregar vistas a la fila y la fila a la tabla
            tableRow.addView(textViewAlumnoName)
            tableRow.addView(editTextGrade)
            tableLayout.addView(tableRow)
        }

        cursor.close()
    }

    private fun guardarCalificacionesEnBaseDeDatos(tableLayout: TableLayout, docenteGroup: String) {
        val db = dbHelper.writableDatabase
        val selection = "${DatabaseHelper.ALUMNO_COLUMN_GROUP_ID} = ?"
        val selectionArgs = arrayOf(docenteGroup) // Utilizar el grupo del docente actual

        for (i in 0 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as TableRow
            val editTextGrade = tableRow.getChildAt(1) as EditText // El índice 1 corresponde a la caja de texto de calificación

            val newGrade = editTextGrade.text.toString().toFloatOrNull()
            if (newGrade != null) {
                val alumnoId = obtenerIdAlumnoDesdeLaCajaDeTexto(editTextGrade)
                if (alumnoId != null) {
                    // Actualizar la calificación en la base de datos
                    val values = ContentValues().apply {
                        put(DatabaseHelper.ALUMNO_COLUMN_GRADE, newGrade)
                    }
                    val affectedRows = db.update(
                        DatabaseHelper.TABLE_ALUMNO,
                        values,
                        "${DatabaseHelper.ALUMNO_COLUMN_ID} = ?",
                        arrayOf(alumnoId.toString())
                    )

                    if (affectedRows == 1) {
                        // Éxito al actualizar la calificación
                        mostrarMensaje("Calificación actualizada con éxito")
                    } else {
                        // Error al actualizar la calificación
                        mostrarMensaje("Error al actualizar la calificación")
                    }
                }
            }
        }
    }

    private fun obtenerIdAlumnoDesdeLaCajaDeTexto(editText: EditText): Int? {
        // Obtener el ID del alumno asociado a la caja de texto
        val parentTableRow = editText.parent as? TableRow
        val alumnoName = (parentTableRow?.getChildAt(0) as? TextView)?.text.toString() // El índice 0 corresponde al nombre del alumno
        val db = dbHelper.readableDatabase
        val selection = "${DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME} || ' ' || ${DatabaseHelper.ALUMNO_COLUMN_LAST_NAME} = ?"
        val selectionArgs = arrayOf(alumnoName)

        val cursor = db.query(
            DatabaseHelper.TABLE_ALUMNO,
            arrayOf(DatabaseHelper.ALUMNO_COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val alumnoId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ALUMNO_COLUMN_ID))
        } else {
            null
        }

        cursor.close()
        return alumnoId
    }

    private fun mostrarMensaje(mensaje: String) {
        // Mostrar un Toast con el mensaje especificado
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}