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
import com.dicoding.storyapp.adapter.ListStoryAdapter
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.PagingStoryAdapter
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.databinding.FragmentListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.DbViewModel
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
                    binding.loadingIcon.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> {
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
                    Toast.makeText(requireActivity(), getString(R.string.no_internet_connetion), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*private fun getStoryCallback(token: String) {
        listStoryViewModel.getListStories(token).observe(viewLifecycleOwner){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> binding.loadingIcon.visibility = View.VISIBLE
                    is Result.Success -> {
                        if (result.data.isEmpty()){
                            Toast.makeText(requireActivity(), getString(R.string.list_story_is_empty), Toast.LENGTH_LONG).show()
                        }else{
                            binding.loadingIcon.visibility = View.GONE
                            result.data.forEach {
                                dbViewModel.saveStoryToDb(it)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.loadingIcon.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(requireActivity(), toastText, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }*/

    /*private fun showResult(listStories: List<StoryEntity>) {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvStory.layoutManager = layoutManager

        binding.rvStory.setHasFixedSize(true)

        val adapter = ListStoryAdapter(listStories)
        if (adapter.itemCount == 0){
            binding.tvEmptyListStory.alpha = 1f
        }else{
            binding.tvEmptyListStory.alpha = 0f
            binding.rvStory.adapter = adapter
            adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback{
                override fun onItemClicked(data: StoryEntity) {
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

    }*/

    companion object{
        var TOKEN = ""
        const val REFRESH_TIME = 2000L
    }
}