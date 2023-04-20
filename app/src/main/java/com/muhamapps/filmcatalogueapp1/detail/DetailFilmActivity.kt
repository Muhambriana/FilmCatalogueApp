package com.muhamapps.filmcatalogueapp1.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.muhamapps.filmcatalogueapp1.R
import com.muhamapps.filmcatalogueapp1.core.domain.model.Film
import com.muhamapps.filmcatalogueapp1.core.utils.NetworkInfo.IMAGE_URL
import com.muhamapps.filmcatalogueapp1.databinding.ActivityDetailFilmBinding

import org.koin.android.viewmodel.ext.android.viewModel

class DetailFilmActivity : AppCompatActivity() {

    private val detailFilmViewModel: DetailFilmViewModel by viewModel()
    private lateinit var binding: ActivityDetailFilmBinding

    lateinit var adView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailFilm = intent.getParcelableExtra<Film>(EXTRA_DATA)
        showDetailFilm(detailFilm)
        showBannerAd()
    }

    private fun showBannerAd() {
        adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                val toastMessage: String = "ad fail to load +"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                val toastMessage: String = "ad loaded"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onAdOpened() {
                super.onAdOpened()
                val toastMessage: String = "ad is open"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onAdClicked() {
                super.onAdClicked()
                val toastMessage: String = "ad is clicked"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                val toastMessage: String = "ad is closed"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onAdImpression() {
                super.onAdImpression()
                val toastMessage: String = "ad impression"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked()
                val toastMessage: String = "ad left application"
//                Toast.makeText(applicationContext, toastMessage.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showDetailFilm(detailFilm: Film?) {
        detailFilm?.let {
            binding.progressBar.visibility = View.GONE
            supportActionBar?.title = detailFilm.title
            binding.rate.text = detailFilm.rating
            binding.tvDetailDescription.text = detailFilm.description
            Glide.with(this@DetailFilmActivity)
                .load(IMAGE_URL + detailFilm.poster)
                .into(binding.ivDetailImage)

            var statusFavorite = detailFilm.isFavorite
            setStatusFavorite(statusFavorite)
            binding.fab.setOnClickListener {
                statusFavorite = !statusFavorite
                detailFilmViewModel.setFavoriteFilm(detailFilm, statusFavorite)
                setStatusFavorite(statusFavorite)
            }
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white))
        } else {
            binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_not_favorite_white))
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}
