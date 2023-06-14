import javafx.scene.Node

data class DragInfo(var target : Node? = null,
                    var anchorX: Double = 0.0, var anchorY: Double = 0.0,
                    var initialX: Double = 0.0, var initialY: Double = 0.0)

