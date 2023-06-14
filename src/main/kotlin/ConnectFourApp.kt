import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class ConnectFourApp : Application() {
    override fun start(stage: Stage) {
        stage.title = "Connect Four Game"
        val model = Model
        val view = View(model)
        val vbox = VBox(view)
        stage.scene = Scene(vbox)

        stage.width = 1000.0
        stage.height = 800.0
        stage.isResizable = false
        stage.show()
    }
}