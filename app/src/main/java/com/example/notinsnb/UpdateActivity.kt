package com.example.notinsnb

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notificationtest.NotificationApi
import com.example.notinsnb.DataModel.Data
import com.example.notinsnb.DataModel.DeleteNotificationDataModal
import com.example.notinsnb.DataModel.Notification
import com.example.notinsnb.DataModel.UserDataModel
import com.example.notinsnb.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private var uid: String = ""
    private var tokenId: String = ""
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        uid = intent.getStringExtra("uid").toString()
        tokenId = intent.getStringExtra("TokenId").toString()

        binding.userNameUpdateEditeText.setText(name)
        binding.userEmailUpdateEditeText.setText(email)

        binding.updateButton.setOnClickListener {
            updateData()
        }


    }

    private fun updateData() {
        val updateName = binding.userNameUpdateEditeText.text.toString()
        val updateEmail = binding.userEmailUpdateEditeText.text.toString()

        val updateData = hashMapOf(
            "name" to updateName,
            "email" to updateEmail,
            "uid" to uid
        )

        db.collection("Users").document(uid).update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Update data success", Toast.LENGTH_SHORT).show()
                sendUpdateMessage(tokenId, updateName, updateEmail)
                finish()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update fell", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    @SuppressLint("CheckResult")
    fun sendUpdateMessage(token: String, name: String, email: String) {
        val updateDataNotification = DeleteNotificationDataModal(
            Data(name, "Update"), Notification("notify_test", "Update", true, name),
            arrayListOf(token)
        )

        val headerMap = HashMap<String, String>()
        headerMap["Authorization"] =
            "key=AAAA8YG8Xpo:APA91bFilH5qO_N43OgdPmyS_PT0qy4GQcX0oxrsrSR-4AM0omXajOFgtCKNJ7PmAgB7tvgoTk97ZhjCx_gYw0JVs4qjOfAxaiZdhtCFGtS-mqrQf4602t4xcApRdFjRAqVAHIZvZHnI"
        NotificationApi.createRetrofit().deleteNotification(headerMap, updateDataNotification)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show()
            },
                { error ->
                    Log.e("RxJava", "An error occurred: ${error.message}", error)
                    Toast.makeText(this, "An error occurred: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                })


    }

}