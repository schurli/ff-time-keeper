package at.ff.timekeeper.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import at.ff.timekeeper.R
import at.ff.timekeeper.data.model.Navigation
import at.ff.timekeeper.databinding.ActivityMainBinding
import at.ff.timekeeper.ui.HasOnBackPressed
import at.ff.timekeeper.ui.main.fragment.HomeFragment
import at.ff.timekeeper.vm.MainViewModel
import at.ff.timekeeper.vm.ViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var activity: MainActivity
    private lateinit var fragmentManager: FragmentManager
    private var contentFragment: HasOnBackPressed? = null

    private var viewModelFactory: ViewModelFactory? = null

    private lateinit var viewModel: MainViewModel
    private var navigation: Navigation? = null

    @Inject
    fun setViewModelFactory(viewModelFactory: ViewModelFactory?) {
        this.viewModelFactory = viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        activity = this
        fragmentManager = supportFragmentManager
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(
            MainViewModel::class.java
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.elementAppBarSetting.setOnClickListener {
            viewModel.setNavigation(
                Navigation.SYSTEM_SETTING
            )
        }
        binding.elementAppBarBack.setOnClickListener {
            viewModel.setNavigation(
                Navigation.HOME
            )
        }

        bind()
    }

    private fun bind() {
        viewModel.navigation.observe(activity) {
            var navigation = it
            Timber.d("getNavigation(%s)", navigation)
            if (navigation == null) {
                navigation = Navigation.HOME
            }
            this.navigation = navigation
            fragment(navigation)
        }
    }

    private fun fragment(navigation: Navigation) {
        Timber.d("fragment(%s)", navigation.name)

        val transaction = fragmentManager.beginTransaction()


        hideAppBar()
        hideSetting()

        when (navigation) {
            Navigation.HOME -> {
                showSetting()
                contentFragment = HomeFragment()
            }

            Navigation.SYSTEM_SETTING -> hideSetting()
        }

        transaction
            .replace(R.id.content, (contentFragment as Fragment?)!!)
            .commit()
    }

    private fun showSetting() {
        binding.elementAppBarSetting.visibility = View.VISIBLE
        binding.elementAppBarBack.visibility = View.GONE
    }

    private fun hideSetting() {
        binding.elementAppBarSetting.visibility = View.GONE
        binding.elementAppBarBack.visibility = View.VISIBLE
    }

    private fun showAppBar() {
        binding.elementAppBar.visibility = View.VISIBLE
    }

    private fun hideAppBar() {
        binding.elementAppBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        contentFragment?.onBackPressed()
    }
}
