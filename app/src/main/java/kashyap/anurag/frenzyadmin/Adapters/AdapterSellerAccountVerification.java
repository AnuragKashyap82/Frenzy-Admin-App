package kashyap.anurag.frenzyadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzyadmin.Models.ModelSellerAccountVerification;
import kashyap.anurag.frenzyadmin.R;
import kashyap.anurag.frenzyadmin.VerifySellerAccountActivity;

public class AdapterSellerAccountVerification extends RecyclerView.Adapter<AdapterSellerAccountVerification.HolderSellerAccountVerification> {

    private Context context;
    private ArrayList<ModelSellerAccountVerification> sellerAccountVerificationArrayList;

    public AdapterSellerAccountVerification(Context context, ArrayList<ModelSellerAccountVerification> sellerAccountVerificationArrayList) {
        this.context = context;
        this.sellerAccountVerificationArrayList = sellerAccountVerificationArrayList;
    }

    @NonNull
    @Override
    public HolderSellerAccountVerification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_seller_account_verification, parent, false);
        return new HolderSellerAccountVerification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellerAccountVerification holder, int position) {
        final ModelSellerAccountVerification modelSellerAccountVerification = sellerAccountVerificationArrayList.get(position);
        String shopId = modelSellerAccountVerification.getShopId();
        String shopName = modelSellerAccountVerification.getShopName();
        String sellerName = modelSellerAccountVerification.getSellerName();

        holder.shopName.setText(shopName);
        holder.sellerName.setText("Owned By: "+sellerName);
        holder.addressTv.setText("address!!!to be integrated");

        loadAddressDetails(shopId, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VerifySellerAccountActivity.class);
                intent.putExtra("shopId", ""+shopId);
                intent.putExtra("verified", "NOT_VERIFIED");
                context.startActivity(intent);
            }
        });
    }

    private void loadAddressDetails(String shopId, HolderSellerAccountVerification holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String city = snapshot.get("city").toString();
                String pinCode = snapshot.get("pinCode").toString();

                holder.addressTv.setText(city+" - "+pinCode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellerAccountVerificationArrayList.size();
    }

    public class HolderSellerAccountVerification extends RecyclerView.ViewHolder {

        private TextView shopName, sellerName, addressTv;

        public HolderSellerAccountVerification(@NonNull View itemView) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shopName);
            sellerName = itemView.findViewById(R.id.sellerName);
            addressTv = itemView.findViewById(R.id.addressTv);
        }
    }
}
