package com.optic.cinema.activities.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.optic.cinema.BuildConfig
import com.optic.cinema.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logoAlpha = ObjectAnimator.ofFloat(binding.logoIcon, "alpha", 0f, 1f)
        val logoScaleX = ObjectAnimator.ofFloat(binding.logoIcon, "scaleX", 0.5f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(binding.logoIcon, "scaleY", 0.5f, 1f)
        
        val logoAnimSet = AnimatorSet()
        logoAnimSet.playTogether(logoAlpha, logoScaleX, logoScaleY)
        logoAnimSet.duration = 1000
        logoAnimSet.interpolator = OvershootInterpolator(1.2f)

        val textAlpha = ObjectAnimator.ofFloat(binding.logoText, "alpha", 0f, 1f)
        val textTranslateY = ObjectAnimator.ofFloat(binding.logoText, "translationY", 50f, 0f)
        
        val textAnimSet = AnimatorSet()
        textAnimSet.playTogether(textAlpha, textTranslateY)
        textAnimSet.duration = 800
        textAnimSet.interpolator = AccelerateDecelerateInterpolator()

        val progressAlpha = ObjectAnimator.ofFloat(binding.loadingProgress, "alpha", 0f, 1f)
        progressAlpha.duration = 500

        val masterSet = AnimatorSet()
        masterSet.playSequentially(logoAnimSet, textAnimSet, progressAlpha)
        masterSet.start()

        lifecycleScope.launch {
            delay(2800) // Beautiful delay

            val isTv = BuildConfig.APP_LAYOUT == "tv" || 
                (BuildConfig.APP_LAYOUT != "mobile" && packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK))

            val targetActivity = if (isTv) MainTvActivity::class.java else MainMobileActivity::class.java
            
            startActivity(Intent(this@SplashActivity, targetActivity))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
