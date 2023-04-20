package com.muhamapps.filmcatalogueapp1.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.muhamapps.filmcatalogueapp1.R
import com.muhamapps.filmcatalogueapp1.core.data.Resource
import com.muhamapps.filmcatalogueapp1.core.domain.model.Film
import com.muhamapps.filmcatalogueapp1.core.ui.FilmAdapter
import com.muhamapps.filmcatalogueapp1.core.ui.FilmShareCallback
import com.muhamapps.filmcatalogueapp1.databinding.ActivityHomeBinding
import com.muhamapps.filmcatalogueapp1.detail.DetailFilmActivity
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), FilmShareCallback {

    private val homeViewModel: HomeViewModel by viewModel()

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding
    private var interstitialAd: InterstitialAd? = null
    private var countdownTimer: CountDownTimer? = null
    private var adIsLoading: Boolean = false
    private var failedToLoad = false
    private var timerMilliseconds = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.setLogo(R.drawable.actionbar_icon)

        if (!adIsLoading && interstitialAd == null) {
            adIsLoading = true
            loadAd()
        }

        createTimer(5000)
        countdownTimer?.start()

        getFilmData()
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
//                    binding?.timer?.text = "seconds remaining: ${ millisUntilFinished / 1000 + 1 }"
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
                        createTimer(300000)
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_detail, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                val uriFavorite = Uri.parse("filmcatalogueapp1://favorite")
                startActivity(Intent(Intent.ACTION_VIEW, uriFavorite))
                true
            }
            else -> true
        }
    }

    override fun onShareClick(data: Film) {
        val mimeType = "text/plain"
        @Suppress("DEPRECATION")
        ShareCompat.IntentBuilder
            .from(this)
            .setType(mimeType)
            .setChooserTitle("Bagikan aplikasi ini sekarang.")
            .setText("Lihat Film ${data.title} di themoviedb.org")
            .startChooser()
    }

    private fun getFilmData() {
        val filmAdapter = FilmAdapter(this)
        filmAdapter.onItemClick = { selectedData ->
            val intent = Intent(this, DetailFilmActivity::class.java)
            intent.putExtra(DetailFilmActivity.EXTRA_DATA, selectedData)
            startActivity(intent)
        }

        homeViewModel.film.observe(this) { film ->
            if (film != null) {
                when (film) {
                    is Resource.Loading -> binding?.progressBar?.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding?.progressBar?.visibility = View.GONE
                        filmAdapter.setData(film.data)
                    }
                    is Resource.Error -> {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        }

        with(binding?.rvFilm) {
            this?.layoutManager =
                if (this?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    GridLayoutManager(context,2)
                } else {
                    GridLayoutManager(this?.context,4)
                }
            this?.setHasFixedSize(true)
            this?.adapter = filmAdapter
        }
    }

}