package kashyap.anurag.frenzyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzyadmin.Adapters.AdapterNewQC;
import kashyap.anurag.frenzyadmin.Models.ModelNewQC;
import kashyap.anurag.frenzyadmin.databinding.ActivityNewQcactivityBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewQCActivity extends AppCompatActivity {
    private ActivityNewQcactivityBinding binding;
    private AdapterNewQC adapterNewQC;
    private ArrayList<ModelNewQC> newQCArrayList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewQcactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllNewQCProduct();
    }

    private void loadAllNewQCProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);
        newQCArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelNewQC modelNewQC = document.toObject(ModelNewQC.class);

                                checkIdActive(modelNewQC);
                            }

                        }else {

                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewQCActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIdActive(ModelNewQC modelNewQC) {
        String productId = modelNewQC.getProductId();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                boolean isActive = (boolean) snapshot.get("active");
                if (isActive){

                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    boolean isDetailsCompleted = (boolean) snapshot.get("isDetailsComplete");
                    if (isDetailsCompleted){

                        newQCArrayList.add(modelNewQC);
                        adapterNewQC = new AdapterNewQC(NewQCActivity.this, newQCArrayList);
                        binding.newQCRv.setAdapter(adapterNewQC);
                        adapterNewQC.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);

                    }else{
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

    }
}