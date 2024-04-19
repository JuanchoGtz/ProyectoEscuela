package com.ubam.proyectoescuelajuan.ui.slideshow

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

class SlideshowFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        dbHelper = DatabaseHelper(requireContext())

        val spinnerCareer: Spinner = root.findViewById(R.id.spinnerCareer)
        val spinnerSemester: Spinner = root.findViewById(R.id.spinnerSemester)
        val editTextGroup: EditText = root.findViewById(R.id.editTextGroup)
        val spinnerShift: Spinner = root.findViewById(R.id.spinnerShift)
        val buttonInsertGroup: Button = root.findViewById(R.id.buttonInsertGroup)

        val careers = arrayOf("Ingeniería en Sistemas", "Diseño de Modas")

        // Adaptador para el spinner de carreras
        val careerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, careers)
        spinnerCareer.adapter = careerAdapter

        // Listener para el botón de registrar grupo
        buttonInsertGroup.setOnClickListener {
            val career = spinnerCareer.selectedItem as String
            val semester = spinnerSemester.selectedItem as String
            val group = editTextGroup.text.toString()
            val shift = spinnerShift.selectedItem as String

            // Lógica para determinar el código del grupo
            val groupCode = buildGroupCode(career, semester, group, shift)

            // Llamada a la función para registrar el grupo
            insertarGrupo(groupCode)
        }

        return root
    }

    private fun buildGroupCode(career: String, semester: String, group: String, shift: String): String {
        val careerCode = if (career == "Ingeniería en Sistemas") "ISC" else "LDM"
        val semesterCode = (semester.toInt() * 10).toString()
        val groupCode = group
        val shiftCode = if (shift == "Vespertino") "V" else "M"
        return "$careerCode$semesterCode$groupCode$shiftCode"
    }

    private fun insertarGrupo(groupName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.GRUPO_COLUMN_NAME, groupName)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_GRUPO, null, values)
        if (newRowId != -1L) {
            // Grupo insertado con éxito
            Toast.makeText(requireContext(), "Grupo insertado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            // Error al insertar el grupo
            Toast.makeText(requireContext(), "Error al insertar el grupo", Toast.LENGTH_SHORT).show()
        }
    }
}
