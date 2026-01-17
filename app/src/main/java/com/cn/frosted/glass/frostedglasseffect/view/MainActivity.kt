package com.cn.frosted.glass.frostedglasseffect.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply window blur for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                // Set window flags for blur effect
                window.setFlags(
                    android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                
                // Enable window blur effect with radius 50
                window.setBackgroundBlurRadius(50)
                
                // Ensure window is transparent
                window.decorView.setBackgroundColor(0x00000000) // Fully transparent
                
                // Set window transparency flag
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } catch (e: Exception) {
                // Handle any exceptions
                android.util.Log.e("MainActivity", "Error applying blur: ${e.message}")
            }
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Apply window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        
        // Try applying blur again in onResume to ensure it takes effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                window.setBackgroundBlurRadius(50)
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error applying blur in onResume: ${e.message}")
            }
        }
    }
}