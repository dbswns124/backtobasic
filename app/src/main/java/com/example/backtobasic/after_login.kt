package com.example.backtobasic

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class after_login : AppCompatActivity() {

    private lateinit var my_uid : String

    override fun onCreate(savedInstanceState: Bundle?) {

        my_uid = intent.getStringExtra("uid").toString()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_after_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userList = mutableListOf<String>()

        val db = Firebase.firestore
        db.collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userData = document.getString("uid")
                    if(userData != null){
                        if(userData != my_uid){
                            userList.add(userData)
                        }
                    }
                    else{
                        Log.d("이게 왜 null이냐...","0")
                    }
                }



                val rv = findViewById<RecyclerView>(R.id.rv_users)

                val rv_adapter = user_Adapter(userList)

                rv.adapter = rv_adapter
                rv.layoutManager = LinearLayoutManager(this)

                rv_adapter.itemclick = object : user_Adapter.Itemclick {
                    override fun onclick(view: View, position: Int) {
                        Toast.makeText(baseContext, userList[position],Toast.LENGTH_LONG).show()
                        val intent = Intent(baseContext, chatroom::class.java)
                        intent.putExtra("my_uid",my_uid)
                        intent.putExtra("opponent_uid",userList[position])

                        startActivity(intent)
                        finish()
                    }


                }
                // 리스트에 데이터를 담은 후에 원하는 작업을 수행
            }

            // uid, 다 가져오는것 성공
            // class를 recyclerview에 넣어야 함





            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu,menu)
            return super.onCreateOptionsMenu(menu)
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {
            if(item.itemId == R.id.logout){

            }
            return super.onOptionsItemSelected(item)
        }

    }
}