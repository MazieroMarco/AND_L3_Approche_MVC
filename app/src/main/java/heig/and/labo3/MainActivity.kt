package heig.and.labo3

import android.graphics.Picture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.constraintlayout.widget.Group
import java.util.*

class MainActivity : AppCompatActivity() {

    // Common widgets
    private lateinit var nameInput: EditText
    private lateinit var firstnameInput: EditText
    private lateinit var birthInput: Calendar
    private lateinit var nationalityInput: Spinner
    private lateinit var occupationInput: RadioGroup
    private lateinit var emailInput: EditText
    private lateinit var photoInput: Picture
    private lateinit var commentsInput: EditText

    // Student widgets
    private var schoolInput: EditText? = null
    private var graduationInput: EditText? = null

    // Employee widgets
    private var companyInput: EditText? = null
    private var sectorInput: Spinner? = null
    private var experienceInput: EditText? = null

    // Groups
    private lateinit var studentGroup: Group
    private lateinit var employeeGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* TODO Retrieves the view widgets
        nameInput = findViewById(R.layout.name)
        firstnameInput = findViewById(R.layout.name)
        birthInput = findViewById(R.layout.name)
        nationalityInput = findViewById(R.layout.name)
        occupationInput = findViewById(R.layout.name)
        emailInput = findViewById(R.layout.name)
        photoInput = findViewById(R.layout.name)
        commentsInput = findViewById(R.layout.name)
        */
        
    }
}