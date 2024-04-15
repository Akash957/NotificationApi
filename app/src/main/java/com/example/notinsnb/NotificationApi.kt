package com.example.notificationtest

import com.example.notinsnb.DataModel.DeleteNotificationDataModal
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface NotificationApi {

    @POST("send")
    fun deleteNotification(
        @HeaderMap authorization: HashMap<String, String>, @Body post: DeleteNotificationDataModal): Observable<Any>

    companion object Factory {
        fun createRetrofit(): NotificationApi {
            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .build()
                .create(NotificationApi::class.java)
        }
    }
}
