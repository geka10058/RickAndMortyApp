package com.example.rickmortyapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import com.example.rickmortyapp.ui.fragments.characters.CharacterFragment
import com.example.rickmortyapp.ui.fragments.episodes.EpisodeFragment
import com.example.rickmortyapp.ui.fragments.locations.LocationsFragment
import com.example.rickmortyapp.R
import com.example.rickmortyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = 0
    private var characterFragment = CharacterFragment()
    private var locationsFragment = LocationsFragment()
    private var episodesFragment = EpisodeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbar)

            bottomNavMenu.setOnNavigationItemSelectedListener {
                handleBottomNavigation(it.itemId)
            }
            //bottomNavMenu.selectedItemId = R.id.charactersFragment
        }
    }

    private fun handleBottomNavigation(menuItemId: Int): Boolean = when (menuItemId) {
        R.id.charactersFragment -> {
            setCurrentFragment(characterFragment, R.string.title_character_fragment, menuItemId)
            true
        }
        R.id.locationsFragment -> {
            setCurrentFragment(locationsFragment, R.string.title_locations_fragment, menuItemId)
            true
        }
        R.id.episodesFragment -> {
            setCurrentFragment(episodesFragment, R.string.title_episodes_fragment, menuItemId)
            true
        }
        else -> false
    }

    private fun setCurrentFragment(fragment: Fragment, fragmentTitle: Int, menuItemId: Int) {
        currentMenuItemId = menuItemId
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_for_fragments, fragment)
            .commit()
        supportActionBar?.title = getString(fragmentTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }
}