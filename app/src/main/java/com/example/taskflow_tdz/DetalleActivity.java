package com.example.taskflow_tdz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class DetalleActivity extends AppCompatActivity {

    TextView txtDescripcion, txtHoras;
    Button btnRegresar, btnEditar, btnEliminar;

    FirebaseFirestore db;
    String actividadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        // Notch / barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        // Vistas
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtHoras = findViewById(R.id.txtHoras);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        db = FirebaseFirestore.getInstance();

        // Recibir ID (IMPORTANTE)
        actividadId = getIntent().getStringExtra("id");

        Log.d("ID_DETALLE", "ID recibido: " + actividadId);

        if (actividadId != null) {
            cargarDetalle(actividadId);
        }

        // 🔙 Regresar
        btnRegresar.setOnClickListener(v -> finish());

        // ✏️ Editar
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActivity.this, EditarActivity.class);
            intent.putExtra("id", actividadId);
            startActivity(intent);
        });

        // 🗑️ Eliminar
        btnEliminar.setOnClickListener(v -> eliminarActividad());
    }

    private void cargarDetalle(String id) {
        db.collection("actividades")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String desc = documentSnapshot.getString("descripcion");
                        Long horas = documentSnapshot.getLong("horas");

                        txtDescripcion.setText(desc != null ? desc : "Sin descripción");
                        txtHoras.setText(horas != null ? horas + " hrs" : "Sin horas");
                    } else {
                        txtDescripcion.setText("Actividad no encontrada");
                        txtHoras.setText("");
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error", e);
                    txtDescripcion.setText("Error al cargar datos");
                });
    }

    // 🗑️ ELIMINAR EN FIREBASE
    private void eliminarActividad() {

        if (actividadId == null) {
            Toast.makeText(this, "Error: ID vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("actividades")
                .document(actividadId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                    finish(); // regresa a lista
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e("DELETE", "Error", e);
                });
    }
}