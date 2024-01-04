package com.example.nework.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.viewmodel.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    @Inject
    lateinit var auth: AppAuth
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
                        true
                    }

                    R.id.signup -> {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
                        true
                    }

                    R.id.signout -> {
                        auth.removeAuth()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.postsFragment)
                        true
                    }

                    else -> false
                }
        })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.postsFragment, R.id.eventsFragment, R.id.usersFragment -> {
                    navView.visibility = View.VISIBLE
                }

                R.id.myProfileFragment -> {
                    if (!viewModel.authenticated) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
                    } else {
                        navView.visibility = View.VISIBLE
                    }
                }

                else -> {
                    navView.visibility = View.GONE
                }
            }
        }

    }

}
