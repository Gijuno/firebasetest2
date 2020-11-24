package com.gijuno.firebasetest2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_getdata.*

class GetdataActivity : AppCompatActivity() {

    var firestore : FirebaseFirestore? = null

    data class Content(var contents : String? = null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getdata)


        firestore = FirebaseFirestore.getInstance()

//        var noti_contents = firestore?.collection("notice")?.document("notice")?.get().toString()

        firestore?.collection("notice")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            for (snapshot in querySnapshot!!.documents) {
                var item = snapshot.toObject(Content::class.java)?.contents

                show_data_tv.setText(item)
            }
        }


    }


}