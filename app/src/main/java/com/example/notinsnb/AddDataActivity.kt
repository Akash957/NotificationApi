package com.example.notinsnb

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.notificationtest.NotificationApi
import com.example.notinsnb.DataModel.Data
import com.example.notinsnb.DataModel.DeleteNotificationDataModal
import com.example.notinsnb.DataModel.Notification
import com.example.notinsnb.DataModel.UserDataModel
import com.example.notinsnb.databinding.ActivityAddDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                Log.d("tokenId", it)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10
            )
        }

        binding.registerButton.setOnClickListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                registerData(it)
            }

        }
    }

    private fun registerData(token: String) {
        val name = binding.userNameEditeText.text.toString()
        val email = binding.userEmailEditeText.text.toString()

        val uid = UUID.randomUUID().toString()

        val map = UserDataModel(name, email, uid, token)

        db.collection("Users").document(uid).set(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Data add success", Toast.LENGTH_SHORT).show()
                sendNotification(name, token)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data add fell", Toast.LENGTH_SHORT).show()
            }

    }

    @SuppressLint("CheckResult")
    fun sendNotification(name: String, token: String) {

        val addDataNotification = DeleteNotificationDataModal(
            Data("23", "mnnk"),
            Notification("notify_test", "${name} is added", true, "add"),
            arrayListOf(token)
        )

        val headerMap = HashMap<String, String>()
        headerMap["Authorization"] =
            "key=AAAA8YG8Xpo:APA91bFilH5qO_N43OgdPmyS_PT0qy4GQcX0oxrsrSR-4AM0omXajOFgtCKNJ7PmAgB7tvgoTk97ZhjCx_gYw0JVs4qjOfAxaiZdhtCFGtS-mqrQf4602t4xcApRdFjRAqVAHIZvZHnI"

        NotificationApi.createRetrofit().deleteNotification(headerMap, addDataNotification)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result ->
                    Toast.makeText(this, "Notification Send", Toast.LENGTH_SHORT).show()
                    finish()
                },
                { error ->
                    // Handle error
                    Log.e("RxJava", "An error occurred: ${error.message}", error)
                    Toast.makeText(this, "An error occurred: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            )

    }
}