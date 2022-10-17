package kashyap.anurag.frenzyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzyadmin.databinding.ActivityVerifySellerAccountBinding;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class VerifySellerAccountActivity extends AppCompatActivity {
    private ActivityVerifySellerAccountBinding binding;
    private String shopId, shopName;
    private String verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifySellerAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shopId = getIntent().getStringExtra("shopId");
        verified = getIntent().getStringExtra("verified");

        if (verified.equals("VERIFIED")){
            binding.verifiedBtn.setVisibility(View.GONE);
        }else if (verified.equals("NOT_VERIFIED")){
            binding.verifiedBtn.setVisibility(View.VISIBLE);
        }

        loadShopDetails(shopId);

        binding.verifiedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyDialog(shopName, shopId);
            }
        });
    }
    private void showBuyDialog(String shopName, String shopId) {

        Dialog approveQcDialog = new Dialog(VerifySellerAccountActivity.this);
        approveQcDialog.setContentView(R.layout.approve_qc_dialog);
        approveQcDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView productTitle = approveQcDialog.findViewById(R.id.productTitle);
        TextView closeDialogBtn = approveQcDialog.findViewById(R.id.closeDialogBtn);
        TextView continueBtn = approveQcDialog.findViewById(R.id.continueBtn);
        TextView tv = approveQcDialog.findViewById(R.id.tv);
        TextView packedTv = approveQcDialog.findViewById(R.id.packedTv);

        tv.setText("Verify Seller");
        packedTv.setText("This seller now will be abe to sell his products in frenzy Store");
        continueBtn.setText("Verify");


        approveQcDialog.setCancelable(true);
        productTitle.setText(shopName);


        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveQcDialog.dismiss();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifySellerAccount(shopId);
                approveQcDialog.dismiss();
            }
        });
        approveQcDialog.show();
    }

    private void verifySellerAccount(String shopId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isVerified", true);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(VerifySellerAccountActivity.this, "Seller Account Verified SuccessFully!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(VerifySellerAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerifySellerAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadShopDetails(String shopId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    shopName = snapshot.getString("shopName");
                    String sellerName = snapshot.getString("sellerName");
                    String email = snapshot.getString("email");
                    String phoneNo = snapshot.getString("phoneNo");
                    String address = snapshot.getString("address");
                    String pinCode = snapshot.getString("pinCode");
                    String city = snapshot.getString("city");

                    binding.shopNameTv.setText(shopName);
                    binding.sellerNameTv.setText(sellerName);
                    binding.emailTv.setText(email);
                    binding.phoneNoTv.setText(phoneNo);
                    binding.addressTv.setText(address);
                    binding.pinCodeTv.setText(pinCode);
                    binding.cityTv.setText(city);

                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}