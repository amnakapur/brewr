package hu.ait.brewrapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.ait.brewrapp.ScrollingActivity
import hu.ait.brewrapp.data.AppDatabase
import hu.ait.brewrapp.touch.BrewrTouchHelperCallback
import kotlin.concurrent.thread
import hu.ait.brewrapp.data.DayInfo
import hu.ait.brewrapp.databinding.DayInfoRowBinding
import hu.ait.brewrapp.touch.BrewrTouchHelperCallback


import hu.bme.todorecyclerview.data.AppDatabase
import hu.bme.todorecyclerview.data.Todo
import hu.bme.todorecyclerview.databinding.TodoRowBinding
import hu.bme.todorecyclerview.touch.TodoTouchHelperCallback

class BrewrAdapter : ListAdapter<Brewr, BrewrAdapter.ViewHolder>, BrewrTouchHelperCallback {

    val context: Context

    constructor(context: Context) : super(TodoDiffCallback()) {
        this.context = context

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoRowBinding = TodoRowBinding.inflate(
            LayoutInflater.from(context),
            parent, false)
        return ViewHolder(todoRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTodo = getItem(holder.adapterPosition)
        holder.bind(currentTodo)

        holder.todoRowBinding.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.todoRowBinding.btnEdit.setOnClickListener {
            // Edit...
            (context as ScrollingActivity).showEditDialog(currentTodo)
        }

        holder.todoRowBinding.cbDone.setOnClickListener {
            currentTodo.done = holder.todoRowBinding.cbDone.isChecked
            thread {
                AppDatabase.getInstance(context).todoDao().updateTodo(currentTodo)
            }
        }
    }


    fun deleteTodo(index: Int) {
        thread {
            AppDatabase.getInstance(context).todoDao().deleteTodo(getItem(index))
        }
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(val todoRowBinding: TodoRowBinding) : RecyclerView.ViewHolder(todoRowBinding.root) {
        fun bind(todo: Todo) {
            todoRowBinding.tvDate.text = todo.createDate
            todoRowBinding.cbDone.text = todo.title
            todoRowBinding.cbDone.isChecked = todo.done
        }
    }
}

    class BrewrDiffCallback : DiffUtil.ItemCallback<Brewr>() {
        override fun areItemsTheSame(oldItem: Brewr, newItem: Brewr): Boolean {
            return oldItem._id == newItem._id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Brewr, newItem: Brewr): Boolean {
            return oldItem == newItem
        }
    }