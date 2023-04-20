package com.muhamapps.filmcatalogueapp1.home

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.muhamapps.filmcatalogueapp1.databinding.ActivityHomeBinding

class TestAdActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding

    private var interstitialAd: InterstitialAd? = null
    private var countdownTimer: CountDownTimer? = null
    private var adIsLoading: Boolean = false
    private var failedToLoad: Boolean = false
    private var timerMilliseconds = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        startAct()

        createTimer(5000)
        countdownTimer?.start()
    }

    private fun startAct() {
        if (!adIsLoading && interstitialAd == null) {
            adIsLoading = true
            loadAd()
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3432330311757220/3260605421",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError?.message)
                    interstitialAd = null
                    adIsLoading = false
                    failedToLoad = true
                    loadAd()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    if (failedToLoad) {
                        failedToLoad = false
                        createTimer(timerMilliseconds)
                        countdownTimer?.start()
                    }
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    Log.i(ContentValues.TAG, "Ad was loaded.")
                }
            }
        )
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private fun createTimer(millisecond: Long) {

        countdownTimer?.cancel()

        countdownTimer =
            object : CountDownTimer(millisecond, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    timerMilliseconds = millisUntilFinished
                    binding?.timer?.text = "seconds remaining: ${ millisUntilFinished / 1000 + 1 }"
                }

                override fun onFinish() {
                    showInterstitial()
                }
            }
    }

    // Show the ad if it's ready.
    private fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(ContentValues.TAG, "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                        loadAd()
                        createTimer(5000)
                        countdownTimer?.start()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(ContentValues.TAG, "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        interstitialAd = null
                        // don't show the ad a second time.
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }
            interstitialAd?.show(this)
        } else {
            Log.i(ContentValues.TAG,"Ad wasn't loaded.")
        }
    }
}