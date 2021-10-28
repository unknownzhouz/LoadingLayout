package com.nick.loadinglayout

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nick.loading.LoadState
import com.nick.loadinglayout.databinding.ActivitySwitchStateBinding


class SwitchStateActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwitchStateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySwitchStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.loadStateView.reload = {
            binding.loadStateView.state = LoadState.Loading
            Toast.makeText(this, "正在加载中...", Toast.LENGTH_LONG).show()
        }

        binding.btnSwitchState.setOnClickListener {
            when (binding.loadStateView.state) {
                LoadState.AttachView -> {
                    binding.loadStateView.state = LoadState.Loading
                    Toast.makeText(this, "正在加载中...", Toast.LENGTH_LONG).show()
                }
                LoadState.Loading -> {
                    binding.loadStateView.state = LoadState.EmptyData
                    Toast.makeText(this, "数据为空时的效果", Toast.LENGTH_LONG).show()
                }
                LoadState.EmptyData -> {
                    binding.loadStateView.state = LoadState.ErrorData
                    Toast.makeText(this, "数据读取错误时的效果", Toast.LENGTH_LONG).show()
                }
                LoadState.ErrorData -> {
                    binding.loadStateView.state = LoadState.ErrorNetwork
                    Toast.makeText(this, "网络异常时的效果", Toast.LENGTH_LONG).show()
                }
                LoadState.ErrorNetwork -> {
                    binding.loadStateView.state = LoadState.AttachView
                    Toast.makeText(this, "显示正常视图时的效果", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}