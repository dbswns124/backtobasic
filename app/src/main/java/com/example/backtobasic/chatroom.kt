// 여기에서 prev_chatroom이 없는경우 
// 1. chatroom에 document하나 추가, 해당 document는 message라는 이름의 array(string)가 있음
// 2. 해당 document의 이름을 받아옴
// 3. User/user1/prev_chatroom 에다가 opponent_uid, 위의 chatroom의 쌍을 넣음 

package com.example.backtobasic

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class chatroom : AppCompatActivity() {
    private lateinit var opponent_uid : String
    private lateinit var my_uid : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatroom)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        opponent_uid = intent.getStringExtra("opponent_uid").toString()
        my_uid = intent.getStringExtra("my_uid").toString()
        Log.d("제대로 넘어왔나??",opponent_uid)

        val chatList = mutableListOf<String>()

        val db = Firebase.firestore
        val userCollection = db.collection("User")

        val uid = my_uid // 사용자의 실제 UID 값으로 대체해야 합니다.

        val documentRef = userCollection.document("user1")
        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 문서가 존재하는 경우 prev_chatroom 필드의 값을 읽어옴
                    val prevChatroom = document.get("prev_chatroom") as? Map<String, String>
                    if (prevChatroom != null && prevChatroom.containsKey(opponent_uid)) {
                        val value = prevChatroom[opponent_uid] // key가 opponent_uid인 값

                        Log.d("prev_chatroom이 존재하네요", "Value: $value")
                        // 여기서 그럼 value가 cid가 됨, 이 cid를 기반으로 chatroom을 감
                        // chatroom에서 data를 불러와서 recyclerview에 넣고 봄
                        // chatList에다가 넣음

                        val chatroomRef = db.collection("chatroom").document(value!!)
                        chatroomRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val chatlog = document.get("message") as? List<String>
                                    if (chatlog != null) {
                                        for (message in chatlog) {
                                            Log.d("Chatlog", message)
                                            chatList.add(message.toString())
                                        }

                                        val sendButton = findViewById<Button>(R.id.btn_send)
                                        sendButton.setOnClickListener {
                                            val messageText = findViewById<EditText>(R.id.sending_msg).text.toString()

                                            if (messageText.isNotEmpty()) {
                                                val newMessage = uid + "/" + messageText
                                                // Firestore에 새로운 메시지를 추가합니다.
                                                val chatroomRef = db.collection("chatroom").document(value!!) // value는 chatroom의 CID
                                                chatroomRef.update("message", FieldValue.arrayUnion(newMessage))
                                                    .addOnSuccessListener {
                                                        // 성공적으로 메시지가 추가된 경우
                                                        Log.d("메시지 송신 완료", "Message sent successfully: $messageText")
                                                        chatList.add(newMessage)

                                                        // EditText를 초기화합니다.
                                                        findViewById<EditText>(R.id.sending_msg).setText("")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // 메시지 추가 중 오류가 발생한 경우
                                                        Log.w("메시지 송신 오류 제발요 제발", "Error sending message", e)
                                                    }
                                            } else {
                                                // 메시지가 비어있는 경우
                                                Log.d("sendMessage", "Message is empty")
                                            }
                                        }



                                        val rv = findViewById<RecyclerView>(R.id.chatting_log)

                                        val rv_adapter = chat_adapter(chatList)

                                        rv.adapter = rv_adapter
                                        rv.layoutManager = LinearLayoutManager(this)


                                        val chatroomRef = db.collection("chatroom").document(value!!)
                                        chatroomRef.addSnapshotListener { snapshot, e ->
                                            if (e != null) {
                                                Log.w("Chatlog", "Listen failed", e)
                                                return@addSnapshotListener
                                            }

                                            if (snapshot != null && snapshot.exists()) {
                                                val chatlog = snapshot.get("message") as? List<String>
                                                if (chatlog != null) {
                                                    chatList.clear() // 이전 데이터를 지우고 새로운 데이터로 채웁니다.
                                                    for (message in chatlog) {
                                                        Log.d("Chatlog", message)
                                                        chatList.add(message.toString())
                                                    }
                                                    // RecyclerView에 데이터가 변경되었음을 알립니다.
                                                    rv_adapter.notifyDataSetChanged()
                                                } else {
                                                    Log.d("Chatlog", "Chatlog field is null or not a list")
                                                }
                                            } else {
                                                Log.d("Chatlog", "Current data: null")
                                            }
                                        }


                                    }
                                    else {
                                        Log.d("Chatlog", "Chatlog field is null or not a list")
                                    }
                                }

                                else {
                                    Log.d("Chatlog", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Chatlog", "Error getting document", exception)
                            }

                    } else {
                        // prev_chatroom 필드가 존재하지만 opponent_uid key가 없는 경우
                        // 어후 존나어렵네 여긴 좀 나중에 구현
                        // -> 1. chatroom에 cid를 하나 만듬
                        // 2. 해당 cid를 받아오고,
                        // opponent_uid key에 해당하는 값을 생성하고자 할 때
                        val newValue = opponent_uid // 새로운 값으로 대체하거나 원하는 값으로 설정
                        documentRef.update("prev_chatroom", newValue)
                            .addOnSuccessListener {
                                Log.d("prev_chatroom", "New value created: $newValue")
                            }
                            .addOnFailureListener { e ->
                                Log.w("prev_chatroom", "Error updating document", e)
                            }
                    }
                }
                else {
                    // 문서가 존재하지 않는 경우
                    Log.d("prev_chatroom", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("prev_chatroom", "Error getting document: ", exception)
            }



    }
}