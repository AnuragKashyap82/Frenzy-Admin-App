package kashyap.anurag.frenzyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzyadmin.Adapters.AdapterActiveProducts;
import kashyap.anurag.frenzyadmin.Adapters.AdapterSellerAccountVerification;
import kashyap.anurag.frenzyadmin.Models.ModelSellerAccountVerification;
import kashyap.anurag.frenzyadmin.databinding.ActivityAllSellerVerificationBinding;

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

public class AllSellerVerification extends AppCompatActivity {
    private ActivityAllSellerVerificationBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterSellerAccountVerification adapterSellerAccountVerification;
    private ArrayList<ModelSellerAccountVerification> sellerAccountVerificationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSellerVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllSellerAccountForVerification();

    }

    private void loadAllSellerAccountForVerification() {
        binding.progressBar.setVisibility(View.VISIBLE);
        sellerAccountVerificationArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Sellers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelSellerAccountVerification modelSellerAccountVerification = document.toObject(ModelSellerAccountVerification.class);

                                checkIsVerified(modelSellerAccountVerification);
                            }

                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AllSellerVerification.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkIsVerified(ModelSellerAccountVerification modelSellerAccountVerification) {
        String shopId = modelSellerAccountVerification.getShopId();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                boolean isVerified = (boolean) snapshot.get("isVerified");
                if (isVerified){
                    binding.progressBar.setVisibility(View.GONE);
                }else {

                    sellerAccountVerificationArrayList.add(modelSellerAccountVerification);
                    adapterSellerAccountVerification = new AdapterSellerAccountVerification(AllSellerVerification.this, sellerAccountVerificationArrayList);
                    binding.allSellerVerificationAccountsRv.setAdapter(adapterSellerAccountVerification);
                    adapterSellerAccountVerification.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}