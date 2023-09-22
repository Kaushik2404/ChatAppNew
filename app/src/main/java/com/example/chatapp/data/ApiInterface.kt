package com.example.chatapp.data
import com.example.chatapp.data.modal.NotificationModel
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiInterface {

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }

    @Headers(
        "Authorization: key= AAAAA4lyKsw:APA91bEYUSl28FvZLxzOH8ix0zkU_VAMqlZ8BEMaYjh5_5xKbmGZa5gh216KYfgqbUT8pjiZT53T7IlIexFlbK00p7b60h8fNb242783kygaqJVDljjOiD6bD6dFHcwJBLlltPvRn1oU"

        ,
        "Content-Type:application/json"
    )

    @POST("fcm/send")
    suspend fun sendNotification( @Body notificationModel: NotificationModel): Response<ResponseBody>



}
