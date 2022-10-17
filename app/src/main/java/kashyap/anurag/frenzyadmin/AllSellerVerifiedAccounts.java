package kashyap.anurag.frenzyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzyadmin.Adapters.AdapterSellerAccountVerification;
import kashyap.anurag.frenzyadmin.Adapters.AdapterVerifiedSeller;
import kashyap.anurag.frenzyadmin.Models.ModelVerifiedSeller;
import kashyap.anurag.frenzyadmin.databinding.ActivityAllSellerVerifiedAccountsBinding;

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

public class AllSellerVerifiedAccounts extends AppCompatActivity {
    private ActivityAllSellerVerifiedAccountsBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterVerifiedSeller adapterVerifiedSeller;
    private ArrayList<ModelVerifiedSeller> verifiedSellerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllSellerVerifiedAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllVerifiedSellers();
    }
    private void loadAllVerifiedSellers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        verifiedSellerArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Sellers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelVerifiedSeller modelVerifiedSeller = document.toObject(ModelVerifiedSeller.class);

                                checkIsVerified(modelVerifiedSeller);
                            }

                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AllSellerVerifiedAccounts.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void checkIsVerified(ModelVerifiedSeller modelVerifiedSeller) {
        String shopId = modelVerifiedSeller.getShopId();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                boolean isVerified = (boolean) snapshot.get("isVerified");
                if (isVerified){
                    verifiedSellerArrayList.add(modelVerifiedSeller);
                    adapterVerifiedSeller = new AdapterVerifiedSeller(AllSellerVerifiedAccounts.this, verifiedSellerArrayList);
                    binding.verifiedSellerRv.setAdapter(adapterVerifiedSeller);
                    adapterVerifiedSeller.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}