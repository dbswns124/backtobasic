package com.example.backtobasic

data class User_data(
    val uid: String = "",
    val name: String = "",
    val prev_chatroom: Map<String, String> = emptyMap()
)
