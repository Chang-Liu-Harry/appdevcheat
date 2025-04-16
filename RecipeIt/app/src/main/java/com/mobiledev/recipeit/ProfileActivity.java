package com.mobiledev.recipeit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobiledev.recipeit.Helpers.UserSessionManager;
import com.mobiledev.recipeit.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize ViewBinding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UserSessionManager
        sessionManager = new UserSessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            return;
        }

        // Set up bottom navigation
        binding.bottomNav.setSelectedItemId(R.id.nav_profile);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                finish(); // Go back to MainActivity
                return true;
            } else if (id == R.id.nav_favorites) {
                Toast.makeText(this, "Favorites selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // Already on profile
            }

            return false;
        });

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Display user information
        if (currentUser != null) {
            binding.textName.setText(currentUser.getDisplayName());
            binding.textEmail.setText(currentUser.getEmail());

            // Set profile initial letter
            String initial = "";
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                initial = currentUser.getDisplayName().substring(0, 1).toUpperCase();
            } else if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                initial = currentUser.getEmail().substring(0, 1).toUpperCase();
            }
            binding.textInitial.setText(initial);
        }

        // Set up logout button
        binding.buttonLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.logoutUser();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}