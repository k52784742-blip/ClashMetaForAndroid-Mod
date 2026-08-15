package com.github.kr328.clash.design.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.design.databinding.AdapterLogMessageBinding
import com.github.kr328.clash.design.util.layoutInflater

class LogMessageAdapter(
    private val context: Context,
    private val copy: (LogMessage) -> Unit,
) :
    RecyclerView.Adapter<LogMessageAdapter.Holder>() {
    class Holder(val binding: AdapterLogMessageBinding) : RecyclerView.ViewHolder(binding.root)

    var messages: List<LogMessage> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            AdapterLogMessageBinding
                .inflate(context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = messages[position]

        holder.binding.message = current

        // 日志等级着色：INFO 青 / WARNING 橙 / ERROR 红 / 其他次要色
        // （日志页高频刷新，不加入场动画避免闪烁）
        val color = when (current.level) {
            LogMessage.Level.Error -> 0xFFE53935.toInt()
            LogMessage.Level.Warning -> 0xFFFB8C00.toInt()
            LogMessage.Level.Info -> 0xFF00ACC1.toInt()
            LogMessage.Level.Debug -> 0xFF9E9E9E.toInt()
            else -> ContextCompat.getColor(context, android.R.color.darker_gray)
        }
        holder.binding.levelView.setTextColor(color)

        holder.binding.root.setOnLongClickListener {
            copy(current)

            true
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}