package com.example.chatapp.adapter

import android.R
import android.R.layout
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import com.google.android.play.integrity.internal.c


class ContactAdapter(
    context: Context?,
    layout: Int,
    c: Cursor?,
    from: Array<out String>?,
    to: IntArray?,
) : SimpleCursorAdapter(context, layout, c, from, to) {

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        super.bindView(view, context, cursor)

        }
    }

