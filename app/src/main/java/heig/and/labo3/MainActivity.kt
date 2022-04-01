package heig.and.labo3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import heig.and.labo3.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var datePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Builds the date picker
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        datePickerBuilder.setTitleText("Date")
        datePicker = datePickerBuilder.build()

        // Datepicker update listener
        datePicker.addOnPositiveButtonClickListener {
            // Sets the selected date
            binding.eMainBaseBirthday.setText(Person.dateFormatter.format(it))
        }

        // Listener on click on birthday button or input field
        binding.bMainBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.eMainBaseBirthday.setOnClickListener {
            showDatePicker()
        }

        // Adds occupation click listener to display / hide detailed groups
        binding.rgOccupation.setOnCheckedChangeListener {_, choiceId ->
            showHideDetails(choiceId)
        }

        // Opens photo mode on picture click
        binding.imageAdditionalPicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // On submit click
        binding.bBtnOk.setOnClickListener {
            validateFields()
        }

        // On cancel click
        binding.bBtnCancel.setOnClickListener {
            clearFields()
        }

        // Fills the fields
        fillFileds()
    }

    private fun showDatePicker() {
        if (!datePicker.isVisible) {
            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER");
        }
    }

    private fun showHideDetails(choiceId: Int) {
        println("CHOICE : ".plus(choiceId))
        when(choiceId) {
            R.id.rbStudent -> {
                binding.groupMainSpecificStudents.visibility = View.VISIBLE
                binding.groupMainSpecificWorkers.visibility = View.GONE
            }
            R.id.rbEmployee -> {
                binding.groupMainSpecificStudents.visibility = View.GONE
                binding.groupMainSpecificWorkers.visibility = View.VISIBLE
            }
            else -> {
                binding.groupMainSpecificStudents.visibility = View.GONE
                binding.groupMainSpecificWorkers.visibility = View.GONE
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun fillFileds() {
        // Name and Firstname
        binding.eMainBaseName.setText(CURRENT_PERSON.name)
        binding.eMainBaseFirstname.setText(CURRENT_PERSON.firstName)

        // Birth date
        binding.eMainBaseBirthday.setText(Person.dateFormatter.format(CURRENT_PERSON.birthDay.time))

        // Nationality
        val natId = this.resources.getStringArray(R.array.nationalities).indexOf(CURRENT_PERSON.nationality)
        binding.sNationalities.setSelection(natId)

        // Details
        when (CURRENT_PERSON) {
            is Student -> {
                // Select radio choice
                binding.rgOccupation.check(R.id.rbStudent)

                // University and diploma year
                binding.eMainSpecificSchoolTitle.setText(CURRENT_PERSON.university)
                binding.eMainSpecificGraduationyearTitle.setText(CURRENT_PERSON.graduationYear.toString())
            }
            is Worker -> {
                // Select radio choice
                binding.rgOccupation.check(R.id.rbEmployee)

                // Company, sector and experience
                binding.eMainSpecificCompagnyTitle.setText(CURRENT_PERSON.company)
                val sectId = this.resources.getStringArray(R.array.sectors).indexOf(CURRENT_PERSON.sector)
                binding.sSectors.setSelection(sectId)
                binding.eMainSpecificExperienceTitle.setText(CURRENT_PERSON.experienceYear.toString())
            }
        }

        // Email address
        binding.eAdditionalEmailTitle.setText(CURRENT_PERSON.email)

        // Photo TODO

        // Comments
        binding.tAdditionalRemarksContent.setText(CURRENT_PERSON.remark)
    }

    private fun clearFields() {
        val count: Int = binding.mainConstraintLayout.childCount
        for (i in 0 until count) {
            when (val view: View = binding.mainConstraintLayout.getChildAt(i)) {
                is EditText -> {
                    view.setText("")
                }
                is Spinner -> {
                    view.setSelection(0)
                }
                is RadioGroup -> {
                    view.clearCheck()
                }
            }
        }
    }

    private fun validateFields() {
        // Validate name and firstname
        validateField(binding.eMainBaseName)
        validateField(binding.eMainBaseFirstname)

        // Birthdate
        validateField(binding.eMainBaseBirthday)

        // Details
        when(binding.rgOccupation.checkedRadioButtonId) {
            binding.rbStudent.id -> {
                validateField(binding.eMainSpecificSchoolTitle)
                validateField(binding.eMainSpecificGraduationyearTitle)
            }
            binding.rbEmployee.id -> {
                validateField(binding.eMainSpecificCompagnyTitle)
                validateField(binding.eMainSpecificExperienceTitle)
            }
        }

        // Email
        validateField(binding.eAdditionalEmailTitle)
    }

    private fun validateField(field: EditText) {
        if (field.text.isEmpty()) {
            field.error = getString(R.string.err_empty_field)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            binding.imageAdditionalPicture.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        val CURRENT_PERSON = Person.exampleStudent as Person
    }
}