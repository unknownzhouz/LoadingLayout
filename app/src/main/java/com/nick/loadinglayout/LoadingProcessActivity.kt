package com.nick.loadinglayout

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nick.loading.LoadState
import com.nick.loading.StateModel
import com.nick.loadinglayout.databinding.ActivityLoadingProcessBinding
import com.nick.loadinglayout.databinding.ViewItemFaqBinding
import com.nick.loadinglayout.util.EmptyFactory
import java.util.ArrayList

class LoadingProcessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingProcessBinding

    private var startProcess = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.postDelayed({
                binding.swipeRefresh.isRefreshing = false
            }, 2000)
        }

        binding.switchState.setOnClickListener {
            if (startProcess) return@setOnClickListener
            showPopupMenu(it)
        }

        binding.loadStateView.reload = {
            playStateView(LoadState.AttachView)
        }

        binding.loadStateView.offsetStateView = {
            when (it) {
                LoadState.Loading -> {
                    val point = Point()
                    point.y = -600
                    point
                }
                else -> {
                    null
                }
            }
        }

        binding.loadStateView.buildStateModel = {
            if (it == LoadState.ErrorData) {
                val model = StateModel()
                model.title = "数据异常\n替换新文本内容和新图标"
                model.iconId = R.drawable.ic_computer
                model
            } else {
                null
            }
        }

        binding.loadStateView.buildStateView = {
            if (it == LoadState.EmptyData) {
                EmptyFactory.SearchView(this, goBack = {
                    onBackPressed()
                 })
            } else {
                null
            }
        }

        val adapter = SimpleAdapter()
        adapter.onItemClick = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_second, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action0 -> {
                    playStateView(LoadState.AttachView)
                }
                R.id.action1 -> {
                    playStateView(LoadState.EmptyData)
                }
                R.id.action2 -> {
                    playStateView(LoadState.ErrorData)
                }
                R.id.action3 -> {
                    playStateView(LoadState.ErrorNetwork)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun playStateView(finish: LoadState) {
        startProcess = true
        binding.loadStateView.state = LoadState.Loading
        binding.loadStateView.postDelayed({
            binding.loadStateView.state = finish
            startProcess = false
        }, 2000)
    }


    class SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {
        private val items = ArrayList<String>()
        var onItemClick: ((String) -> Unit)? = null

        init {
            for (i in 0 until 100) {
                items.add("$i 这是一段简单简介文本")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
            val binding = ViewItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SimpleViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            holder.bindData(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class SimpleViewHolder(private val binding: ViewItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bindData(data: String) {
                binding.tvTitle.text = data
                binding.tvTitle.setOnClickListener {
                    onItemClick?.invoke(data)
                }
            }
        }
    }

}