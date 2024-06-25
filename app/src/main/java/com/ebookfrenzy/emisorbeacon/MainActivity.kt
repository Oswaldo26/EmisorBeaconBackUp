package com.ebookfrenzy.emisorbeacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var beaconEmitter: BeaconEmitter
    private var isAdvertising by mutableStateOf(false)

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Todos los permisos han sido otorgados
            beaconEmitter.startAdvertising()
            isAdvertising = true
        } else {
            // Algunos permisos no han sido otorgados, maneja el caso adecuadamente
            // Por ejemplo, muestra un mensaje al usuario explicando la necesidad de los permisos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beaconEmitter = BeaconEmitter(this)

        setContent {
            BeaconEmitterApp()
        }

        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    private fun hasPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(requiredPermissions)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun BeaconEmitterApp() {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Beacon Emitter") })
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            if (isAdvertising) {
                                beaconEmitter.stopAdvertising()
                                isAdvertising = false
                            } else {
                                if (hasPermissions()) {
                                    beaconEmitter.startAdvertising()
                                    isAdvertising = true
                                } else {
                                    requestPermissions()
                                }
                            }
                        }) {
                            Text(if (isAdvertising) "Stop Advertising" else "Start Advertising")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Advertising status: ${if (isAdvertising) "Active" else "Inactive"}")
                    }
                }
            }
        )
    }
}