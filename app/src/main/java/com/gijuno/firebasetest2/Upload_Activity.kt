package com.gijuno.firebasetest2

import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_upload_.*
import java.util.*


class Upload_Activity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_)

        val nowtime = (Calendar.MONTH).toString()+"-"+(Calendar.DAY_OF_MONTH).toString()+"-"+(Calendar.HOUR_OF_DAY).toString()+"-"+(Calendar.MINUTE).toString()



        sendData_btn.setOnClickListener {Log.d("asdf","BUTTON CLICKED")
            var notice_contents = sendData_edtx.text.toString()
            Log.d("asdf","TEXT SETTED")
            Log.d("asdf",notice_contents)
            val notice = hashMapOf(
                "contents" to notice_contents
            )
            Log.d("asdf","CONTENTS = NOTICE CONTENTS")

            fun hi() = db.collection("notice").document("notice")
                .set(notice)
                .addOnSuccessListener { Log.d("asdf", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.d("asdf", "Error writing document", e) }
            hi()
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

        }
    }
}
