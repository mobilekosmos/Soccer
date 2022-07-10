package com.mobilekosmos.android.clubs.ui

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.mobilekosmos.android.clubs.R
import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.mobilekosmos.android.clubs.databinding.FragmentClubsBinding
import com.mobilekosmos.android.clubs.ui.model.ClubsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ClubsListFragment : Fragment(), ClubsListAdapter.OnClubClickListener {

    private lateinit var clubsListAdapter: ClubsListAdapter
    private val viewModel: ClubsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Use data binding (not view binding!) to easily switch views visibility before/while/after list loading.
        val binding: FragmentClubsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_clubs,
            container,
            false
        )

        // Set the lifecycleOwner so DataBinding can observe LiveData.
        binding.lifecycleOwner = viewLifecycleOwner

        // Initializes the viewModel variable in the xml layout file.
        binding.viewModel = viewModel

        clubsListAdapter = ClubsListAdapter(this)

        // We use view binding here just because we use data binding in this class so it comes handy.
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clubsListAdapter
            binding.root.context.let {
                val dividerItemDecoration = MaterialDividerItemDecoration(
                    it,
                    MaterialDividerItemDecoration.VERTICAL
                )
                dividerItemDecoration.isLastItemDecorated = false

                // https://github.com/material-components/material-components-android/blob/master/docs/components/Divider.md
                // Needed if you did not set colorOnSurface in your theme -> default according to Material should be colorOnSurface (12% opacity applied automatically on top).
                // dividerItemDecoration.setDividerColorResource(it, R.color.colorPrimary)

                addItemDecoration(dividerItemDecoration)
            }
        }

        binding.clubsRetryButton.setOnClickListener {
            viewModel.retryLoadingData()
        }

        activity?.setTitle(R.string.app_title)

        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created. It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clubs.observe(viewLifecycleOwner) { clubsList ->
            clubsList?.let {
                clubsListAdapter.dataset = it
            }
        }

        // Alternatively use 2 Material Chips, but if the amount of filters should grow in the future maybe the idea is not so good.
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_clubs, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        viewModel.toggleSorting()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Observer for the sorting event.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventSorted
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collectLatest {
                        showSortedListToast(it)
                    }
        }
    }

    // Attention: don't use this approach when showing a Toast/Popup/etc. as error. We could use this but then we would need special
    // handling otherwise we would also call this at onConfigurationChanges and the user will get triggered on each configuration change.
    override fun onResume() {
        super.onResume()
        // If we already registered an error and the list is empty we mostly are coming back to the app and
        // we need to manually trigger a retry because the ViewModel is already in the memory and won't fetch again by itself.
        // TODO: test case where the list is cached and then you are offline.
        if (viewModel.eventNetworkError.value == true && viewModel.clubs.value.isNullOrEmpty()) {
            viewModel.retryLoadingData()
        }
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            // We don't use a toast anymore since we added the text directly to the view.
            //Toast.makeText(activity, getString(R.string.error_loading), Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    /**
     * Method for displaying a Toast after the list was sorted using the toggle filter menu.
     */
    private fun showSortedListToast(sortingMode: ClubsViewModel.SortingMode) {
        context?.let {
            when (sortingMode) {
                ClubsViewModel.SortingMode.SORT_BY_NAME_ASCENDING -> showSortedListToast(
                        it.getString(
                                R.string.event_sorted_by_name
                        )
                )
                ClubsViewModel.SortingMode.SORT_BY_VALUE_DESCENDING -> showSortedListToast(
                        it.getString(
                                R.string.event_sort_by_value
                        )
                )
            }
        }
    }

    /**
     * Method for displaying a Toast after the list was sorted using the toggle filter menu.
     */
    private fun showSortedListToast(toastMessage: String) {
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onClubClick(clubObject: ClubEntity, clubImageView: ImageView) {
        // Note: using this instead of "old" navigation method the ripple effect is not visible, maybe bug or works as designed, not sure.
        val extras = FragmentNavigatorExtras(clubImageView to clubImageView.transitionName)
        val action = ClubsListFragmentDirections.showClubDetails(clubObject)
        findNavController().navigate(action, extras)
    }
}