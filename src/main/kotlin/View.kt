import javafx.animation.Interpolator
import javafx.animation.TranslateTransition
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.util.Duration

class View(model: Model) : Pane(), IView {
    private var startButton = Button("Click here to start game!")
    private val player1 = Label("Player #1")
    private val player2 = Label("Player #2")
    private var model = model
    private var dragInfo = DragInfo()
    private var dragInfo2 = DragInfo()
    private val gridNode = ImageView(javaClass.getResource("/grid_8x7.png").toString()).apply {
        fitWidth = 600.0
        isPreserveRatio = true
        isSmooth = true
        x = 200.0
        y = 170.0
    }
    private var mygrid: MutableMap<Int, Int> = mutableMapOf()
    private var win = Player.NONE
    private var draw = false


    override fun updateView() {
        println("View: updateView")
        this.model = model
        mygrid = model.mygrid
        win = model.onGameWin.value
        draw = model.onGameDraw.value
        if (win == Player.ONE) {
            playerWon("#1")
        } else if (win == Player.TWO) {
            playerWon("#2")
        }
        if (draw) {
            val label = Label("DRAW").apply {
                prefWidth = 300.0
                prefHeight = 100.0
                layoutX = 350.0
                layoutY = 350.0
                alignment = Pos.CENTER
//                font = Font("Arial", 30.0)
                background = Background(BackgroundFill(Color.AQUA, null, null))
            }
            children.add(label)
        }
    }

