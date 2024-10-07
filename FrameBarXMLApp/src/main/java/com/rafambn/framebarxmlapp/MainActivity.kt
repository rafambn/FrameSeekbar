package com.rafambn.framebarxmlapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafambn.framebar.FrameBar

class MainActivity : AppCompatActivity() {

    private lateinit var zoom: FrameBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoom = findViewById(R.id.aaaa)
        zoom.setOffset(50F)
    }
}