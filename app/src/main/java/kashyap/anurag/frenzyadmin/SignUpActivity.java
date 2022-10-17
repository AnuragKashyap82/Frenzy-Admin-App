package kashyap.anurag.frenzyadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzyadmin.databinding.ActivitySignUpBinding;


public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private String email, name, password, cPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.alreadyAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        name = binding.yourNameEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        cPassword = binding.cPasswordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(SignUpActivity.this, "Enter Valid Email!!!", Toast.LENGTH_SHORT).show();
        }else if (name.isEmpty()){
            Toast.makeText(SignUpActivity.this, "Enter Your Name!!!!", Toast.LENGTH_SHORT).show();
        }else if (password.length()<8){
            Toast.makeText(SignUpActivity.this, "Enter your Password of length must be 8 characters!!!!", Toast.LENGTH_SHORT).show();
        }else if (!cPassword.equals(password)){
            Toast.makeText(SignUpActivity.this, "Password doesn't matches!!!!", Toast.LENGTH_SHORT).show();
        }else {

            createAccountWithEmailAndPassword(email, password);
        }
    }
    private void createAccountWithEmailAndPassword(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignUpActivity.this, "Account Created Successfully!!!", Toast.LENGTH_SHORT).show();
                        saveUserDataToDb();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToDb() {

        long timestamp = System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getCurrentUser().getUid());
        hashMap.put("email", ""+email);
        hashMap.put("name", ""+name);
        hashMap.put("phoneNo", "");
        hashMap.put("profileImage", "");
        hashMap.put("userType", "admin");
        hashMap.put("timestamp", ""+timestamp);


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Admin").document(firebaseAuth.getUid());
        documentReference.set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "data Added to database!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finishAffinity();

                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "Failed adding data to database!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}