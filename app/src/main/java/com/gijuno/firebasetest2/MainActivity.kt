package com.gijuno.firebasetest2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    //private const val TAG = "GoogleA  ctivity"
    private val RC_SIGN_IN = 99
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //btn_googleSignIn.setOnClickListener (this) // 구글 로그인 버튼
        googleLogin_btn.setOnClickListener {
            signIn()
        }


        //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()


        var selected : String = "unselected"
        //스피너
        val mclass = arrayOf("1-1","1-2","1-3","1-4","1-5","1-6")

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mclass)

        select_class_spinner.adapter = myAdapter

        select_class_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selected = mclass[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selected = "unselected"
            }
        }

        fun unsubscribe() {
            for(i in mclass) {
                Firebase.messaging.unsubscribeFromTopic(i)
            }
        }

        select_class_btn.setOnClickListener {
            unsubscribe()
            if (selected == "unselected")
                Toast.makeText(this, "Select Your Class", Toast.LENGTH_SHORT).show()
            else {
                Firebase.messaging.subscribeToTopic(selected)
                Toast.makeText(this, "Selected $selected", Toast.LENGTH_SHORT).show()
            }
        }


    }



    // onStart. 유저가 앱에 이미 구글 로그인을 했는지 확인
    public override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if(account!==null){ // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
//            gotoMainActivity(firebaseAuth.currentUser)
//        }
    } //onStart End

    // onActivityResult
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("LoginActivity", "Google sign in failed", e)
            }
        }
    } // onActivityResult End

    // firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        Snackbar.make(mainLayout, "로그인 중 ...", Snackbar.LENGTH_SHORT).show()

        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
                    gotoMainActivity(firebaseAuth?.currentUser)
                } else {
                    Log.d("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                    Snackbar.make(mainLayout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }// firebaseAuthWithGoogle END


    // gotoMainActivity
    private fun gotoMainActivity(user: FirebaseUser?) {
        if (user != null) { // MainActivity 로 이동
            startActivity(Intent(this, HiActivity::class.java))
            finish()
        }
    } // gotoMainActivity End

    // signIn
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // signIn End

}