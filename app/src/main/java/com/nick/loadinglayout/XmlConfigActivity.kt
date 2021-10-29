package com.nick.loadinglayout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.nick.loading.State
import com.nick.loading.LoadStateLayout
import com.nick.loadinglayout.databinding.ActivityXmlConfigBinding


class XmlConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityXmlConfigBinding


    private var loadStateLayout: LoadStateLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityXmlConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfig.setOnClickListener {
            showPopupMenu(it)
        }

        binding.btnSwitchState.setOnClickListener {
            when (loadStateLayout?.state) {
                State.AttachView -> {
                    loadStateLayout?.state = State.Loading
                }
                State.Loading -> {
                    loadStateLayout?.state = State.EmptyData
                }
                State.EmptyData -> {
                    loadStateLayout?.state = State.ErrorData
                }
                State.ErrorData -> {
                    loadStateLayout?.state = State.ErrorNetwork
                }
                State.ErrorNetwork -> {
                    loadStateLayout?.state = State.AttachView
                }
            }
        }

        hideAllLoadState()
        // 默认状态
        loadStateLayout = binding.loadStateView1
        loadStateLayout?.visibility = View.VISIBLE
        loadStateLayout?.state = State.AttachView
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_config, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            hideAllLoadState()
            when (it.itemId) {
                R.id.action0 -> {
                    loadStateLayout = binding.loadStateView1
                }
                R.id.action1 -> {
                    loadStateLayout = binding.loadStateView2
                }
                R.id.action2 -> {
                    loadStateLayout = binding.loadStateView3
                }
            }

            loadStateLayout?.visibility = View.VISIBLE
            loadStateLayout?.state = State.AttachView

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