package com.dicoding.mystudentdata

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.dicoding.mystudentdata.database.Student
import com.dicoding.mystudentdata.database.StudentAndUniversity
import com.dicoding.mystudentdata.database.StudentWithCourse
import com.dicoding.mystudentdata.database.UniversityAndStudent
import com.dicoding.mystudentdata.helper.SortType
import kotlinx.coroutines.launch

class MainViewModel(private val studentRepository: StudentRepository) : ViewModel() {

//    init {
//        insertAllData()
//    }
    private val _sort = MutableLiveData<SortType>()

    init{
        _sort.value = SortType.ASCENDING
    }

    fun changeSortType(sortType: SortType){
        _sort.value = sortType
    }

    //memanggil data student berdasarkan sort
//    fun getAllStudent(): LiveData<List<Student>> = _sort.switchMap {
//        studentRepository.getAllStudent(it)
//    }

    //menggunakan paging 2
    fun getAllStudent(): LiveData<PagedList<Student>> = _sort.switchMap {
        studentRepository.getAllStudent(it)
    }

    fun getAllStudentAndUniversity(): LiveData<List<StudentAndUniversity>> = studentRepository.getAllStudentAndUniversity()

    fun getAllUnivAndStudent(): LiveData<List<UniversityAndStudent>> = studentRepository.getAllUnivAndStudent()

    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>> = studentRepository.getAllStudentWithCourse()

//    private fun insertAllData() = viewModelScope.launch {
//        studentRepository.insertAllData()
//    }
}

class ViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}