package com.ms.lcw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lcw.lsdk.events.LCWChatEvents

/**
 * Entry-point activity. Displays a live-updating event dashboard that reflects
 * all SDK callbacks routed through [LCWChatEvents] LiveData. Chat configuration
 * and launch are handled in [ChatActivity].
 */
class HomeActivity : AppCompatActivity() {

    // ── Event value TextViews ─────────────────────────────────────────────────
    private lateinit var tvNewMessage: TextView
    private lateinit var tvAgentAssigned: TextView
    private lateinit var tvChatInitiated: TextView
    private lateinit var tvChatEnded: TextView
    private lateinit var tvChatRestored: TextView
    private lateinit var tvMinimized: TextView
    private lateinit var tvClosed: TextView
    private lateinit var tvBotSignIn: TextView
    private lateinit var tvError: TextView

    // ── Action buttons ────────────────────────────────────────────────────────
    private lateinit var btnOpenChat: Button
    private lateinit var btnClearLog: Button
    private lateinit var btnClearNewMessage: Button
    private lateinit var btnClearAgentAssigned: Button
    private lateinit var btnClearChatInitiated: Button
    private lateinit var btnClearChatEnded: Button
    private lateinit var btnClearChatRestored: Button
    private lateinit var btnClearMinimized: Button
    private lateinit var btnClearClosed: Button
    private lateinit var btnClearBotSignIn: Button
    private lateinit var btnClearError: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bindViews()
        setupButtons()
        observeChatEvents()
    }

    private fun bindViews() {
        tvNewMessage    = findViewById(R.id.tvNewMessage)
        tvAgentAssigned = findViewById(R.id.tvAgentAssigned)
        tvChatInitiated = findViewById(R.id.tvChatInitiated)
        tvChatEnded     = findViewById(R.id.tvChatEnded)
        tvChatRestored  = findViewById(R.id.tvChatRestored)
        tvMinimized     = findViewById(R.id.tvMinimized)
        tvClosed        = findViewById(R.id.tvClosed)
        tvBotSignIn     = findViewById(R.id.tvBotSignIn)
        tvError         = findViewById(R.id.tvError)

        btnOpenChat           = findViewById(R.id.btnOpenChat)
        btnClearLog           = findViewById(R.id.btnClearLog)
        btnClearNewMessage    = findViewById(R.id.btnClearNewMessage)
        btnClearAgentAssigned = findViewById(R.id.btnClearAgentAssigned)
        btnClearChatInitiated = findViewById(R.id.btnClearChatInitiated)
        btnClearChatEnded     = findViewById(R.id.btnClearChatEnded)
        btnClearChatRestored  = findViewById(R.id.btnClearChatRestored)
        btnClearMinimized     = findViewById(R.id.btnClearMinimized)
        btnClearClosed        = findViewById(R.id.btnClearClosed)
        btnClearBotSignIn     = findViewById(R.id.btnClearBotSignIn)
        btnClearError         = findViewById(R.id.btnClearError)
    }

    private fun setupButtons() {
        btnOpenChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Clear All — resets every row at once
        btnClearLog.setOnClickListener { clearAll() }

        // Per-row clear buttons
        btnClearNewMessage.setOnClickListener    { resetRow(tvNewMessage) }
        btnClearAgentAssigned.setOnClickListener { resetRow(tvAgentAssigned) }
        btnClearChatInitiated.setOnClickListener { resetRow(tvChatInitiated) }
        btnClearChatEnded.setOnClickListener     { resetRow(tvChatEnded) }
        btnClearChatRestored.setOnClickListener  { resetRow(tvChatRestored) }
        btnClearMinimized.setOnClickListener     { resetRow(tvMinimized) }
        btnClearClosed.setOnClickListener        { resetRow(tvClosed) }
        btnClearBotSignIn.setOnClickListener     { resetRow(tvBotSignIn) }
        btnClearError.setOnClickListener         { resetRow(tvError) }
    }

    private fun observeChatEvents() {
        // newMessage: fire-once — will NOT re-deliver on screen resume.
        // Safe to observe here because HomeActivity does not host the chat view.
        LCWChatEvents.newMessage.observe(this) { message ->
            val text = (message?.getProperty("content") ?: message)?.toString() ?: "null"
            Log.d(TAG, "newMessage: $text")
            updateEventView(tvNewMessage, text)
        }

        LCWChatEvents.agentAssigned.observe(this) { agentName ->
            Log.d(TAG, "agentAssigned: $agentName")
            updateEventView(tvAgentAssigned, agentName)
        }

        LCWChatEvents.chatInitiated.observe(this) {
            Log.d(TAG, "chatInitiated")
            updateEventView(tvChatInitiated, "true")
        }

        LCWChatEvents.chatEnded.observe(this) { byAgent ->
            val who = if (byAgent) "by agent" else "by customer"
            Log.d(TAG, "chatEnded: $who")
            updateEventView(tvChatEnded, who)
        }

        LCWChatEvents.chatRestored.observe(this) {
            Log.d(TAG, "chatRestored")
            updateEventView(tvChatRestored, "true")
        }

        LCWChatEvents.minimized.observe(this) {
            Log.d(TAG, "minimized")
            updateEventView(tvMinimized, "true")
        }

        LCWChatEvents.closed.observe(this) {
            Log.d(TAG, "closed")
            updateEventView(tvClosed, "true")
        }

        LCWChatEvents.botSignIn.observe(this) { content ->
            Log.d(TAG, "botSignIn: $content")
            updateEventView(tvBotSignIn, content)
        }

        LCWChatEvents.error.observe(this) { error ->
            val text = error?.errorMessage ?: "unknown error"
            Log.d(TAG, "error: $text")
            updateEventView(tvError, text, isError = true)
        }
    }

    /**
     * Sets event text and colors it:
     * - green for normal active events
     * - red for errors
     */
    private fun updateEventView(tv: TextView, value: String, isError: Boolean = false) {
        tv.text = value
        tv.setTextColor(
            ContextCompat.getColor(
                this,
                if (isError) R.color.lcw_txt_color_error else R.color.lcw_txt_color_green
            )
        )
    }

    /** Resets a single event row to the idle placeholder. */
    private fun resetRow(tv: TextView) {
        tv.text = "—"
        tv.setTextColor(ContextCompat.getColor(this, R.color.lcw_colorPreChatTextSubtitle))
    }

    /** Resets every event row at once. */
    private fun clearAll() {
        listOf(
            tvNewMessage, tvAgentAssigned, tvChatInitiated, tvChatEnded,
            tvChatRestored, tvMinimized, tvClosed, tvBotSignIn, tvError
        ).forEach { resetRow(it) }
    }

    companion object {
        private const val TAG = "###LCW_CHAT"
    }
}
