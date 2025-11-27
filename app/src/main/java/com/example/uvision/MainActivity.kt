package com.example.uvision

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

// --- Cores Exatas do Design TSX/Figma ---
val NavyBlue = Color(0xFF152E4C)     // #152e4c
val YellowHighlight = Color(0xFFFDB813) // #fdb813
val OrangeAlert = Color(0xFFF5732F)    // #F5732F
val TextGray = Color(0xFF7C8D9C)       // #7c8d9c
val BgLight = Color(0xFFFFFFFF)        // bg-white
val GreenSafe = Color(0xFF22C55E)      // bg-green-500
val RedDanger = Color(0xFFDC2626)      // bg-red-600

class MainActivity : ComponentActivity() {

    // Renomeado para evitar conflito com a classe nativa do Android
    private val appBluetoothManager by lazy { PulseiraBluetoothManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MaterialTheme {
                DashboardScreen(appBluetoothManager)
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Adiciona permissões novas apenas se for Android 12 ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
    }
}

// --- LÓGICA BLUETOOTH (Renomeada) ---
class PulseiraBluetoothManager(private val context: Context) {
    private val adapter: BluetoothAdapter? = android.bluetooth.BluetoothManager::class.java.cast(
        context.getSystemService(Context.BLUETOOTH_SERVICE)
    )?.adapter

    private var socket: BluetoothSocket? = null
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission") // Já checamos a permissão antes de chamar
    suspend fun connectToDevice(deviceName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Verificação extra de segurança para Android 12+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return@withContext false
                    }
                }

                val device = adapter?.bondedDevices?.find { it.name == deviceName }
                if (device != null) {
                    socket = device.createRfcommSocketToServiceRecord(uuid)
                    socket?.connect()
                    return@withContext true
                }
                return@withContext false
            } catch (e: IOException) {
                return@withContext false
            }
        }
    }

    suspend fun listenForData(onDataReceived: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            val inputStream = socket?.inputStream
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                try {
                    if (inputStream != null) {
                        bytes = inputStream.read(buffer)
                        val readMessage = String(buffer, 0, bytes).trim()
                        if (readMessage.isNotEmpty()) {
                            onDataReceived(readMessage)
                        }
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }
    }
}

// --- TELA DASHBOARD ---

@Composable
fun DashboardScreen(btManager: PulseiraBluetoothManager) {
    var uvIndex by remember { mutableStateOf(0) }
    var isDeviceConnected by remember { mutableStateOf(false) }
    var connectionStatusText by remember { mutableStateOf("Desconectada") }

    // Simulação de dados para o design
    val exposureTime = 45
    val safeTimeRemaining = if (uvIndex > 0) 120 / uvIndex else 999

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun handleSync() {
        scope.launch {
            connectionStatusText = "Conectando..."
            // Chama a classe renomeada
            val success = btManager.connectToDevice("UVision_SmartBand")
            if (success) {
                isDeviceConnected = true
                connectionStatusText = "Conectada"
                btManager.listenForData { data ->
                    try {
                        val numberOnly = data.replace("UV:", "").trim().toIntOrNull()
                        if (numberOnly != null) uvIndex = numberOnly
                    } catch (e: Exception) { Log.e("BT", "Erro parse") }
                }
            } else {
                isDeviceConnected = false
                connectionStatusText = "Falha"
                Toast.makeText(context, "Erro ao conectar. Verifique o pareamento!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationSection() },
        containerColor = BgLight
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(
                isConnected = isDeviceConnected,
                statusText = connectionStatusText,
                onSync = { handleSync() }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                if (safeTimeRemaining <= 15) {
                    AlertBanner(safeTimeRemaining)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                MainUVCard(uvIndex, safeTimeRemaining)
                Spacer(modifier = Modifier.height(16.dp))
                StatsGrid(exposureTime)
                Spacer(modifier = Modifier.height(16.dp))
                HourlyExposureCard()
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(isConnected: Boolean, statusText: String, onSync: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyBlue)
            .padding(16.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícone WbSunny (precisa da lib extended)
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("UVision", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Bem-vindo de volta!", color = TextGray, fontSize = 12.sp)
                }
            }

            OutlinedButton(
                onClick = onSync,
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                // Ícone Sync (precisa da lib extended)
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync", fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone Bluetooth (precisa da lib extended ou usar core)
            Icon(
                Icons.Default.Bluetooth,
                contentDescription = null,
                tint = if (isConnected) YellowHighlight else TextGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pulseira $statusText",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Surface(
                color = if (isConnected) YellowHighlight else TextGray,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (isConnected) "Online" else "Offline",
                    color = if (isConnected) NavyBlue else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun AlertBanner(minutes: Int) {
    Surface(
        color = OrangeAlert.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, OrangeAlert),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Warning, contentDescription = null, tint = OrangeAlert, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Atenção! Você tem apenas $minutes minutos de exposição segura restante.",
                color = OrangeAlert,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MainUVCard(uvIndex: Int, safeTime: Int) {
    val (levelLabel, badgeColor) = when {
        uvIndex <= 2 -> "Baixo" to GreenSafe
        uvIndex <= 5 -> "Moderado" to YellowHighlight
        uvIndex <= 7 -> "Alto" to OrangeAlert
        else -> "Muito Alto" to RedDanger
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, NavyBlue.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Índice UV Atual", color = NavyBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Atualizado há 2 minutos", color = TextGray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(uvIndex.toString(), fontSize = 48.sp, color = NavyBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(color = badgeColor, shape = RoundedCornerShape(16.dp)) {
                            Text(
                                text = levelLabel,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Tempo seguro: ",
                        color = TextGray,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "$safeTime min",
                        color = OrangeAlert,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = YellowHighlight.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
fun StatsGrid(exposureTime: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallCard("Exposição Hoje", "$exposureTime min", "+12%", NavyBlue, Modifier.weight(1f))
        SmallCard("Média Semanal", "38 min", "Seguro", NavyBlue, Modifier.weight(1f))
        SmallCard("Alertas", "2", "Ativos", OrangeAlert, Modifier.weight(1f))
    }
}

@Composable
fun SmallCard(title: String, value: String, sub: String, valueColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, NavyBlue.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = TextGray, fontSize = 10.sp, minLines = 2, lineHeight = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = valueColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = TextGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun HourlyExposureCard() {
    val hourlyData = listOf("08:00" to 0.2f, "09:00" to 0.3f, "10:00" to 0.55f, "11:00" to 0.7f, "12:00" to 0.9f)
    val hourlyLabels = listOf("10m", "15m", "25m", "35m", "45m")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, NavyBlue.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Exposição por Hora", color = NavyBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Hoje", color = TextGray, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = {},
                    border = BorderStroke(1.dp, NavyBlue),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Ver Mais", color = NavyBlue, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            hourlyData.forEachIndexed { index, (hour, fill) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(hour, color = TextGray, fontSize = 10.sp, modifier = Modifier.width(32.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TextGray.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fill)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(YellowHighlight),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(hourlyLabels[index], fontSize = 9.sp, color = NavyBlue, modifier = Modifier.padding(end = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationSection() {
    NavigationBar(
        containerColor = Color.White,
        contentColor = NavyBlue,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavyBlue, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, null) },
            label = { Text("Dados") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Alertas") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Perfil") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboard() {
    MaterialTheme {
        DashboardScreen(PulseiraBluetoothManager(LocalContext.current))
    }
}