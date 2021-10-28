package com.nick.loadinglayout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.nick.loading.LoadState
import com.nick.loading.LoadStateView
import com.nick.loadinglayout.databinding.ActivityXmlConfigBinding


class XmlConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityXmlConfigBinding


    private var loadStateView: LoadStateView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityXmlConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfig.setOnClickListener {
            showPopupMenu(it)
        }

        binding.btnSwitchState.setOnClickListener {
            when (loadStateView?.state) {
                LoadState.AttachView -> {
                    loadStateView?.state = LoadState.Loading
                }
                LoadState.Loading -> {
                    loadStateView?.state = LoadState.EmptyData
                }
                LoadState.EmptyData -> {
                    loadStateView?.state = LoadState.ErrorData
                }
                LoadState.ErrorData -> {
                    loadStateView?.state = LoadState.ErrorNetwork
                }
                LoadState.ErrorNetwork -> {
                    loadStateView?.state = LoadState.AttachView
                }
            }
        }

        hideAllLoadState()
        // 默认状态
        loadStateView = binding.loadStateView1
        loadStateView?.visibility = View.VISIBLE
        loadStateView?.state = LoadState.AttachView
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_config, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            hideAllLoadState()
            when (it.itemId) {
                R.id.action0 -> {
                    loadStateView = binding.loadStateView1
                }
                R.id.action1 -> {
                    loadStateView = binding.loadStateView2
                }
                R.id.action2 -> {
                    loadStateView = binding.loadStateView3
                }
            }

            loadStateView?.visibility = View.VISIBLE
            loadStateView?.state = LoadState.AttachView

            true
        }
        popupMenu.show()
    }

    private fun hideAllLoadState() {
        binding.loadStateView1.visibility = View.INVISIBLE
        binding.loadStateView2.visibility = View.INVISIBLE
        binding.loadStateView3.visibility = View.INVISIBLE
    }

}