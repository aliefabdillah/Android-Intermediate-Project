package com.dicoding.mystudentdata.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: List<Student>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUniversity(university: List<University>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourse(course: List<Course>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourseStudentCrossRef(courseStudentCrossRef: List<CourseStudentCrossRef>)

//    @Query("SELECT * from student")
//    fun getAllStudent(): LiveData<List<Student>>

    //anotasi menandakan penggunaan rawQuery
    //agar bisa diobserve tabelnya menggunakan observedEntities
//    @RawQuery(observedEntities = [Student::class])
//    fun getAllStudent(query: SupportSQLiteQuery): LiveData<List<Student>>

    //get data menggunakan paging 2
    //DataSource adalah sebuah kelas dasar untuk mengatur seberapa banyak data yang diambil ke dalam PagedList.
    @RawQuery(observedEntities = [Student::class])
    fun getAllStudent(query: SupportSQLiteQuery): DataSource.Factory<Int, Student>


    //transaction digunakan untuk menjalankan lebih dari satu query secara bersama
    @Transaction
    //memanggil kelas utama dan kembalian berupa relasi
    @Query("SELECT * from student")
    fun getAllStudentAndUniversity(): LiveData<List<StudentAndUniversity>>

    @Transaction
    @Query("SELECT * from university")
    fun getAllUnivAndStudent(): LiveData<List<UniversityAndStudent>>

    @Transaction
    @Query("SELECT * from student")
    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>>
}