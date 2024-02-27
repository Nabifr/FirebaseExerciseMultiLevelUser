package com.android.firebaseexercisemultileveluser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.autofill.UserData
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.firebaseexercisemultileveluser.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var context : Context
    private lateinit var pref:preferences

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        pref = preferences(context)

        binding.btnLogin.setOnClickListener(View.OnClickListener {
            val username: String = binding.etUsername.text.toString()
            val password: String = binding.etPassword.text.toString()
            if (username.isEmpty()){
                binding.etUsername.error = "Data tidak boleh kosong"
                binding.etUsername.requestFocus()
            } else if (password.isEmpty()){
                binding.etPassword.error = "Data tidak boleh kosong"
                binding.etPassword.requestFocus()
            } else{
                val query: Query = database.child("users").orderByChild("phone").equalTo(username)
                query.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                        for(item in snapshot.children) {
                            val user = item.getValue<userData>()
                            if (user != null) {
                                if (user.password.equals(password)) {
                                    pref.prefStatus = true
                                    pref.prefLevel = user.level
                                    var intent: Intent? = null
                                    intent = if (user.level.equals("admin")){
                                                Intent(context, AdminActivity::class.java)
                                                } else {Intent(context, UserActivity::class.java)
                                                       }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(context, "Kata sandi belum sesuai", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }

                        }else{
                            Toast.makeText(context,"Username belum terdaftar", Toast.LENGTH_LONG)
                                .show()
                            }
                        }


                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText( context,error.message, Toast.LENGTH_LONG).show()
                    }

                })
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (pref.prefStatus){
            var intent: Intent? = null
            intent = if (pref.prefLevel.equals("admin")){
                Intent(context, AdminActivity::class.java)
            } else {
                Intent(context, UserActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}