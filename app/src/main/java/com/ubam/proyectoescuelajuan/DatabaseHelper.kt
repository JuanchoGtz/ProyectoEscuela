package com.ubam.proyectoescuelajuan

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UBAM.db"

        // Tabla Admin
        const val TABLE_ADMIN = "admin"
        const val ADMIN_COLUMN_ID = "id"
        const val ADMIN_COLUMN_USERNAME = "username"
        const val ADMIN_COLUMN_PASSWORD = "password"
        const val ADMIN_COLUMN_HIERARCHY = "hierarchy"

        // Tabla Docente
        const val TABLE_DOCENTE = "docente"
        const val DOCENTE_COLUMN_ID = "id"
        const val DOCENTE_COLUMN_FIRST_NAME = "first_name"
        const val DOCENTE_COLUMN_LAST_NAME = "last_name"
        const val DOCENTE_COLUMN_USERNAME = "username"
        const val DOCENTE_COLUMN_PASSWORD = "password"
        const val DOCENTE_COLUMN_GROUP_ID = "group_id"
        const val DOCENTE_COLUMN_SUBJECT = "subject"
        const val DOCENTE_COLUMN_HIERARCHY = "hierarchy"

        // Tabla Alumno
        const val TABLE_ALUMNO = "alumno"
        const val ALUMNO_COLUMN_ID = "id"
        const val ALUMNO_COLUMN_FIRST_NAME = "first_name"
        const val ALUMNO_COLUMN_LAST_NAME = "last_name"
        const val ALUMNO_COLUMN_USERNAME = "username"
        const val ALUMNO_COLUMN_PASSWORD = "password"
        const val ALUMNO_COLUMN_GROUP_ID = "group_id"
        const val ALUMNO_COLUMN_SUBJECT = "subject"
        const val ALUMNO_COLUMN_GRADE = "grade"
        const val ALUMNO_COLUMN_HIERARCHY = "hierarchy"

        // Tabla Grupo
        const val TABLE_GRUPO = "grupo"
        const val GRUPO_COLUMN_ID = "id"
        const val GRUPO_COLUMN_NAME = "name"

        // Creación de tablas
        const val CREATE_TABLE_ADMIN = ("CREATE TABLE " + TABLE_ADMIN + "("
                + ADMIN_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ADMIN_COLUMN_USERNAME + " TEXT,"
                + ADMIN_COLUMN_PASSWORD + " TEXT,"
                + ADMIN_COLUMN_HIERARCHY + " TEXT)")

        const val CREATE_TABLE_DOCENTE = ("CREATE TABLE " + TABLE_DOCENTE + "("
                + DOCENTE_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + DOCENTE_COLUMN_FIRST_NAME + " TEXT,"
                + DOCENTE_COLUMN_LAST_NAME + " TEXT,"
                + DOCENTE_COLUMN_USERNAME + " TEXT,"
                + DOCENTE_COLUMN_PASSWORD + " TEXT,"
                + DOCENTE_COLUMN_GROUP_ID + " INTEGER,"
                + DOCENTE_COLUMN_SUBJECT + " TEXT,"
                + DOCENTE_COLUMN_HIERARCHY + " TEXT,"
                + "FOREIGN KEY(" + DOCENTE_COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GRUPO + "(" + GRUPO_COLUMN_ID + "))")

        const val CREATE_TABLE_ALUMNO = ("CREATE TABLE " + TABLE_ALUMNO + "("
                + ALUMNO_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ALUMNO_COLUMN_FIRST_NAME + " TEXT,"
                + ALUMNO_COLUMN_LAST_NAME + " TEXT,"
                + ALUMNO_COLUMN_USERNAME + " TEXT,"
                + ALUMNO_COLUMN_PASSWORD + " TEXT,"
                + ALUMNO_COLUMN_GROUP_ID + " INTEGER,"
                + ALUMNO_COLUMN_SUBJECT + " TEXT,"
                + ALUMNO_COLUMN_GRADE + " REAL,"
                + ALUMNO_COLUMN_HIERARCHY + " TEXT,"
                + "FOREIGN KEY(" + ALUMNO_COLUMN_GROUP_ID + ") REFERENCES " + TABLE_GRUPO + "(" + GRUPO_COLUMN_ID + "))")

        const val CREATE_TABLE_GRUPO = ("CREATE TABLE " + TABLE_GRUPO + "("
                + GRUPO_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + GRUPO_COLUMN_NAME + " TEXT)")
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ADMIN)
        db.execSQL(CREATE_TABLE_DOCENTE)
        db.execSQL(CREATE_TABLE_ALUMNO)
        db.execSQL(CREATE_TABLE_GRUPO)

        // Insertar usuarios
        db.execSQL("INSERT INTO $TABLE_ADMIN ($ADMIN_COLUMN_USERNAME, $ADMIN_COLUMN_PASSWORD, $ADMIN_COLUMN_HIERARCHY) VALUES ('Jesus', 'admin123', 'admin')")
        db.execSQL("INSERT INTO $TABLE_DOCENTE ($DOCENTE_COLUMN_FIRST_NAME, $DOCENTE_COLUMN_LAST_NAME, $DOCENTE_COLUMN_USERNAME, $DOCENTE_COLUMN_PASSWORD, $DOCENTE_COLUMN_GROUP_ID, $DOCENTE_COLUMN_SUBJECT, $DOCENTE_COLUMN_HIERARCHY) VALUES ('Jesus', 'Castillo', 'profeJesus', 'profe123', 1, 'Aplicaciones mobiles', 'docente')")
        db.execSQL("INSERT INTO $TABLE_ALUMNO ($ALUMNO_COLUMN_FIRST_NAME, $ALUMNO_COLUMN_LAST_NAME, $ALUMNO_COLUMN_USERNAME, $ALUMNO_COLUMN_PASSWORD, $ALUMNO_COLUMN_GROUP_ID, $ALUMNO_COLUMN_SUBJECT, $ALUMNO_COLUMN_GRADE, $ALUMNO_COLUMN_HIERARCHY) VALUES ('Juan', 'Gutierrez', 'alumnoJuan', 'juan123', 1, 'Aplicaciones mobiles', NULL, 'alumno')")
        db.execSQL("INSERT INTO $TABLE_GRUPO ($GRUPO_COLUMN_NAME) VALUES ('ISC801V')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar tablas si existen
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADMIN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOCENTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GRUPO")

        // Crear tablas nuevamente
        onCreate(db)
    }
}