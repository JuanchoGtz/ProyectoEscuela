package com.ubam.proyectoescuelajuan.ui.gallery


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


class GalleryFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinnerGroup: Spinner
    private lateinit var editTextSubject: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        dbHelper = DatabaseHelper(requireContext())

        editTextFirstName = view.findViewById(R.id.editTextFirstName)
        editTextLastName = view.findViewById(R.id.editTextLastName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        spinnerGroup = view.findViewById(R.id.spinnerGroup)
        editTextSubject = view.findViewById(R.id.editTextSubject)

        val buttonInsertDocente = view.findViewById<Button>(R.id.buttonInsertDocente)
        buttonInsertDocente.setOnClickListener {
            insertarDocente()
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

    private fun insertarDocente() {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.DOCENTE_COLUMN_FIRST_NAME, editTextFirstName.text.toString())
            put(DatabaseHelper.DOCENTE_COLUMN_LAST_NAME, editTextLastName.text.toString())
            put(DatabaseHelper.DOCENTE_COLUMN_USERNAME, editTextUsername.text.toString())
            put(DatabaseHelper.DOCENTE_COLUMN_PASSWORD, editTextPassword.text.toString())
            put(DatabaseHelper.DOCENTE_COLUMN_GROUP_ID, spinnerGroup.selectedItemPosition + 1) // Grupo seleccionado
            put(DatabaseHelper.DOCENTE_COLUMN_SUBJECT, editTextSubject.text.toString()) // Asignatura
            put(DatabaseHelper.DOCENTE_COLUMN_HIERARCHY, "docente") // Valor "docente" para la jerarquía
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_DOCENTE, null, values)
        if (newRowId != -1L) {
            Toast.makeText(requireContext(), "Docente insertado exitosamente.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Error al insertar docente.", Toast.LENGTH_SHORT).show()
        }
    }
}