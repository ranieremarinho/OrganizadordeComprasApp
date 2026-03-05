package com.adssenac.organizadordecompras;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import com.adssenac.organizadordecompras.data.AppDatabase;
import com.adssenac.organizadordecompras.model.Categoria;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText editNomeCategoria;
    private Button btnSalvarCategoria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        editNomeCategoria = findViewById(R.id.editNomeCategoria);
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria);
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "organizador-db"
        ).allowMainThreadQueries().build();
        btnSalvarCategoria.setOnClickListener(v -> {
            String nome = editNomeCategoria.getText().toString().trim();
            if (!nome.isEmpty()) {
                Categoria categoria = new Categoria(nome);
                db.categoriaDao().inserir(categoria);
                List<Categoria> categorias = db.categoriaDao().listarTodas();
                Toast.makeText(this, "Total: " + categorias.size(), Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Categoria salva!", Toast.LENGTH_SHORT).show();
                editNomeCategoria.setText("");
            } else {
                Toast.makeText(this, "Digite um nome!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}