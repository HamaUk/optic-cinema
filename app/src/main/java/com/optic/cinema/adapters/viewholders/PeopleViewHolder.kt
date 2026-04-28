package com.optic.cinema.adapters.viewholders

import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.optic.cinema.R
import com.optic.cinema.databinding.ItemPeopleMobileBinding
import com.optic.cinema.databinding.ItemPeopleTvBinding
import com.optic.cinema.fragments.movie.MovieMobileFragment
import com.optic.cinema.fragments.movie.MovieMobileFragmentDirections
import com.optic.cinema.fragments.movie.MovieTvFragment
import com.optic.cinema.fragments.movie.MovieTvFragmentDirections
import com.optic.cinema.fragments.tv_show.TvShowMobileFragment
import com.optic.cinema.fragments.tv_show.TvShowMobileFragmentDirections
import com.optic.cinema.fragments.tv_show.TvShowTvFragment
import com.optic.cinema.fragments.tv_show.TvShowTvFragmentDirections
import com.optic.cinema.models.People
import com.optic.cinema.utils.getCurrentFragment
import com.optic.cinema.utils.toActivity

class PeopleViewHolder(
    private val _binding: ViewBinding
) : RecyclerView.ViewHolder(
    _binding.root
) {

    private val context = itemView.context
    private lateinit var people: People

    fun bind(people: People) {
        this.people = people

        when (_binding) {
            is ItemPeopleMobileBinding -> displayMobileItem(_binding)
            is ItemPeopleTvBinding -> displayTvItem(_binding)
        }
    }


    private fun displayMobileItem(binding: ItemPeopleMobileBinding) {
        binding.root.apply {
            setOnClickListener {
                when (context.toActivity()?.getCurrentFragment()) {
                    is MovieMobileFragment -> findNavController().navigate(
                        MovieMobileFragmentDirections.actionMovieToPeople(
                            id = people.id,
                            name = people.name,
                            image = people.image,
                        )
                    )
                    is TvShowMobileFragment -> findNavController().navigate(
                        TvShowMobileFragmentDirections.actionTvShowToPeople(
                            id = people.id,
                            name = people.name,
                            image = people.image,
                        )
                    )
                }
            }
        }

        binding.ivPeopleImage.apply {
            clipToOutline = true
            Glide.with(context)
                .load(people.image)
                .placeholder(R.drawable.ic_person_placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
        }

        binding.tvPeopleName.text = people.name
    }

    private fun displayTvItem(binding: ItemPeopleTvBinding) {
        binding.root.apply {
            setOnClickListener {
                when (context.toActivity()?.getCurrentFragment()) {
                    is MovieTvFragment -> findNavController().navigate(
                        MovieTvFragmentDirections.actionMovieToPeople(
                            id = people.id,
                            name = people.name,
                            image = people.image,
                        )
                    )
                    is TvShowTvFragment -> findNavController().navigate(
                        TvShowTvFragmentDirections.actionTvShowToPeople(
                            id = people.id,
                            name = people.name,
                            image = people.image,
                        )
                    )
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                // Applichiamo l'animazione solo all'immagine, non a tutto il root (che include il testo)
                val animation = when {
                    hasFocus -> AnimationUtils.loadAnimation(context, R.anim.zoom_in)
                    else -> AnimationUtils.loadAnimation(context, R.anim.zoom_out)
                }
                binding.ivPeopleImage.startAnimation(animation)
                animation.fillAfter = true
            }
        }

        binding.ivPeopleImage.apply {
            clipToOutline = true
            Glide.with(context)
                .load(people.image)
                .placeholder(R.drawable.ic_person_placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
        }

        binding.tvPeopleName.text = people.name
    }
}
