import androidx.compose.ui.window.ComposeUIViewController
import com.rafambn.framebarcomposeapp.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
