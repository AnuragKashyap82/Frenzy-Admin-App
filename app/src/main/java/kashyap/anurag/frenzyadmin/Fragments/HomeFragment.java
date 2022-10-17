package kashyap.anurag.frenzyadmin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kashyap.anurag.frenzyadmin.AllActiveProductsActivity;
import kashyap.anurag.frenzyadmin.AllSellerVerification;
import kashyap.anurag.frenzyadmin.AllSellerVerifiedAccounts;
import kashyap.anurag.frenzyadmin.NewQCActivity;
import kashyap.anurag.frenzyadmin.R;
import kashyap.anurag.frenzyadmin.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        binding.newQcTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewQCActivity.class));
            }
        });
        binding.allProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AllActiveProductsActivity.class));
            }
        });
        binding.sellerVerificationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AllSellerVerification.class));
            }
        });
        binding.sellerAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AllSellerVerifiedAccounts.class));
            }
        });

        return binding.getRoot();
    }
}