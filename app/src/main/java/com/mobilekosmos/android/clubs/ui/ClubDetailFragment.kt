package com.mobilekosmos.android.clubs.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.mobilekosmos.android.clubs.R
import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.databinding.FragmentClubsBinding


class ClubDetailFragment : Fragment() {

    private val args: ClubDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)
        val clubObject: ClubEntity = args.clubDetail
        displayData(clubObject, rootView)

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }

        return rootView
    }

    // TODO: "Views should trigger coroutines for UI-related logic. For example, ... or formatting a String."
    private fun displayData(objectEntity: ClubEntity, rootView: View) {
        activity?.title = objectEntity.name

        val clubDetailName = objectEntity.country
        rootView.findViewById<TextView>(R.id.club_detail_country).text = clubDetailName

        val clubDescriptionFirst = resources.getQuantityString(
            R.plurals.detail_description,
            objectEntity.value,
            objectEntity.name,
            objectEntity.country,
            objectEntity.value
        )

        rootView.findViewById<TextView>(R.id.club_detail_description_1).text =
            HtmlCompat.fromHtml(clubDescriptionFirst, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val clubDescriptionSecond = resources.getQuantityString(
            R.plurals.detail_description_2,
            objectEntity.european_titles.toInt(),
            objectEntity.name,
            objectEntity.european_titles.toInt()
        )
        rootView.findViewById<TextView>(R.id.club_detail_description_2).text = clubDescriptionSecond

        rootView.findViewById<View>(R.id.club_detail_image).transitionName = objectEntity.name

        downloadTheImage(
            rootView.findViewById(R.id.club_detail_image),
            objectEntity.image
        )
    }

    private fun downloadTheImage(clubDetailImage: ImageView, image: String) {
        // The API uses a URL from https://picsum.photos/ which redirects to a random image.
        // TODO: check coil's caching behavior over time, if we better need to free something manually
        //  after some time or there are parameters we can define.
        clubDetailImage.load(image) {
            crossfade(true)
            error(R.drawable.no_image)
            listener(
                onStart = {
                    // TODO: show some progress view. Maybe using placeholder instead
                },
                onSuccess = { request: ImageRequest, successResult: SuccessResult ->
                    startPostponedEnterTransition()
                },
                onError = { request: ImageRequest, errorResult: ErrorResult ->
                    startPostponedEnterTransition()
                    activity?.let {
                        Toast.makeText(
                            it,
                            getString(R.string.error_loading),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

        }
    }
}
