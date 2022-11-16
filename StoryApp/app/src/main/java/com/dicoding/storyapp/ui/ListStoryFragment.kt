package com.dicoding.storyapp.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.PagingStoryAdapter
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.databinding.FragmentListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory
import kotlinx.coroutines.launch

class ListStoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentListStoryBinding
    private lateinit var itemBinding: ItemRowStoryBinding

    private val listStoryViewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListStoryBinding.inflate(inflater, container, false)
        itemBinding = ItemRowStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setColorSchemeResources(R.color.orange_200)
        binding.swipeRefresh.setOnRefreshListener(this)
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())

        token = arguments?.getString(TOKEN)!!
        getStoryPagingCallback(token)
    }

    override fun onRefresh() {
        getStoryPagingCallback(token)
        Handler().postDelayed({
            binding.swipeRefresh.isRefreshing = false
        }, REFRESH_TIME)
    }

    private fun getStoryPagingCallback(token: String){

        val adapter = PagingStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter  {
                adapter.retry()
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            listStoryViewModel.getStoryPaging(token).observe(viewLifecycleOwner){
                adapter.submitData(lifecycle, it)
            }
        }

        adapter.addLoadStateListener { loadState ->
            when(loadState.source.refresh){
                is LoadState.Loading -> {
                    binding.erroAlert.alertConnection.isVisible = false
                    binding.loadingIcon.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> {
                    binding.erroAlert.alertConnection.isVisible = false
                    binding.loadingIcon.visibility = View.GONE
                    if (loadState.append.endOfPaginationReached && adapter.itemCount < 1){
                        binding.tvEmptyListStory.isVisible = true
                    }
                    else{
                        binding.tvEmptyListStory.isVisible = false
                        adapter.setOnItemClickCallback(object : PagingStoryAdapter.OnItemClickCallback{
                            override fun onItemClicked(data: ListStoryItem) {
                                val iToDetail = Intent(requireActivity(), DetailStoryActivity::class.java)
                                iToDetail.putExtra("ID", data.id)
                                iToDetail.putExtra("TOKEN", token)
                                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(),
                                    Pair(itemBinding.imgItem, "storyImage"),
                                    Pair(itemBinding.tvUsername, "username")
                                )
                                startActivity(iToDetail, options.toBundle())
                            }
                        })

                    }
                }
                is LoadState.Error -> {
                    Handler().postDelayed({
                        binding.loadingIcon.visibility = View.GONE
                        binding.erroAlert.alertConnection.isVisible = true
                        Toast.makeText(requireActivity(), getString(R.string.no_internet_connetion), Toast.LENGTH_SHORT).show()
                    }, TIMEOUT_TIME)
                }
            }
        }
    }

    companion object{
        var TOKEN = ""
        const val REFRESH_TIME = 2000L
        const val TIMEOUT_TIME = 7000L
    }
}