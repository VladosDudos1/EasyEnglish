package english.lessons.inlesson.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import english.lessons.inlesson.R
import english.lessons.inlesson.databinding.GameItemBinding

class GameAdapter(private val onClickListener: OnClickListener,  val context: Context) : RecyclerView.Adapter<GameAdapter.GameVH>() {
    private lateinit var binding: GameItemBinding
    private lateinit var description: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameVH {
        binding = GameItemBinding.bind(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.game_item,
                    parent,
                    false
                )
        )
        return GameVH(binding.root)
    }

    override fun onBindViewHolder(holder: GameVH, position: Int) {
        when (position) {
            0 -> {
                Glide.with(binding.imgGame)
                    .load("https://firebasestorage.googleapis.com/v0/b/inlesson-d6a94.appspot.com/o/123.jpg?alt=media&token=4087994c-a3df-4b71-88a6-9a5e8c96a3fd")
                    .into(binding.imgGame)
                binding.gameNameTxt.text = context.getString(R.string.explain_the_picture)
            }
            1 -> {
                Glide.with(binding.imgGame)
                    .load("https://firebasestorage.googleapis.com/v0/b/inlesson-d6a94.appspot.com/o/1.jpg?alt=media&token=41675ade-3041-421a-9cb0-dcf2b6ae4f3c")
                    .into(binding.imgGame)
                binding.gameNameTxt.text = context.getString(R.string.explain_the_word)
            }
            2 -> {
                Glide.with(binding.imgGame)
                    .load("https://firebasestorage.googleapis.com/v0/b/inlesson-d6a94.appspot.com/o/game.png?alt=media&token=75386a36-633a-4418-86b1-ae4471579448")
                    .into(binding.imgGame)
                binding.gameNameTxt.text = context.getString(R.string.who_what_is_it)
            }
        }

        binding.gameLayout.setOnClickListener {
            onClickListener.click(position)
        }
        binding.imgInfo.setOnClickListener {
            when (position){
                0 -> description = context.getString(R.string.game1_description)
                1 -> description = context.getString(R.string.game2_description)
                2 -> description = context.getString(R.string.game3_description)
            }
            MaterialDialog(context)
                .title(text = context.getString(R.string.game_description))
                .message(text = description)
                .show()
        }
    }

    override fun getItemCount(): Int = 3

    interface OnClickListener{
        fun click(data: Int)
    }
    class GameVH(view: View): RecyclerView.ViewHolder(view)
}