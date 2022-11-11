package com.dicoding.mystudentdata

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystudentdata.adapter.StudentAndUnivAdapter
import com.dicoding.mystudentdata.adapter.StudentListAdapter
import com.dicoding.mystudentdata.adapter.StudentWithCourseAdapter
import com.dicoding.mystudentdata.adapter.UnivAndStudentAdapter
import com.dicoding.mystudentdata.databinding.ActivityMainBinding
import com.dicoding.mystudentdata.helper.SortType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStudent.layoutManager = LinearLayoutManager(this)

        getStudent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_single_table -> {
                getStudent()
                //menset tombol sorting muncul ketika hanya di menu single table
                showSortingOptionMenu(true)
                return true
            }
            R.id.action_many_to_one -> {
                getStudentAndUniversity()
                showSortingOptionMenu(false)
                true
            }
            R.id.action_one_to_many -> {
                getUniversityAndStudent()
                showSortingOptionMenu(false)
                true
            }

            R.id.action_many_to_many -> {
                getStudentWithCourse()
                showSortingOptionMenu(false)
                true
            }
            R.id.action_sort -> {
                //method menampilkan popup ketika diklik
                showSortingPopupMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //method untuk mengatur sort menu tampil atau tidak
    private fun showSortingOptionMenu(isShow: Boolean) {
        val view = findViewById<View>(R.id.action_sort) ?: return
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun showSortingPopupMenu() {
        val view = findViewById<View>(R.id.action_sort) ?: return
        PopupMenu(this, view).run {
            //inflate layout sorting menu ke pop up menu
            menuInflater.inflate(R.menu.sorting_menu, menu)

            //memberikan aksi ketika tombol pop up  di klik
            setOnMenuItemClickListener {
                mainViewModel.changeSortType(
                    when(it.itemId){
                        R.id.action_ascending -> SortType.ASCENDING
                        R.id.action_descending -> SortType.DESCENDING
                        else -> SortType.RANDOM
                    }
                )
                true
            }
        show()
        }
    }

    private fun getStudent() {
        val adapter = StudentListAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudent().observe(this) {
            adapter.submitList(it)
        }
    }

    private fun getStudentAndUniversity() {
        val adapter = StudentAndUnivAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudentAndUniversity().observe(this){
            adapter.submitList(it)
            Log.d(TAG, "getStudentAndUniv: $it")
        }
    }

    private fun getUniversityAndStudent() {
        val adapter = UnivAndStudentAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllUnivAndStudent().observe(this){
            adapter.submitList(it)
            Log.d(TAG, "getUnivAndStudent: $it")
        }
    }


    private fun getStudentWithCourse() {
        val adapter = StudentWithCourseAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudentWithCourse().observe(this){
            adapter.submitList(it)
            Log.d(TAG, "getStudentWithCourse: $it")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}