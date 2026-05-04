package com.example.taskflow_tdz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditarActivity extends AppCompatActivity {

    EditText edtDescripcion, edtHoras;
    Button btnGuardar, btnCancelar;

    FirebaseFirestore db;
    String actividadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        // Vistas
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtHoras = findViewById(R.id.edtHoras);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        db = FirebaseFirestore.getInstance();

        // Recibir ID
        actividadId = getIntent().getStringExtra("id");

        // ⚠️ VALIDACIÓN IMPORTANTE
        if (actividadId == null || actividadId.isEmpty()) {
            Toast.makeText(this, "Error: ID no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatos(actividadId);

        // Cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatos(String id) {
        db.collection("actividades")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String desc = documentSnapshot.getString("descripcion");
                        Long horas = documentSnapshot.getLong("horas");

                        edtDescripcion.setText(desc != null ? desc : "");
                        edtHoras.setText(horas != null ? String.valueOf(horas) : "");
                    } else {
                        Toast.makeText(this, "No se encontró la actividad", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error cargando datos", e);
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void guardarCambios() {

        if (actividadId == null) {
            Toast.makeText(this, "Error: ID inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevaDesc = edtDescripcion.getText().toString().trim();
        String horasStr = edtHoras.getText().toString().trim();

        if (nuevaDesc.isEmpty() || horasStr.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaDesc.length() < 3 || nuevaDesc.length() > 100) {
            Toast.makeText(this, "Descripción no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        int nuevasHoras;

        try {
            nuevasHoras = Integer.parseInt(horasStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Horas inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevasHoras < 1 || nuevasHoras > 500) {
            Toast.makeText(this, "Horas fuera de rango", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("actividades")
                .document(actividadId)
                .update(
                        "descripcion", nuevaDesc,
                        "horas", nuevasHoras
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error update", e);
                });
    }
}