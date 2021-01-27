package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.detail.DetailViewModelFactory

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            viewModel.displayAsteroidDetails(it)
        })

        viewModel.navigateToSelectedAndroid.observe(viewLifecycleOwner, Observer {
            if (null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAndroidDetailsComplete()
            }
        })

        viewModel.populateDatabase.observe(viewLifecycleOwner, Observer {
            if(it) {
                alertEmptyDatabaseDialog()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> {
                viewModel.displayAsteroids(Selection.NEXT7DAYS)
            }
            R.id.show_today_menu -> {
                viewModel.displayAsteroids(Selection.TODAY)
            }
            R.id.show_saved_menu -> {
                viewModel.displayAsteroids(Selection.ALLSTORED)
            }
        }
        return true
    }

    private fun alertEmptyDatabaseDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.populate_database_caption))
            .setMessage(getString(R.string.populate_database_button))
            .setPositiveButton(android.R.string.ok) { _, _ ->context?.let {
                    viewModel.populateDatabase(it)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}
