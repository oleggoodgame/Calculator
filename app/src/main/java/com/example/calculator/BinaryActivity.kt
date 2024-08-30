package com.example.calculator

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.BinaryMainBinding
import com.google.android.material.tabs.TabLayout

class BinaryActivity : AppCompatActivity() {
// I wanted to add a binary calculator through tabLoyaut, but through Intent
    // I realized that it is very bad, that is, it does not work as it should.
    // And I thought that I will not integrate because it is just a calculator, the first good project
    private lateinit var binding: BinaryMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BinaryMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("TabPrefs", MODE_PRIVATE)

        setupTabLayout()
    }

    private fun setupTabLayout() {
        val savedTab = sharedPreferences.getInt("selected_tab", 0)

        binding.tabLoyaut.removeAllTabs()
        binding.tabLoyaut.addTab(binding.tabLoyaut.newTab().setText("Calculator"))
        binding.tabLoyaut.addTab(binding.tabLoyaut.newTab().setText("Tab 2"))
        binding.tabLoyaut.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            startActivity(Intent(this@BinaryActivity, MainActivity::class.java))
                        }
                        1 -> {
                            startActivity(Intent(this@BinaryActivity, BinaryActivity::class.java))
                        }
                    }
                }

                with(sharedPreferences.edit()) {
                    putInt("selected_tab", tab?.position ?: 0)
                    apply()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Реакція на зняття вибору вкладки
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Реакція на повторний вибір вкладки
            }
        })

        binding.tabLoyaut.getTabAt(savedTab)?.select()
    }
}
