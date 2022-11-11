package com.dicoding.mystudentdata.database

import androidx.room.*

@Entity
data class Student(
    @PrimaryKey
    val studentId: Int,
    val name: String,
    val univId: Int,
)

@Entity
data class University(
    @PrimaryKey
    val universityId: Int,
    val name: String,
)

@Entity
data class Course(
    @PrimaryKey
    val courseId: Int,
    val name: String,
)

data class StudentAndUniversity(
    @Embedded
    val student: Student,

    @Relation(
        parentColumn = "univId",
        entityColumn = "universityId"
    )
    val university: University? = null
)

//relasi one to many antara tabel university dan student
data class UniversityAndStudent(
    //anotasi @Embedded digunakan untuk menambahkan data utama yang ingin ditampilkan
    @Embedded
    val university: University,

    //anotasi untuk mengetahui relasi antar tabel
    @Relation(
        parentColumn = "universityId",          //parent colum berisi primarykey dari tabel parent
        entityColumn = "univId"                 //entityColum berisi foreign key dari tabel tujuan
    )
    val student: List<Student>      //berbentuk list karena 0ne to many
)

//membuat tabel baru berisi id dari 2 tabel yang ingin direlasikan
@Entity(primaryKeys = ["sId", "cId"])
data class CourseStudentCrossRef(
    val sId: Int,                   //id baru yang mereferensikan studentId
    @ColumnInfo(index = true)
    val cId: Int                    //id baru mereferensikan courseId
)

data class StudentWithCourse(
    @Embedded
    val studentAndUniversity: StudentAndUniversity,

    @Relation(
        parentColumn = "studentId",
        entity = Course::class,                         //tabel tujuan yang ingin direlasikan
        entityColumn = "courseId",
        //associateBy diguakana untuk menambahkan referensi antara kedua kelas
        associateBy = Junction(
            value = CourseStudentCrossRef::class,
            parentColumn = "sId",
            entityColumn = "cId"
        )
        /*
        Sebagai catatan, apabila id pada masing-masing tabel (Student dan Course) sama dengan id
        pada tabel relasi (CourseStudentCrossRef), Anda tidak perlu menambahkan property parentColumn
        dan entityColumn pada Junction.
        * */
    )
    val course: List<Course>
)