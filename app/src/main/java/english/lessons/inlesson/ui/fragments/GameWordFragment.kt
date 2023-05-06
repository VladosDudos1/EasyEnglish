package english.lessons.inlesson.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import english.lessons.inlesson.R
import english.lessons.inlesson.databinding.GameWordFragmentBinding
import english.lessons.inlesson.ui.Case
import english.lessons.inlesson.ui.Case.backPressType
import java.util.*
import kotlin.random.Random

class GameWordFragment : Fragment() {

    private lateinit var binding: GameWordFragmentBinding
    private var store = FirebaseDatabase.getInstance().reference
    private var question = ""
    private var randomTask = setRandom().toString()

    private var isCorrect = false

    private lateinit var answer1: String
    private lateinit var answer2: String
    private lateinit var answer3: String
    private lateinit var answer4: String

    private lateinit var activityForDialogs: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameWordFragmentBinding.bind(
            inflater.inflate(
                R.layout.game_word_fragment, container, false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityForDialogs = requireActivity()

        onClick()
        startGame()
        backPressType = 2
    }


    private fun setQuestion() {
        store.child("Game").child("2").child("tasks").child(randomTask).child("answers").child("1")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    answer1 = it.result.value.toString()
                    binding.answer1Btn.text = answer1
                }
            }
        store.child("Game").child("2").child("tasks").child(randomTask).child("answers").child("2")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    answer2 = it.result.value.toString()
                    binding.answer2Btn.text = answer2
                }
            }
        store.child("Game").child("2").child("tasks").child(randomTask).child("answers").child("3")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    answer3 = it.result.value.toString()
                    binding.answer3Btn.text = answer3
                }
            }
        store.child("Game").child("2").child("tasks").child(randomTask).child("answers").child("4")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    answer4 = it.result.value.toString()
                    binding.answer4Btn.text = answer4
                }
            }
    }

    private fun onClick() {
        binding.helpBtn.setOnClickListener {
            store.child("Game").child("2").get()
                .addOnCompleteListener { r ->
                    MaterialDialog(activityForDialogs)
                        .title(text = "Prompt")
                        .cancelable(true)
                        .positiveButton(text = "Close prompt") {
                            it.cancel()
                        }
                        .message(
                            text = r.result.child("tasks").child(randomTask)
                                .child("help").value.toString()
                        )
                        .show()
                }
        }

        binding.answer1Btn.setOnClickListener {
            makeAnswer(0)
        }
        binding.answer2Btn.setOnClickListener {
            makeAnswer(1)
        }
        binding.answer3Btn.setOnClickListener {
            makeAnswer(2)
        }
        binding.answer4Btn.setOnClickListener {
            makeAnswer(3)
        }
        binding.answerBtn.setOnClickListener {
            if (!binding.etAnswer.text.toString().lowercase()
                    .contains(question.lowercase()) && binding.etAnswer.text.toString()
                    .isNotEmpty()
            ) {
                store.child("room2").child("resultFirst").setValue(binding.etAnswer.text.toString())
                val dialog = MaterialDialog(activityForDialogs)
                    .cancelable(false)
                    .title(text = "Thank you, waiting to the next player")
                    .negativeButton(text = "Leave") {
                        activityForDialogs.supportFragmentManager.popBackStack()
                    }
                dialog.show { }

                val timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    var title = ""
                    override fun run() {
                        store.child("room2").child("resultSecond").get()
                            .addOnCompleteListener { f ->
                                store.child("room2").child("resultSecond")
                                    .get()
                                    .addOnCompleteListener { r ->
                                        isCorrect =
                                            f.result.value.toString() == question
                                        if (r.result.value.toString().isNotEmpty()) {
                                            dialog.cancel()
                                            title = if (isCorrect) {
                                                "The player got you!"
                                            } else {
                                                "The player did not understand you"
                                            }
                                            timer.cancel()
                                            MaterialDialog(activityForDialogs)
                                                .cancelable(false)
                                                .title(text = title)
                                                .negativeButton(text = "Leave") {
                                                    it.cancel()
                                                    activityForDialogs.supportFragmentManager.popBackStack()
                                                    resultOfGame()
                                                }
                                                .show()
                                        }
                                    }
                            }
                    }
                }, 1500, 2000)
            } else Toast.makeText(
                activityForDialogs,
                "Answer can`t contain a question word or be blank",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun makeAnswer(num: Int) {
        when (num) {
            0 -> answerRes(answer1)
            1 -> answerRes(answer2)
            2 -> answerRes(answer3)
            3 -> answerRes(answer4)
        }
    }

    private fun answerRes(ans: String){
        store.child("room2").child("resultSecond").setValue(ans)
        val title = if (ans == question) "Success, you`re right" else "Loose, you`re incorrect"
        MaterialDialog(activityForDialogs)
            .title(text = title)
            .cancelable(false)
            .positiveButton(text = "Close the game") {
                it.cancel()
                activityForDialogs.supportFragmentManager.popBackStack()
            }
            .show { }
        Handler().postDelayed({ resultOfGame() }, 2000)
    }

    private fun setRandom(): Int {
        return Random.nextInt(1, 21)
    }

    private fun startGame() {
        store.child("Game").child("2").get()
            .addOnCompleteListener(activityForDialogs) {
                if (it.isSuccessful) {
                    store.child("room2").get()
                        .addOnCompleteListener(activityForDialogs) { r ->
                            if (r.isSuccessful) {
                                if (r.result.child("isEmpty").value.toString().toBoolean()) {
                                    vision1()
                                    question = it.result.child("tasks").child(randomTask)
                                        .child("question").value.toString()
                                    store.child("room2").child("isEmpty").setValue(false)
                                    store.child("room2").child("idGame").setValue(randomTask)
                                    binding.questionTxt.text = it.result.child("tasks").child(randomTask)
                                        .child("question").value.toString()
                                } else {
                                    vision2()
                                    randomTask = r.result.child("idGame").value.toString()
                                    binding.questionTxt.text = "Waiting the first player"
                                    var resF = r.result.child("resultFirst").value.toString()
                                    setQuestion()
                                    question = it.result.child("tasks").child(randomTask)
                                        .child("question").value.toString()
                                    val timer = Timer()
                                    timer.scheduleAtFixedRate(object : TimerTask() {
                                        override fun run() {
                                            if (resF.isEmpty()) {
                                                store.child("room2").get()
                                                    .addOnCompleteListener { t ->
                                                        resF =
                                                            t.result.child("resultFirst").value.toString()
                                                    }
                                            } else {
                                                activityForDialogs.runOnUiThread {
                                                    binding.questionTxt.text = resF
                                                    binding.answer1Btn.isEnabled = true
                                                    binding.answer2Btn.isEnabled = true
                                                    binding.answer3Btn.isEnabled = true
                                                    binding.answer4Btn.isEnabled = true
                                                }
                                                timer.cancel()
                                            }
                                        }
                                    }, 200, 1800)
                                }
                            }
                        }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        resultOfGame()
    }

    private fun resultOfGame() {
        store.child("room2").child("isEmpty").setValue(true)
        store.child("room2").child("resultFirst").setValue("")
        store.child("room2").child("resultSecond").setValue("")
        store.child("room2").child("idGame").setValue("")
    }

    private fun vision1() {
        binding.etAnswer.visibility = View.VISIBLE
        binding.answerBtn.visibility = View.VISIBLE
        binding.answer1Btn.visibility = View.GONE
        binding.answer2Btn.visibility = View.GONE
        binding.answer3Btn.visibility = View.GONE
        binding.answer4Btn.visibility = View.GONE
        binding.helpBtn.visibility = View.VISIBLE
    }

    private fun vision2() {
        binding.etAnswer.visibility = View.INVISIBLE
        binding.answerBtn.visibility = View.INVISIBLE

        binding.answer1Btn.visibility = View.VISIBLE
        binding.answer2Btn.visibility = View.VISIBLE
        binding.answer3Btn.visibility = View.VISIBLE
        binding.answer4Btn.visibility = View.VISIBLE

        binding.helpBtn.visibility = View.GONE
    }
}