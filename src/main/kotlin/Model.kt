import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableObjectValue
import java.lang.Exception

/**
 * Model for Connect-Four.
 */
object Model {
    private val views: ArrayList<IView> = ArrayList()

    private fun updateAllViews() {
        for (view in views) {
            println("Model: notify View")
            view.updateView()
        }
    }

    /**
     * SimpleObjectValue notifies all listeners when its stored value is set. (This is different from a [SimpleObjectProperty] that only notifies al listeners when its stored value has changed.)
     */
    class SimpleObjectValue<T>(initialValue: T) : ObservableObjectValue<T> {
        private var value = initialValue
        private val invalidationListeners = mutableListOf<InvalidationListener?>()
        private val changeListeners = mutableListOf<ChangeListener<in T>?>()
        override fun addListener(listener: InvalidationListener?) { invalidationListeners.add(listener) }
        override fun addListener(listener: ChangeListener<in T>?) { changeListeners.add(listener) }
        override fun removeListener(listener: InvalidationListener?) { invalidationListeners.remove(listener) }
        override fun removeListener(listener: ChangeListener<in T>?) { changeListeners.remove(listener) }
        override fun getValue(): T { return value }
        override fun get(): T { return value }
        fun set(value: T) {
            invalidationListeners.forEach { it?.invalidated(this) }
            changeListeners.forEach { it?.changed(this, this.value, value) }
            this.value = value
        }
    }

    var mygrid: MutableMap<Int, Int> = mutableMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0)

    /**
     * Width of the grid in cells
     */
    const val width = 8

    /**
     * Height of the grid in cells
     */
    const val height = 7

    /**
     * Length of the pieces that have to be in a row (vertically, horizontally, or diagonally) to win the game
     */
    const val length = 4

    /**
     * The game grid
     */
    private val grid = Grid(width, height, length)

    /**
     * Property is changed if the game concluded with a win. The event contains the winning [Player].
     */
    val onGameWin = SimpleObjectProperty(Player.NONE)

    /**
     * Property is changed if the game concluded with a draw.
     */
    val onGameDraw = SimpleBooleanProperty(false)

    /**
     * Property is changed if a player can start their turn. Its value holds the value of player whose turn it currently is. The event contains the [Player] whose turn just began.
     */
    val onNextPlayer = SimpleObjectProperty(Player.NONE)

    /**
     * Value is changed after a piece has been dropped into a slot. Its value holds the last successfully placed piece or null if the placement was not successful. The event contains the [Piece] that was just placed or null if the placement was not successful.
     */
    val onPieceDropped = SimpleObjectValue<Piece?>(null)

    /**
     * Starts the game. Listen to [onNextPlayer] to receive notification about changing player and turns.
     */
    fun startGame() {
        onNextPlayer.value = Player.ONE
    }

    /**
     * Attempts to drop a piece into a given column. Listen to [onPieceDropped] to receive notification about the success of the action.
     * @param column the column
     */
    fun dropPiece(column: Int) {
        println(onNextPlayer.value)
        var i = mygrid[column]
        if (i != null) {
            mygrid[column] = i+1
        }

        if (onGameWin.value == Player.NONE && onGameDraw.value == false) {       // if game has not resolved yet ...
            onPieceDropped.set(grid.dropPiece(column, onNextPlayer.value))       // ... attempt to drop piece and notify listeners
            if (onPieceDropped.value != null) {                                  // if drop successful ...
                onGameWin.value = grid.hasWon()                                  // ... check resolution (win)
                val won = onGameWin.value
                print("haswon: $won\n")
                onGameDraw.value = grid.hasDraw()                                // ... check resolution (draw)
                val draw = onGameDraw.value
                print("hasdraw: $draw\n")
                if (onGameWin.value == Player.NONE && onGameDraw.value == false) // if neither ...
                    onNextPlayer.value = when(onNextPlayer.value) {              // ... set up next player
                        Player.ONE -> Player.TWO
                        Player.TWO -> Player.ONE
                        else -> throw Exception("Invalid game state in dropPiece: current player was Player.NONE")
                    }
            }
        }
        updateAllViews()
    }
}