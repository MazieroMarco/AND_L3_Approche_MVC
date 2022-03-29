package heig.and.labo3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
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

        // Fills the fields
        fillFileds()
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
                binding.groupMainSpecificWorkers.visibility = View.INVISIBLE
            }
            R.id.rbEmployee -> {
                binding.groupMainSpecificStudents.visibility = View.INVISIBLE
                binding.groupMainSpecificWorkers.visibility = View.VISIBLE
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
        val personData = CURRENT_PERSON
        binding.eMainBaseName.setText(personData.name)
        binding.eMainBaseFirstname.setText(personData.firstName)
        val dateFormat = SimpleDateFormat("dd.mm.yyyy")
        binding.eMainBaseBirthday.setText(dateFormat.format(personData.birthDay.time))
        val natId = this.resources.getStringArray(R.array.nationalities).indexOf(personData.nationality)
        binding.sNationalities.setSelection(natId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            binding.imageAdditionalPicture.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
        val CURRENT_PERSON = Person.exampleStudent
    }

}