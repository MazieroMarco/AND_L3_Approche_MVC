package ch.heigvd.iict.and.labo3

import java.text.DateFormat
import java.util.*

/*
 *  Nous définissons ici les deux classes, Worker et Student, qui représentent le modèle de
 *  notre application. Celles-ci héritent de la classe Person qui est abstraite.
 *  Malheureusement en Kotlin, les data class ne permettent pas l’héritage…
 *  Nous fournissons également exemples, un étudiant et une employée
 */

abstract class Person(var name: String,
                  var firstName: String,
                  var birthDay : Calendar,
                  var nationality: String,
                  var email : String,
                  var remark : String,
                  var picturePath : String? = null ) {

    protected fun superToString(): String {
        return "name: $name, firstName: $firstName, birthDay: ${dateFormatter.format(birthDay.time)}, nationality: $nationality, email: $email, picturePath: $picturePath, remark: $remark"
    }

    companion object {
        val dateFormatter = DateFormat.getDateInstance() //depends on phone's current locale

        val exampleWorker = Worker(
            "Devost",
            "Chantal",
            Calendar.getInstance().apply {
                set(Calendar.YEAR, 1996)
                set(Calendar.MONTH, Calendar.JUNE)
                set(Calendar.DAY_OF_MONTH, 12)
            },
            "Suisse",
            "HEIG-VD",
            "Sciences",
            2,
            "c.devost@email.com",
            ""
        )
        val exampleStudent = Student(
            "Dreher",
            "Matthias",
            Calendar.getInstance().apply {
                set(Calendar.YEAR, 1998)
                set(Calendar.MONTH, Calendar.APRIL)
                set(Calendar.DAY_OF_MONTH, 8)
            },
            "Allemand",
            "HEIG-VD",
            2023,
            "m.dreher@email.com",
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        )
    }

}

class Student(name: String,
              firstName: String,
              birthDay : Calendar,
              nationality: String,
              var university : String,
              var graduationYear : Int,
              email : String,
              remark: String,
              picturePath : String? = null) : Person(name, firstName, birthDay, nationality, email, remark, picturePath) {

    override fun toString(): String {
        return "Student - ${super.superToString()}, university: $university, graduationYear: $graduationYear"
    }
}

class Worker(name: String,
              firstName: String,
              birthDay : Calendar,
              nationality: String,
              var company : String,
              var sector : String,
              var experienceYear : Int,
              email : String,
              remark: String,
              picturePath : String? = null) : Person(name, firstName, birthDay, nationality, email, remark, picturePath) {

    override fun toString(): String {
        return "Worker - ${super.superToString()}, company: $company, sector: $sector, experienceYear: $experienceYear"
    }
}