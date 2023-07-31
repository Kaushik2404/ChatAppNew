package com.example.chatapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.chatapp.R
import com.example.chatapp.interfacefile.OnClickDilogFile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FileDialog(val onclickFile: OnClickDilogFile) : BottomSheetDialogFragment() {

    lateinit var file:LinearLayout
    lateinit var camera:LinearLayout
    lateinit var galary:LinearLayout
    lateinit var location:LinearLayout
    lateinit var audio:LinearLayout
    lateinit var contact:LinearLayout




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.file_dialog,container,false)

        file=view.findViewById(R.id.file)
        camera=view.findViewById(R.id.camera)
        galary=view.findViewById(R.id.galary)
        location=view.findViewById(R.id.location)
        audio=view.findViewById(R.id.audio)
        contact=view.findViewById(R.id.contact)

//       initvar(view)
        onclick()
        return view

    }

    private fun onclick() {
        galary.setOnClickListener {
            onclickFile.onClickGalary()
        }
        camera.setOnClickListener {
            onclickFile.onClickCamera()
        }

    }

    private fun getImageId() {

    }

    private fun initvar(view: View?) {

//         file=view.findViewById(R.id.file)
//         camera=view.findViewById(R.id.camera)
//         galary=view.findViewById(R.id.galary)
//         location=view.findViewById(R.id.location)
//         audio=view.findViewById(R.id.audio)
//         contact=view.findViewById(R.id.contact)

    }
}