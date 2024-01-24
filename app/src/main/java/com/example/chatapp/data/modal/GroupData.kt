package com.example.chatapp.data.modal

data class GroupData(
    val groupId: String? = null,
    val groupUserList: List<GroupList> = emptyList(),
    val groupName: String? = null

)
