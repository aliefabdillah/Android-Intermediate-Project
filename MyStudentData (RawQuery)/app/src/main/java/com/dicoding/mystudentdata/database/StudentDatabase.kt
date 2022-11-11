package com.dicoding.mystudentdata.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.mystudentdata.helper.InitialDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Student::class, University::class, Course::class, CourseStudentCrossRef::class],
    version = 3,
    exportSchema = true,
    //property auto migration untuk migrasi otomatis
    autoMigrations = [
        AutoMigration(from = 2, to = 3, spec = StudentDatabase.MyAutoMigration::class)
    ]
)
abstract class StudentDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    //membuat class spec untuk automigration dan anotasi perubahan yang terjadi
    @RenameColumn(tableName = "University", fromColumnName = "name", toColumnName = "universityName")
    class MyAutoMigration: AutoMigrationSpec

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context, applicationScope: CoroutineScope): StudentDatabase {
            if (INSTANCE == null) {
                synchronized(StudentDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            StudentDatabase::class.java, "student_database")
                        .fallbackToDestructiveMigration()
                        //pre populate dari asset (tapi ini tidak tersedia di in memory room untuk testing)
//                        .createFromAsset("student_database.db")
                         //pre populate dengan addCallback
                        .addCallback(object : Callback(){
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                INSTANCE?.let { database ->
                                    applicationScope.launch {
                                        val studentDao = database.studentDao()
                                        studentDao.insertStudent(InitialDataSource.getStudents())
                                        studentDao.insertUniversity(InitialDataSource.getUniversities())
                                        studentDao.insertCourse(InitialDataSource.getCourses())
                                        studentDao.insertCourseStudentCrossRef(InitialDataSource.getCourseStudentRelation())                                }
                                    }

                            }
                        })
                        .build()
                }
            }
            return INSTANCE as StudentDatabase
        }

    }
}