package com.ubam.proyectoescuelajuan.ui.home


import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ubam.proyectoescuelajuan.DatabaseHelper
import com.ubam.proyectoescuelajuan.R

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinnerGroup: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        dbHelper = DatabaseHelper(requireContext())

        editTextFirstName = view.findViewById(R.id.editTextFirstName)
        editTextLastName = view.findViewById(R.id.editTextLastName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        spinnerGroup = view.findViewById(R.id.spinnerGroup)

        val buttonInsertAlumno = view.findViewById<Button>(R.id.buttonInsertAlumno)
        buttonInsertAlumno.setOnClickListener {
            insertarAlumno()
        }

        cargarGruposDisponibles()

        return view
    }

    private fun cargarGruposDisponibles() {
        val grupos = obtenerGruposDisponibles()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grupos)
        spinnerGroup.adapter = adapter
    }

    private fun obtenerGruposDisponibles(): List<String> {
        val db = dbHelper.readableDatabase
        val grupos = mutableListOf<String>()
        val query = "SELECT ${DatabaseHelper.GRUPO_COLUMN_NAME} FROM ${DatabaseHelper.TABLE_GRUPO}"

        val cursor = db.rawQuery(query, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val groupName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GRUPO_COLUMN_NAME))
                    grupos.add(groupName)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        return grupos
    }

    private fun insertarAlumno() {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.ALUMNO_COLUMN_FIRST_NAME, editTextFirstName.text.toString())
            put(DatabaseHelper.ALUMNO_COLUMN_LAST_NAME, editTextLastName.text.toString())
            put(DatabaseHelper.ALUMNO_COLUMN_USERNAME, editTextUsername.text.toString())
            put(DatabaseHelper.ALUMNO_COLUMN_PASSWORD, editTextPassword.text.toString())
            put(DatabaseHelper.ALUMNO_COLUMN_GROUP_ID, spinnerGroup.selectedItemPosition + 1) // Grupo al que pertenece el alumno
            put(DatabaseHelper.ALUMNO_COLUMN_SUBJECT, "MateriaAlumno")
            put(DatabaseHelper.ALUMNO_COLUMN_GRADE, null as Double?) // Calificación inicialmente nula
            put(DatabaseHelper.ALUMNO_COLUMN_HIERARCHY, "alumno") // Valor "alumno" para la jerarquía
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_ALUMNO, null, values)
        if (newRowId != -1L) {
            Toast.makeText(requireContext(), "Alumno insertado exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Error al insertar alumno", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}