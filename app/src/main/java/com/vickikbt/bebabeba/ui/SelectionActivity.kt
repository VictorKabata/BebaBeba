package com.vickikbt.bebabeba.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vickikbt.bebabeba.R
import kotlinx.android.synthetic.main.activity_selection.*

class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        btn_select_customer.setOnClickListener {
            startActivity(Intent(this, RiderLoginActivity::class.java))
            finish()
        }

    }
}
