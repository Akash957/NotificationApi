package com.example.notinsnb.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notinsnb.DataModel.UserDataModel
import com.example.notinsnb.R
import com.example.notinsnb.UpdateActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(
    private val context: Context,
    private var userList: List<UserDataModel>,
    private val deleteApi: DeleteApi
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_textView)
        val email: TextView = itemView.findViewById(R.id.email_textView)
        val updateButton: ImageButton = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = userList[position].name
        holder.email.text = userList[position].email

        holder.updateButton.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("name", userList[position].name)
            intent.putExtra("email", userList[position].email)
            intent.putExtra("uid", userList[position].uid)
            intent.putExtra("TokenId", userList[position].tockenId)

            context.startActivity(intent)
        }
        holder.deleteButton.setOnClickListener {
            FirebaseFirestore.getInstance()
                .collection("Users").document(userList[position].uid!!)
                .delete()
                .addOnSuccessListener {
                    deleteApi.deleteNotification(userList[position])
                }
        }
    }

    interface DeleteApi {
        fun deleteNotification(list: UserDataModel)
    }

}