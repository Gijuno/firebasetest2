package com.gijuno.firebasetest2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_hi.*
import java.util.*


class HiActivity : AppCompatActivity() {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    //22
    var fbAuth : FirebaseAuth? = null
    var fbFirestore : FirebaseFirestore? = null

    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi)

        val NOTIFICATION_ID = 1001;
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_HIGH,
            false, getString(R.string.app_name), "App notification channel") // 1

        val channelId = "$packageName-${getString(R.string.app_name)}" // 2
        val title = "Android Developer"
        val content = "Notifications in Android holy moly hohohohohohohohohohohohohohohohohohohohohohohohoho"

        val intent = Intent(baseContext, this::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(baseContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3

        val builder = NotificationCompat.Builder(this, channelId)  // 4
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_light)    // 5
        builder.setContentTitle(title)    // 6
        builder.setContentText(content)    // 7
        builder.priority = NotificationCompat.PRIORITY_HIGH
        //builder.setAutoCancel(true)   // 9
        builder.setContentIntent(pendingIntent)   // 10

        val notificationManager = NotificationManagerCompat.from(this)
           // 11

        test_btn.setOnClickListener {
            Handler().postDelayed({notificationManager.notify(NOTIFICATION_ID, builder.build())}, 3000)
              }

        upload_btn.setOnClickListener {
            startActivity(Intent(this, Upload_Activity::class.java))
        }

        showdata_btn.setOnClickListener {
            startActivity(Intent(this, GetdataActivity::class.java))
        }

        logout_btn2.setOnClickListener {signOut()}

        //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        //22
        fbAuth = FirebaseAuth.getInstance()
        fbFirestore = FirebaseFirestore.getInstance()

        //DB에 유저 정보 넘김
        if(true)
        {
            var userInfo = ModelFriends()

            userInfo.uid = fbAuth?.uid
            userInfo.userId = fbAuth?.currentUser?.email
            fbFirestore?.collection("users")?.document(fbAuth?.uid.toString())?.set(userInfo)
        }
    }

    private fun signOut() { // 로그아웃
        // Firebase sign out
        firebaseAuth.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)s
        }
        startActivity(Intent(this, MainActivity::class.java))
    }
}