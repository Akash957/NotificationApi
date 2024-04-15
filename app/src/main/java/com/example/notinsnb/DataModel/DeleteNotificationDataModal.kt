package com.example.notinsnb.DataModel

data class DeleteNotificationDataModal(
    val data: Data? = null,
    val notification: Notification?=null,
    val registration_ids: List<String>? = null
)
