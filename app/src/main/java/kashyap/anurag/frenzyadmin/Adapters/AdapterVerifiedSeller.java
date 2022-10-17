package kashyap.anurag.frenzyadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzyadmin.Models.ModelVerifiedSeller;
import kashyap.anurag.frenzyadmin.R;
import kashyap.anurag.frenzyadmin.VerifySellerAccountActivity;


public class AdapterVerifiedSeller extends RecyclerView.Adapter<AdapterVerifiedSeller.HolderVerifiedSeller> {

    private Context context;
    private ArrayList<ModelVerifiedSeller> verifiedSellerArrayList;

    public AdapterVerifiedSeller(Context context, ArrayList<ModelVerifiedSeller> verifiedSellerArrayList) {
        this.context = context;
        this.verifiedSellerArrayList = verifiedSellerArrayList;
    }

    @NonNull
    @Override
    public HolderVerifiedSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_seller_account_verification, parent, false);
        return new HolderVerifiedSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVerifiedSeller holder, int position) {
        final ModelVerifiedSeller modelVerifiedSeller = verifiedSellerArrayList.get(position);
        String shopId = modelVerifiedSeller.getShopId();
        String shopName = modelVerifiedSeller.getShopName();
        String sellerName = modelVerifiedSeller.getSellerName();

        holder.shopName.setText(shopName);
        holder.sellerName.setText("Owned By: "+sellerName);
        holder.addressTv.setText("address!!!to be integrated");
        holder.notVerified.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.successGreen)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VerifySellerAccountActivity.class);
                intent.putExtra("shopId", ""+shopId);
                intent.putExtra("verified", "VERIFIED");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return verifiedSellerArrayList.size();
    }

    public class HolderVerifiedSeller extends RecyclerView.ViewHolder {

        private TextView shopName, sellerName, addressTv;
        private ImageView notVerified;

        public HolderVerifiedSeller(@NonNull View itemView) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shopName);
            sellerName = itemView.findViewById(R.id.sellerName);
            addressTv = itemView.findViewById(R.id.addressTv);
            notVerified = itemView.findViewById(R.id.notVerified);
        }
    }
}
