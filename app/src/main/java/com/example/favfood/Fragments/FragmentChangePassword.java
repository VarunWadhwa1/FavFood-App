package com.example.favfood.Fragments;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.favfood.R;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentChangePassword extends Fragment {


    public void Dialog(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        View layout  = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_custom_dialog,null,false);
//        View layout = getLayoutInflater().inflate(R.layout.activity_custom_dialog,null,false);
        EditText editText = layout.findViewById(R.id.editTextEmail);
        Button reset = layout.findViewById(R.id.reset);
        Button cancel = layout.findViewById(R.id.cancel);
        alert.setView(layout);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        reset.setOnClickListener(v1 -> {
            String forgotEmail = editText.getText().toString();
//            Toast.makeText(getContext(), forgotEmail, Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(forgotEmail)) {
                Toast.makeText(requireContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(forgotEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Reset mail is sent to your Email address", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                }
            });
            
            alertDialog.dismiss();
        });
        cancel.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
}