package kashyap.anurag.frenzyadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzyadmin.ApproveQCActivity;
import kashyap.anurag.frenzyadmin.Models.ModelNewQC;
import kashyap.anurag.frenzyadmin.R;

public class AdapterActiveProducts extends RecyclerView.Adapter<AdapterActiveProducts.HolderActiveProducts> {

    private Context context;
    private ArrayList<ModelNewQC> newQCArrayList;

    public AdapterActiveProducts(Context context, ArrayList<ModelNewQC> newQCArrayList) {
        this.context = context;
        this.newQCArrayList = newQCArrayList;
    }

    @NonNull
    @Override
    public HolderActiveProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_produt_qc_item, parent, false);
        return new HolderActiveProducts(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderActiveProducts holder, int position) {
        final ModelNewQC modelNewQC = newQCArrayList.get(position);
        String productId = modelNewQC.getProductId();
        String shopId = modelNewQC.getShopId();

        loadProductDetails(productId, holder);
        loadShopDetails(shopId, holder);
    }

    private void loadShopDetails(String shopId, HolderActiveProducts holder) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String shopName = snapshot.getString("shopName");

                holder.shopNameTv.setText(shopName);


            }
        });
    }

    private void loadProductDetails(String productId, HolderActiveProducts holder) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.getString("productTitle");
                String productImage = snapshot.getString("productImage");
                String processedDate = snapshot.getString("processedDate");
                String processedTime = snapshot.getString("processedTime");
                String category = snapshot.getString("category");

                holder.dateTv.setText(processedDate+" - "+processedTime);
                holder.productTitle.setText(productTitle);
                holder.categoryTv.setText(category);
                try {
                    Picasso.get().load(productImage).placeholder(R.drawable.ic_cart_black).into(holder.productImage);
                } catch (Exception e) {
                    holder.productImage.setImageResource(R.drawable.ic_cart_black);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ApproveQCActivity.class);
                        intent.putExtra("productId", ""+productId);
                        intent.putExtra("productTitle", ""+productTitle);
                        intent.putExtra("BUTTON_CODE", "HIDE");
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return newQCArrayList.size();
    }

    public class HolderActiveProducts extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView categoryTv, productTitle, dateTv, shopNameTv;

        public HolderActiveProducts(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            productTitle = itemView.findViewById(R.id.productTitle);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
        }
    }
}