    fun playerWon(player: String) {
        val label = Label("Player $player WON").apply {
            prefWidth = 300.0
            prefHeight = 100.0
            layoutX = 350.0
            layoutY = 350.0
            alignment = Pos.CENTER
//            font = Font("Arial", 30.0)
            background = Background(BackgroundFill(Color.GREEN, null, null))
        }
        children.add(label)
    }
    fun playerOne() {
        var isDropped = false
        val piece = Circle(100.0, 100.0, 29.0, Color.RED).apply {
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                dragInfo = DragInfo(this, it.sceneX, it.sceneY, translateX, translateY)
            }
            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (!isDropped) {
                    val x = dragInfo.initialX + it.sceneX - dragInfo.anchorX
                    val y = dragInfo.initialY + it.sceneY - dragInfo.anchorY
                    if (-36.0 <= y && y <= 35.0 && -70 <= x && x <= 870.0) {
                        if (100.0 <= x && x < 175.0) {
                            translateX = 137.5
                        } else if (175.0 <= x && x < 250.0) {
                            translateX = 212.5
                        } else if (250.0 <= x && x < 325.0) {
                            translateX = 287.5
                        } else if (325.0 <= x && x < 400.0) {
                            translateX = 362.5
                        } else if (400.0 <= x && x < 475.0) {
                            translateX = 437.5
                        } else if (475.0 <= x && x < 550.0) {
                            translateX = 512.5
                        } else if (550.0 <= x && x < 625.0) {
                            translateX = 587.5
                        } else if (625.0 <= x && x < 700.0) {
                            translateX = 662.5
                        } else {
                            translateX = dragInfo.initialX + it.sceneX - dragInfo.anchorX
                        }
                        translateY = dragInfo.initialY + it.sceneY - dragInfo.anchorY
                    }
                }
            }
            addEventFilter(MouseEvent.MOUSE_RELEASED) {
                val x = dragInfo.initialX + it.sceneX - dragInfo.anchorX
                val y = dragInfo.initialY + it.sceneY - dragInfo.anchorY
                print("x: $x\n y: $y\n")
                var n = 0
                if (!isDropped) {
                    if (100.0 <= x && x < 175.0) {
                        n = 0
                    } else if (175.0 <= x && x < 250.0) {
                        n = 1
                    } else if (250.0 <= x && x < 325.0) {
                        n = 2
                    } else if (325.0 <= x && x < 400.0) {
                        n = 3
                    } else if (400.0 <= x && x < 475.0) {
                        n = 4
                    } else if (475.0 <= x && x < 550.0) {
                        n = 5
                    } else if (550.0 <= x && x < 625.0) {
                        n = 6
                    } else if (625.0 <= x && x < 700.0) {
                        n = 7
                    }

                    TranslateTransition(Duration.millis(200.0), this).apply{
                        byY = scene.height - centerY - translateY - radius
                        toY = 558.0 - (mygrid[n]?.times(75.0)!!)
                        interpolator = Interpolator.EASE_IN
                        if (100.0 > x || x >= 700.0 || mygrid[n] == 7) {
                            toX = 0.0
                            toY = 0.0
                        }
                    }.play()

                    if (100.0 <= x && x < 700.0 && mygrid[n]!! < 7) {
                        isDropped = true
                        player1.isDisable = true
                        dragInfo = DragInfo()
                        children.remove(gridNode)
                        children.add(gridNode)
                        model.dropPiece(n)
                        updateView()
                        if (win == Player.NONE && !draw) {
                            player2.isDisable = false
                            playerTwo()
                        }
                    }
                }
            }
        }
        children.add(piece)
    }

    fun playerTwo() {
        var isDropped = false
        val pieceTwo = Circle(900.0, 100.0, 29.0, Color.YELLOW).apply {
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                dragInfo2 = DragInfo(this, it.sceneX, it.sceneY, translateX, translateY)
            }
            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (!isDropped) {
                    val x = dragInfo2.initialX + it.sceneX - dragInfo2.anchorX
                    val y = dragInfo2.initialY + it.sceneY - dragInfo2.anchorY

                    if (-36.0 <= y && y <= 35.0 && -870.0 < x && x <= 70.0) {
                        if (-700.0 <= x && x < -625.0) {
                            translateX = -662.5
                        } else if (-625.0 <= x && x < -550.0) {
                            translateX = -587.5
                        } else if (-550.0 <= x && x < -475.0) {
                            translateX = -512.5
                        } else if (-475.0 <= x && x < -400.0) {
                            translateX = -437.5
                        } else if (-400.0 <= x && x < -325.0) {
                            translateX = -362.5
                        } else if (-325.0 <= x && x < -250.0) {
                            translateX = -287.5
                        } else if (-250.0 <= x && x < -175.0) {
                            translateX = -212.5
                        } else if (-175.0 <= x && x < -100.0) {
                            translateX = -137.5
                        } else {
                            translateX = dragInfo2.initialX + it.sceneX - dragInfo2.anchorX
                        }
                        translateY = dragInfo2.initialY + it.sceneY - dragInfo2.anchorY
                    }
                }

            }
            addEventFilter(MouseEvent.MOUSE_RELEASED) {
                val x = dragInfo2.initialX + it.sceneX - dragInfo2.anchorX
                var n = 0
                if (!isDropped) {
                    if (-700.0 <= x && x < -625.0) {
                        n = 0
                    } else if (-625.0 <= x && x < -550.0) {
                        n = 1
                    } else if (-550.0 <= x && x < -475.0) {
                        n = 2
                    } else if (-475.0 <= x && x < -400.0) {
                        n = 3
                    } else if (-400.0 <= x && x < -325.0) {
                        n = 4
                    } else if (-325.0 <= x && x < -250.0) {
                        n = 5
                    } else if (-250.0 <= x && x < -175.0) {
                        n = 6
                    } else if (-175.0 <= x && x < -100.0) {
                        n = 7
                    }
                    TranslateTransition(Duration.millis(200.0), this).apply{
                        byY = scene.height - centerY - translateY - radius
                        toY = 558.0 - (mygrid[n]?.times(75.0)!!)
                        interpolator = Interpolator.EASE_IN
                        if (-700.0 > x || x >= -100.0 || mygrid[n] == 7) {
                            toX = 0.0
                            toY = 0.0
                        }
                    }.play()
                    if (-700.0 <= x && x < -100.0 && mygrid[n]!! < 7) {
                        isDropped = true
                        player2.isDisable = true
                        dragInfo2 = DragInfo()
                        children.remove(gridNode)
                        children.add(gridNode)
                        model.dropPiece(n)
                        updateView()
                        if (win == Player.NONE && !draw) {
                            player1.isDisable = false
                            playerOne()
                        }
                    }
                }
            }
        }
        children.add(pieceTwo)
    }


    init {
        mygrid = model.mygrid

        startButton.apply {
            prefWidth = 200.0
            layoutX = 400.0
            layoutY = 70.0
        }
        player1.apply {
            layoutX = 20.0
            layoutY = 10.0
            isDisable = true
        }
        player2.apply {
            layoutX = 930.0
            layoutY = 10.0
            isDisable = true
        }
        
        children.addAll(player1, startButton, player2)
        children.add(gridNode)

        startButton.setOnMouseClicked {
            startButton.isVisible = false
            player1.isDisable = false
            model.startGame()
            playerOne()
        }
    }
}