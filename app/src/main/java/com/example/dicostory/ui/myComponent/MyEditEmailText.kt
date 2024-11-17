package com.example.dicostory.ui.myComponent

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import android.view.View
import com.example.dicostory.R

class MyEditEmailText @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
): AppCompatEditText(context, attr), View.OnTouchListener{

    init {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    error = context.getString(R.string.email_is_invalid)
                }
            }

            override fun afterTextChanged(p0: Editable?){}
        })
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}