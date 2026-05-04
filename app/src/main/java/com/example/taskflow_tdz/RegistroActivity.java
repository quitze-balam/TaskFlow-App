package com.example.taskflow_tdz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etHoras = findViewById(R.id.etHoras);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // GUARDAR
        btnGuardar.setOnClickListener(v -> {

            String descripcion = etDescripcion.getText().toString().trim();
            String horasTexto = etHoras.getText().toString().trim();

            // 1. Validar vacíos
            if (descripcion.isEmpty() || horasTexto.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Validar longitud
            if (descripcion.length() < 3 || descripcion.length() > 100) {
                Toast.makeText(this, "Descripción no válida", Toast.LENGTH_SHORT).show();
                return;
            }

            int horas;

            // 3. Validar número
            try {
                horas = Integer.parseInt(horasTexto);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Horas inválidas", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Validar rango
            if (horas < 1 || horas > 500) {
                Toast.makeText(this, "Horas fuera de rango", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. Guardar en Firebase
            Map<String, Object> actividad = new HashMap<>();
            actividad.put("descripcion", descripcion);
            actividad.put("horas", horas);

            db.collection("actividades")
                    .add(actividad)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Actividad guardada", Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", "Guardado");
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        Log.w("Firebase", "Error", e);
                    });
        });

        // CANCELAR
        btnCancelar.setOnClickListener(v -> finish());
    }
}