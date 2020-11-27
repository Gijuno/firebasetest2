package com.gijuno.firebasetest2

import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_upload_.*
import java.util.*

class UserData {
    var userEmailID // email 주소에서 @ 이전까지의 값.
            : String? = null
    var fcmToken: String? = null
}

class Upload_Activity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    //fcm from app
    private var firebase2Auth = FirebaseAuth.getInstance();
    private var user: FirebaseUser? = firebase2Auth.getCurrentUser()

    var email = user?.email

    var userEmail = email?.slice(0 until email!!.indexOf("@"))

    var fcmToken = FirebaseInstanceId.getInstance().getToken()

    var fbAuth : FirebaseAuth? = null
    var fbFirestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_)


        val nowtime = (Calendar.MONTH).toString()+"-"+(Calendar.DAY_OF_MONTH).toString()+"-"+(Calendar.HOUR_OF_DAY).toString()+"-"+(Calendar.MINUTE).toString()



        sendData_btn.setOnClickListener {
            var notice_contents = sendData_edtx.text.toString()
            val notice = hashMapOf(
                "contents" to notice_contents
            )

            fun hi() = db.collection("notice").document("notice")
                .set(notice)
                .addOnSuccessListener { Log.d("asdf", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.d("asdf", "Error writing document", e) }
            hi()

            val userToken: String = userEmail+fcmToken
            val token = hashMapOf(
                "userToken" to userToken
            )


            fbFirestore?.collection("users")?.document(fbAuth?.uid.toString())?.set(userToken)


            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

        }
    }
}
