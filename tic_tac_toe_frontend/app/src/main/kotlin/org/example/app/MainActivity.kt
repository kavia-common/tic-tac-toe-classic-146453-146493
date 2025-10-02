package org.example.app

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout

/**
 * PUBLIC_INTERFACE
 * MainActivity is the single screen of the Tic Tac Toe app.
 * It renders a centered 3x3 board and controls below for starting/restarting the game.
 * It manages game state for two local players (X and O), checks win/draw conditions,
 * and provides a clean, modern "Ocean Professional" themed experience.
 */
class MainActivity : Activity() {

    // Board represented as a 9-length array; each cell is 'X', 'O', or null
    private val board: Array<Char?> = arrayOfNulls(9)

    // Current player: 'X' or 'O'
    private var currentPlayer: Char = 'X'

    // True when a game has been started
    private var gameStarted: Boolean = false

    // Cache view references
    private lateinit var statusText: TextView
    private lateinit var startButton: Button
    private lateinit var restartButton: Button
    private lateinit var grid: GridLayout
    private lateinit var cellButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        restartButton = findViewById(R.id.restartButton)
        grid = findViewById(R.id.gridBoard)

        // Collect the 9 buttons in index order
        cellButtons = listOf(
            findViewById(R.id.cell_0),
            findViewById(R.id.cell_1),
            findViewById(R.id.cell_2),
            findViewById(R.id.cell_3),
            findViewById(R.id.cell_4),
            findViewById(R.id.cell_5),
            findViewById(R.id.cell_6),
            findViewById(R.id.cell_7),
            findViewById(R.id.cell_8),
        )

        // Attach listeners
        startButton.setOnClickListener { startGame() }
        restartButton.setOnClickListener { resetBoard(hard = true) }

        cellButtons.forEachIndexed { index: Int, button: Button ->
            button.setOnClickListener { onCellClicked(index, button) }
        }

        // Initial UI state
        updateBoardUI()
        updateStatusText()
        setBoardEnabled(false)
    }

    /**
     * PUBLIC_INTERFACE
     * Start a new game if not started; prepares the board for play and enables interactions.
     */
    private fun startGame() {
        if (!gameStarted) {
            gameStarted = true
            resetBoard(hard = false)
            setBoardEnabled(true)
            updateStatusText()
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Handle a tap on a board cell. Places the current player's mark if valid,
     * checks for a win/draw, and swaps turn accordingly.
     */
    private fun onCellClicked(index: Int, button: Button) {
        if (!gameStarted) return
        if (board[index] != null) return // already taken

        board[index] = currentPlayer
        // Set icon for the move and disable button
        applyIconForMark(button, currentPlayer)
        button.isEnabled = false

        // Check result
        val winner = checkWinner()
        if (winner != null) {
            statusText.text = getString(R.string.win_format, playerLabel(winner))
            endGame()
            return
        }

        if (isDraw()) {
            statusText.setText(R.string.draw)
            endGame()
            return
        }

        // Next player's turn
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        updateStatusText()
    }

    /**
     * Reset the board for a new round.
     * If hard=true, also sets gameStarted to true and enables the board.
     */
    private fun resetBoard(hard: Boolean) {
        for (i in board.indices) board[i] = null
        currentPlayer = 'X'
        updateBoardUI()
        if (hard) {
            gameStarted = true
            setBoardEnabled(true)
        }
        updateStatusText()
    }

    // Update all 9 buttons based on board state
    private fun updateBoardUI() {
        cellButtons.forEachIndexed { index, btn ->
            val symbol = board[index]
            if (symbol == null) {
                // Clear text and icons for empty cells
                btn.text = ""
                btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                btn.contentDescription = null
            } else {
                // Apply the appropriate chess piece icon
                applyIconForMark(btn, symbol)
            }
            btn.isEnabled = symbol == null && gameStarted
        }
    }

    // Enable or disable all board cells
    private fun setBoardEnabled(enabled: Boolean) {
        cellButtons.forEach { button: Button ->
            if (enabled) {
                // Only enable empty cells (no icon/text)
                val hasDrawable = button.compoundDrawables.any { it != null }
                button.isEnabled = !hasDrawable
            } else {
                button.isEnabled = false
            }
        }
    }

    // Update the status text with whose turn it is or show "Start Game"
    private fun updateStatusText() {
        if (!gameStarted) {
            statusText.setText(R.string.start_game)
        } else {
            statusText.text = getString(R.string.turn_format, playerLabel(currentPlayer))
        }
    }

    // Return a friendly label for a player mark
    private fun playerLabel(mark: Char): String {
        return if (mark == 'X') getString(R.string.player_x) else getString(R.string.player_o)
    }

    // Determine if there's a winner; returns 'X', 'O' or null
    private fun checkWinner(): Char? {
        val wins = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        for (line in wins) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a != null && a == b && b == c) {
                return a
            }
        }
        return null
    }

    // Returns true if all cells are filled with no winner
    private fun isDraw(): Boolean {
        return board.all { it != null }
    }

    // Finish the game and disable the board
    private fun endGame() {
        gameStarted = false
        setBoardEnabled(false)
    }

    /**
     * Apply the appropriate chess icon to a board button for the given mark.
     * X -> Knight, O -> Queen. Clears button text for visual cleanliness.
     */
    private fun applyIconForMark(button: Button, mark: Char) {
        button.text = "" // do not use letters

        val drawable: Drawable? = if (mark == 'X') {
            ContextCompat.getDrawable(this, R.drawable.ic_chess_knight)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_chess_queen)
        }

        // Place the icon centered by using it as top compound drawable
        button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

        // Accessibility content description
        button.contentDescription = if (mark == 'X') {
            getString(R.string.player_x)
        } else {
            getString(R.string.player_o)
        }
    }
}
