package kashyap.anurag.frenzyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import kashyap.anurag.frenzyadmin.Adapters.ProductDescriptionAdapter;
import kashyap.anurag.frenzyadmin.databinding.ActivityApproveQcactivityBinding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApproveQCActivity extends AppCompatActivity {
    private ActivityApproveQcactivityBinding binding;

    private LinearLayout productDetailsLl, addToCartBuyNowLl, outOfStockLl;
    private ConstraintLayout productDetailsTabContainer, rewardLayout;
    private RelativeLayout  productDetailsRlLayout;
    private String productId, productHead, price, BUTTON_CODE;

    private ViewPager productDescriptionViewPager;
    private TabLayout productDescriptionTabLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView productTitle, productPrice, productDetailsTv, cuttedPrice,
            codIndicatorTv, rewardTitle, rewardBody, namePincodeTv, completeAddressTv, codTv,
            deliverTo,
            deliveryWithinTv, deliveryFeeTv, percentDiscountTv;
    private ImageView codIndicator;

    private ImageSlider bannerSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApproveQcactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getStringExtra("productId");
        productHead = getIntent().getStringExtra("productTitle");
        BUTTON_CODE = getIntent().getStringExtra("BUTTON_CODE");

        if (BUTTON_CODE.equals("HIDE")){
            binding.approveQCBtn.setEnabled(false);
        }else if (BUTTON_CODE.equals("SHOW")){
            binding.approveQCBtn.setEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        productDescriptionViewPager = findViewById(R.id.productDetailsViewPager);
        productDescriptionTabLayout = findViewById(R.id.productDetailsTabLayout);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productDetailsTabContainer = findViewById(R.id.productDetailsTabContainer);
        productDetailsRlLayout = findViewById(R.id.productDetailsRlLayout);
        productDetailsTv = findViewById(R.id.productDetailsTv);
        productDetailsLl = findViewById(R.id.productDetailsLl);
        bannerSlider = findViewById(R.id.bannerSlider);
        cuttedPrice = findViewById(R.id.cuttedPrice);
        codIndicatorTv = findViewById(R.id.codIndicatorTv);
        codIndicator = findViewById(R.id.codIndicator);
        rewardLayout = findViewById(R.id.rewardLayout);
        rewardBody = findViewById(R.id.rewardBody);
        rewardTitle = findViewById(R.id.rewardTitle);
        addToCartBuyNowLl = findViewById(R.id.addToCartBuyNowLl);
        outOfStockLl = findViewById(R.id.outOfStockLl);
        completeAddressTv = findViewById(R.id.completeAddressTv);
        namePincodeTv = findViewById(R.id.namePincodeTv);
        codTv = findViewById(R.id.codTv);
        deliverTo = findViewById(R.id.deliverTo);
        deliveryWithinTv = findViewById(R.id.deliveryWithinTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        percentDiscountTv = findViewById(R.id.percentDiscountTv);

        loadProductDetails(productId);
        checkRewardAvailable(productId);
        loadMyAddress();
        loadInStock(productId);
        loadBannerSlider();

        productDescriptionViewPager.setAdapter(new ProductDescriptionAdapter(getSupportFragmentManager(), productDescriptionTabLayout.getTabCount()));
        productDescriptionViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDescriptionTabLayout));
        productDescriptionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                productDescriptionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.approveQCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyDialog(productId, productHead);
            }
        });
    }
    private void showBuyDialog(String productId, String productHead) {

        binding.progressBar.setVisibility(View.GONE);

        Dialog approveQcDialog = new Dialog(ApproveQCActivity.this);
        approveQcDialog.setContentView(R.layout.approve_qc_dialog);
        approveQcDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView productTitle = approveQcDialog.findViewById(R.id.productTitle);
        TextView closeDialogBtn = approveQcDialog.findViewById(R.id.closeDialogBtn);
        TextView continueBtn = approveQcDialog.findViewById(R.id.continueBtn);
        approveQcDialog.setCancelable(true);
        productTitle.setText(productHead);


        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveQcDialog.dismiss();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveQc(productId);
                approveQcDialog.dismiss();
            }
        });
        approveQcDialog.show();
    }

    private void approveQc(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("active", true);

        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           binding.progressBar.setVisibility(View.GONE);
                           approveQcRealtimeDb(productId);
                       }else {
                           binding.progressBar.setVisibility(View.GONE);
                           Toast.makeText(ApproveQCActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(ApproveQCActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void approveQcRealtimeDb(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("active", true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(ApproveQCActivity.this, "QC approved Successfully!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(ApproveQCActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApproveQCActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkRewardAvailable(String productId) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });
    }


    private void loadProductDetails(String productId) {

        List<String> productImages = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("products").document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();


                            productPrice.setText("Rs."+documentSnapshot.get("productPrice").toString());

                            price = documentSnapshot.get("productPrice").toString();


                            String deliveryWithin = documentSnapshot.get("deliveryWithin").toString();

                            String deliveryFee = documentSnapshot.get("deliveryFee").toString();

                            if (deliveryFee.equals("FREE Delivery")){
                                deliveryFeeTv.setText("FREE Delivery");
                            }else {
                                deliveryFeeTv.setText("Rs."+deliveryFee);
                            }

                            productPrice.setText("Rs."+price);
                            deliveryWithinTv.setText("Delivery within "+deliveryWithin+ " days");

                            String cuttedPrice1 = documentSnapshot.get("productCuttedPrice").toString();
                            cuttedPrice.setText("Rs."+cuttedPrice1);

                            productTitle.setText(documentSnapshot.get("productTitle").toString());
//                            rewardTitle.setText(documentSnapshot.get("rewardTitle").toString());
//                            rewardBody.setText(documentSnapshot.get("rewardBody").toString());
                            productDetailsTv.setText(documentSnapshot.get("allDetails").toString());

                            if ((boolean) documentSnapshot.get("isCod")){
                                codIndicator.setVisibility(View.VISIBLE);
                                codIndicatorTv.setVisibility(View.VISIBLE);
                                codTv.setText("Cash on delivery available");
                            }else {
                                codIndicator.setVisibility(View.GONE);
                                codIndicatorTv.setVisibility(View.GONE);
                                codTv.setText("Cash on delivery not available");
                            }
                            if ((boolean) documentSnapshot.get("isTabSelected")){
                                productDetailsRlLayout.setVisibility(View.GONE);
                                productDetailsTabContainer.setVisibility(View.VISIBLE);
                            }else {
                                productDetailsRlLayout.setVisibility(View.VISIBLE);
                                productDetailsTabContainer.setVisibility(View.GONE);
                            }

                            int percentDiscount = ((Integer.parseInt(cuttedPrice1) - Integer.parseInt(price)) * 100)/Integer.parseInt(cuttedPrice1);
                            percentDiscountTv.setText(percentDiscount+"% OFF");

                        } else {
                            Toast.makeText(ApproveQCActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
    private void loadInStock(String productId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference.child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String noOfQuantity = ""+snapshot.child("noOfQuantity").getValue();
                        if (noOfQuantity.equals("0")){
                            addToCartBuyNowLl.setVisibility(View.GONE);
                            outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else if (Integer.parseInt(noOfQuantity) < 0){
                            addToCartBuyNowLl.setVisibility(View.GONE);
                            outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else {
                            addToCartBuyNowLl.setVisibility(View.VISIBLE);
                            outOfStockLl.setVisibility(View.GONE);
                            changeInStockTrue(productId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void changeInStock(String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("inStock", false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ApproveQCActivity.this, "InStock Updated false!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApproveQCActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void changeInStockTrue(String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("inStock", true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ApproveQCActivity.this, "InStock Updated true!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApproveQCActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMyAddress() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myAddress").document("default")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {
                            namePincodeTv.setVisibility(View.VISIBLE);
                            completeAddressTv.setVisibility(View.VISIBLE);
                            deliverTo.setVisibility(View.VISIBLE);

                            String name = snapshot.get("name").toString();
                            String area = snapshot.get("area").toString();
                            String city = snapshot.get("city").toString();
                            String landmark = snapshot.get("landmark").toString();
                            String phoneNo = snapshot.get("phoneNo").toString();
                            String pinCode = snapshot.get("pinCode").toString();
                            String state = snapshot.get("state").toString();

                            namePincodeTv.setText(name + ", "+pinCode);
                            completeAddressTv.setText(city + " " + area+"  " + landmark +", "+ state + ", " + pinCode);


                        } else {
                            Toast.makeText(ApproveQCActivity.this, "No Default Address!!!!", Toast.LENGTH_SHORT).show();
                            namePincodeTv.setVisibility(View.GONE);
                            completeAddressTv.setVisibility(View.GONE);
                            deliverTo.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void loadBannerSlider() {
        final List<SlideModel> bannerSliderModel = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("productImages").child(productId).child("BannerImages");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot data : snapshot.getChildren())
                                bannerSliderModel.add(new SlideModel(data.child("productImage").getValue().toString(), ScaleTypes.FIT));

                            bannerSlider.setImageList(bannerSliderModel, ScaleTypes.CENTER_CROP);
                            bannerSlider.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onItemSelected(int i) {
                                    Toast.makeText(ApproveQCActivity.this, "Banner Slider!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(ApproveQCActivity.this, "No ProductImages!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}