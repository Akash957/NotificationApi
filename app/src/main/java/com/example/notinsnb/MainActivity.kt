package com.example.notinsnb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationtest.NotificationApi
import com.example.notinsnb.Adapter.UserAdapter
import com.example.notinsnb.DataModel.Data
import com.example.notinsnb.DataModel.DeleteNotificationDataModal
import com.example.notinsnb.DataModel.Notification
import com.example.notinsnb.DataModel.UserDataModel
import com.example.notinsnb.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(), UserAdapter.DeleteApi {
    private lateinit var binding: ActivityMainBinding
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val token = it
            println(it)
        }
        getData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addFlotingButton.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun getData() {
        db.collection("Users").get()
            .addOnSuccessListener { document ->
                val list = ArrayList<UserDataModel>()
                for (a in document) {
                    val data = a.toObject(UserDataModel::class.java)
                    list.add(data)
                }

                val adapter = UserAdapter(this, list, this)
                binding.userListRecycleView.layoutManager = LinearLayoutManager(this)
                binding.userListRecycleView.adapter = adapter
            }.addOnFailureListener {
                Toast.makeText(this, "get Data feel", Toast.LENGTH_SHORT).show()
            }
    }
    override fun deleteNotification(list: UserDataModel) {
        sendNotification(list)
        getData()
    }

    @SuppressLint("CheckResult")
    fun sendNotification(user: UserDataModel) {
        val postData = DeleteNotificationDataModal(
            Data("23", "mnnk"),
            Notification("notify_test", "${user.name} is Deleted", true, "Deleted"),
            arrayListOf(user.tockenId.toString())
        )

        val headerMap = HashMap<String, String>()
        headerMap["Authorization"] =
            "key=AAAA8YG8Xpo:APA91bFilH5qO_N43OgdPmyS_PT0qy4GQcX0oxrsrSR-4AM0omXajOFgtCKNJ7PmAgB7tvgoTk97ZhjCx_gYw0JVs4qjOfAxaiZdhtCFGtS-mqrQf4602t4xcApRdFjRAqVAHIZvZHnI"

        NotificationApi.createRetrofit().deleteNotification(headerMap, postData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result ->
                    Toast.makeText(this, "Notification Send", Toast.LENGTH_SHORT).show()
                },
                { error ->
                    Log.e("RxJava", "An error occurred: ${error.message}", error)
                    Toast.makeText(this, "An error occurred: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            )
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

}