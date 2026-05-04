package com.example.taskflow_tdz;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> lista;
    ArrayList<String> listaIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NOTCH Y BARRA
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        // BOTÓN AGREGAR
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // LISTVIEW
        ListView listView = findViewById(R.id.ListView);
        lista = new ArrayList<>();
        listaIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(adapter);

        // CLICK EN ITEM → DETALLE (con ID real)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
            intent.putExtra("actividad", lista.get(position));
            intent.putExtra("id", listaIds.get(position));
            startActivity(intent);
        });
    }

    // REFRESCAR LISTA
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("actividades")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    listaIds.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String desc = doc.getString("descripcion");
                        Long horas = doc.getLong("horas");

                        lista.add(desc + " - " + horas + " hrs");
                        listaIds.add(doc.getId());
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error", e);
                });
    }
}