package english.lessons.inlesson.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import english.lessons.inlesson.R
import english.lessons.inlesson.databinding.GameQuizFragmentBinding
import english.lessons.inlesson.ui.Case.backPressType

class GameQuizFragment : Fragment() {

    private lateinit var binding: GameQuizFragmentBinding
    private var index = 1
    private var correctAnswers = 0

    private lateinit var image1: String
    private lateinit var image2: String
    private lateinit var image3: String
    private lateinit var image4: String

    private var store = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameQuizFragmentBinding.bind(
            inflater.inflate(
                R.layout.game_quiz_fragment, container, false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.child("Game").child("3").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    setQuestion(
                        it.result.child("tasks").child(index.toString()).child("question").value
                            .toString()
                    )
                }
            }
        onClick()
        backPressType = 0
    }

    private fun setQuestion(ques: String) {
        if (index <= 20) {
            binding.questionTxt.text = ques
            store.child("Game").child("3").child("tasks").child(index.toString()).child("answers")
                .child("0")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        image1 = it.result.value.toString()
                        Glide.with(binding.answer1Btn)
                            .load(image1)
                            .into(binding.answer1Btn)
                    }
                }
            store.child("Game").child("3").child("tasks").child(index.toString()).child("answers")
                .child("1")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        image2 = it.result.value.toString()
                        Glide.with(binding.answer2Btn)
                            .load(image2)
                            .into(binding.answer2Btn)
                    }
                }
            store.child("Game").child("3").child("tasks").child(index.toString()).child("answers")
                .child("2")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        image3 = it.result.value.toString()
                        Glide.with(binding.answer3Btn)
                            .load(image3)
                            .into(binding.answer3Btn)
                    }
                }
            store.child("Game").child("3").child("tasks").child(index.toString()).child("answers")
                .child("3")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        image4 = it.result.value.toString()
                        Glide.with(binding.answer4Btn)
                            .load(image4)
                            .into(binding.answer4Btn)
                    }
                }

            index++
        }
    }

    private fun onClick() {
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
    }

    private fun makeAnswer(num: Int) {
        store.child("Game").child("3").child("tasks").child((index-1).toString()).get()
            .addOnCompleteListener { r->
                if (r.isSuccessful) {
                    if (num == r.result.child("help").value.toString().toInt()) {
                        correctAnswers++
                    }
                    if (index != 21) {
                        store.child("Game").child("3").get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    setQuestion(
                                        it.result.child("tasks").child(index.toString()).child("question").value
                                            .toString()
                                    )
                                }
                            }
                    } else {
                        val dialog = MaterialDialog(requireActivity())
                            .title(text = "Ваш результат: $correctAnswers")
                            .positiveButton(text = "play again") {
                                correctAnswers = 0
                                index = 1
                                it.cancel()
                                store.child("Game").child("3").get()
                                    .addOnCompleteListener {r->
                                        if (r.isSuccessful) {
                                            setQuestion(
                                                r.result.child("tasks").child(index.toString()).child("question").value
                                                    .toString()
                                            )
                                        }
                                    }
                            }
                            .negativeButton(text = "main menu") {
                                correctAnswers = 0
                                it.cancel()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                            .cancelOnTouchOutside(false)

                        dialog.show { }
                    }
                }
            }
    }
}