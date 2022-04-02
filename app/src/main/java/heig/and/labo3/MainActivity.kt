package heig.and.labo3

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import heig.and.labo3.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var datePicker: MaterialDatePicker<Long>

    private var userPictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Builds a new date picker
        datePicker = buildDatePicker()

        // Date picker update listener
        datePicker.addOnPositiveButtonClickListener {
            // Sets the selected date
            binding.eMainBaseBirthday.setText(Person.dateFormatter.format(it))
        }

        // Picture activity callback
        val takePicture = registerForActivityResult(TakePicture()) { imgSaved ->
            if (imgSaved) {
                GlobalScope.launch {
                    val correctedPic = ProfilePictureManager.correctRotation(contentResolver, userPictureUri!!)
                    withContext(Dispatchers.Main) {
                        binding.imageAdditionalPicture.setImageBitmap(correctedPic)
                    }
                }
            }
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
            userPictureUri = ProfilePictureManager.createPicUri(this, cacheDir)
            takePicture.launch(userPictureUri)
        }

        // On submit click
        binding.bBtnOk.setOnClickListener {
            // Validates the fields
            if(validateFields()) {
                // Fields are valid, creates the person
                val person = createNewPerson()
                println(person.toString())

                // Displays alert dialog
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle(getString(R.string.success_person_creation))
                builder.setMessage(person.toString())
                builder.setPositiveButton("Ok", null)
                builder.show()
            }
        }

        // On cancel click
        binding.bBtnCancel.setOnClickListener {
            clearFields()
        }

        // Fills the fields
        fillFields()
    }

    private fun buildDatePicker() : MaterialDatePicker<Long> {
        // Set up date constraints
        val calendar = Calendar.getInstance()
        val upTo = calendar.timeInMillis
        calendar.set(1900, 1, 1)
        val startFrom = calendar.timeInMillis
        val constraints = CalendarConstraints.Builder()
            .setStart(startFrom)
            .setEnd(upTo)
            .build()

        // Builds the date picker
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        datePickerBuilder.setTitleText(getString(R.string.main_base_birthdate_title))
        datePickerBuilder.setCalendarConstraints(constraints)
        return datePickerBuilder.build()
    }

    private fun showDatePicker() {
        if (!datePicker.isVisible) {
            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER");
        }
    }

    private fun showHideDetails(choiceId: Int) {
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

    private fun fillFields() {
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
                is ImageView -> {
                    // Resets user picture
                    if (view.id == binding.imageAdditionalPicture.id) {
                        view.setImageResource(R.drawable.placeholder_selfie)
                    }
                }
            }
        }
    }

    private fun validateFields() : Boolean {
        var result = true

        // Validate name and firstname
        result = result and validateField(binding.eMainBaseName)
        result = result and validateField(binding.eMainBaseFirstname)

        // Birthdate
        result = result and validateField(binding.eMainBaseBirthday)

        // Nationality
        result = result and validateField(binding.sNationalities)

        // Details
        when(binding.rgOccupation.checkedRadioButtonId) {
            binding.rbStudent.id -> {
                result = result and validateField(binding.eMainSpecificSchoolTitle)
                result = result and validateField(binding.eMainSpecificGraduationyearTitle)
            }
            binding.rbEmployee.id -> {
                result = result and validateField(binding.eMainSpecificCompagnyTitle)
                result = result and validateField(binding.eMainSpecificExperienceTitle)
                result = result and validateField(binding.sSectors)
            }
        }

        // Email
        result = result.and(validateField(binding.eAdditionalEmailTitle))

        return result
    }

    private fun validateField(field: View) : Boolean {
        when(field) {
            is EditText -> {
                if (field.text.isEmpty()) {
                    field.error = getString(R.string.err_empty_field)
                    return false
                } else {
                    field.error = null
                }
            }
            is Spinner -> {
                if (field.selectedItemPosition == 0) {
                    val errorText = field.selectedView as TextView
                    errorText.error = getString(R.string.err_empty_field)
                    errorText.setTextColor(Color.RED)
                    return false
                }
            }
        }
        return true
    }

    private fun createNewPerson() : Person? {
        // Gets the date picker date
        val birthday = Calendar.getInstance()
        val date = Person.dateFormatter.parse(binding.eMainBaseBirthday.text.toString())
        birthday.time = date

        var newPerson: Person? = null

        when(binding.rgOccupation.checkedRadioButtonId) {
            binding.rbStudent.id -> {
                newPerson = Student(
                    binding.eMainBaseName.text.toString(),
                    binding.eMainBaseFirstname.text.toString(),
                    birthday,
                    binding.sNationalities.selectedItem.toString(),
                    binding.eMainSpecificSchoolTitle.text.toString(),
                    binding.eMainSpecificGraduationyearTitle.text.toString().toInt(),
                    binding.eAdditionalEmailTitle.text.toString(),
                    binding.tAdditionalRemarksContent.text.toString(),
                    userPictureUri?.path
                )
            }
            binding.rbEmployee.id -> {
                newPerson = Worker(
                    binding.eMainBaseName.text.toString(),
                    binding.eMainBaseFirstname.text.toString(),
                    birthday,
                    binding.sNationalities.selectedItem.toString(),
                    binding.eMainSpecificCompagnyTitle.text.toString(),
                    binding.sSectors.selectedItem.toString(),
                    binding.eMainSpecificExperienceTitle.text.toString().toInt(),
                    binding.eAdditionalEmailTitle.text.toString(),
                    binding.tAdditionalRemarksContent.text.toString(),
                    userPictureUri?.path
                )
            }
        }

        return newPerson
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        val CURRENT_PERSON = Person.exampleStudent as Person
    }
}