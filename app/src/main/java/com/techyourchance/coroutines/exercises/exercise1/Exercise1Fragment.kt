package com.techyourchance.coroutines.exercises.exercise1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.techyourchance.coroutines.R
import com.techyourchance.coroutines.common.BaseFragment
import com.techyourchance.coroutines.common.ThreadInfoLogger
import com.techyourchance.coroutines.home.ScreenReachableFromHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Exercise1Fragment : BaseFragment() {

    override val screenTitle get() = ScreenReachableFromHome.EXERCISE_1.description

    private lateinit var edtUserId: EditText
    private lateinit var btnGetReputation: Button

    private lateinit var getReputationEndpoint: GetReputationEndpoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getReputationEndpoint = compositionRoot.getReputationEndpoint
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_1, container, false)

        edtUserId = view.findViewById(R.id.edt_user_id)
        edtUserId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnGetReputation.isEnabled = !s.isNullOrEmpty() // if the text is null returns false else true
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnGetReputation = view.findViewById(R.id.btn_get_reputation)
        ///////////
        btnGetReputation.setOnClickListener {
            btnGetReputation.isEnabled = false

            //here could be main since getReputation func is working in another thread. But this works also.
            CoroutineScope(Dispatchers.Default).launch{
                logThreadInfo("button callback")
                val reputation = getReputationForUser(edtUserId.text.toString())
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "reputation: $reputation", Toast.LENGTH_SHORT).show()
                    btnGetReputation.isEnabled = true

                }
            }

        }

        return view
    }

    private suspend fun getReputationForUser(userId: String) : Int {
        logThreadInfo("getReputationForUser()")

        return withContext(Dispatchers.Default){
            getReputationEndpoint.getReputation(userId)

        }

    }

    private fun logThreadInfo(message: String) {
        ThreadInfoLogger.logThreadInfo(message)
    }

    companion object {
        fun newInstance(): Fragment {
            return Exercise1Fragment()
        }
    }
}