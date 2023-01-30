package com.example.loadfluttermodule

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.loadfluttermodule.pigeon_generated.PigeonModelFlutterApi
import com.example.loadfluttermodule.pigeon_generated.PigeonModelHostApi
import com.example.loadfluttermodule.ui.theme.LoadFlutterModuleTheme
import io.flutter.FlutterInjector
import io.flutter.embedding.android.ExclusiveAppComponent
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.platform.PlatformPlugin

class MainActivity : ComponentActivity(), ExclusiveAppComponent<Activity> {
    private lateinit var engine: FlutterEngine
    private var platformPlugin: PlatformPlugin? = null
    private val viewModel: MyViewModel by viewModels()

    // Implementing Host api interface
    private inner class MyPigeonModel : PigeonModelHostApi.PigeonModelApi {

        // Receiving data from flutter side
        override fun activateSubmissionButton(enabled: Boolean) {
            Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_SHORT).show()

            // Updating UI using viewmodel
            viewModel.isButtonEnabled.value = true
        }

        // Sending ready data to flutter side
        override fun getPerson(): PigeonModelHostApi.Person {
            val person = PigeonModelHostApi.Person()
            person.name = "Ashiq"
            person.age = 31

            return person
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContent {
            val isButtonEnabled = viewModel.isButtonEnabled.value

            LoadFlutterModuleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TopAppBar {}
                        Text(text = "Hello from android")
                        Spacer(modifier = Modifier.height(20.dp))

                        // Loading flutter view
                        Column(modifier = Modifier.weight(1f)) {
                            LoadFlutterView(engine)
                        }

                        if (isButtonEnabled)
                            Button(onClick = { }) {
                                Text(text = "Click me")
                            }
                    }
                }
            }
        }
    }

    private fun init() {
        engine = FlutterEngine(this)
        engine.dartExecutor.executeDartEntrypoint(

            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "nativeLoad"
            )
        )

        engine.activityControlSurface.attachToActivity(this, this.lifecycle)

        // Initializing our flutter api from generated java file
        val flutterApi = PigeonModelFlutterApi.PigeonFlutterApi(engine.dartExecutor.binaryMessenger)

        // Sending employee data to flutter side
        val emp = PigeonModelFlutterApi.Employee()
        emp.name = "Legend Ashiq"
        flutterApi.setEmployee(emp) {}

        // Binding your host api class with your flutter engine
        PigeonModelHostApi.PigeonModelApi.setup(
            engine.dartExecutor.binaryMessenger,
            MyPigeonModel()
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        detachFlutterView()
    }

    private fun detachFlutterView() {
        engine.activityControlSurface.detachFromActivity()
        engine.lifecycleChannel.appIsDetached()
        platformPlugin?.destroy()
        platformPlugin = null
    }

    override fun detachFromFlutterEngine() {
        detachFlutterView()
    }

    override fun getAppComponent() = this

    override fun onResume() {
        super.onResume()
        engine.lifecycleChannel.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        engine.lifecycleChannel.appIsPaused()
    }

    override fun onBackPressed() {
        engine.navigationChannel.popRoute()
    }
}

@Composable
fun LoadFlutterView(engine: FlutterEngine = FlutterEngine(LocalContext.current)) {
    AndroidView(factory = { ctx ->
        FlutterView(ctx).apply {
            attachToFlutterEngine(engine)
        }
    }) {}
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoadFlutterModuleTheme {
        LoadFlutterView()
    }
}