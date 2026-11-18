package com.salem.alexander

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AlexanderApp() }
    }
}

private data class ChatMessage(val text: String, val fromUser: Boolean)


@Composable
fun AlexanderApp() {
    val bg = Color(0xFF0B1020)
    val panel = Color(0xFF151D2F)
    val accent = Color(0xFF4F8CFF)
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var voiceStatus by remember { mutableStateOf("اضغط الميكروفون واتكلم") }
    var messages by remember {
        mutableStateOf(listOf(ChatMessage("أهلاً يا سالم، أنا ألكسندر. جاهز أساعدك، نبدأ بإيه؟", false)))
    }
    val voiceController = remember(context) {
        VoiceController(context,
            onTranscript = { transcript ->
                val reply = "سمعتك يا سالم: $transcript. أنا لسه في النسخة التجريبية؛ محرك الذكاء الاصطناعي وتنفيذ الأوامر هيتوصلوا في المرحلة القادمة."
                messages = messages + ChatMessage(transcript, true) + ChatMessage(reply, false)
                voiceStatus = "تم استلام كلامك"
                reply
            },
            onStatus = { status -> voiceStatus = status }
        )
    }
    DisposableEffect(voiceController) {
        onDispose { voiceController.destroy() }
    }
    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) voiceController.listen() else voiceStatus = "لازم تسمح باستخدام الميكروفون"
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = bg, surface = panel, primary = accent,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Column(Modifier.fillMaxSize().background(bg).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Surface(shape = RoundedCornerShape(16.dp), color = panel, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SmartToy, contentDescription = "Alexander", tint = accent, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("ALEXANDER", fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("مساعدك الذكي الشخصي", color = Color(0xFF9BA8BF), fontSize = 12.sp)
                }
                Surface(color = Color(0xFF173322), shape = CircleShape) {
                    Text("● جاهز", color = Color(0xFF69DB9A), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (message.fromUser) Color(0xFF244B91) else panel,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.widthIn(max = 320.dp)
                        ) {
                            Text(message.text, modifier = Modifier.padding(14.dp), color = Color.White, lineHeight = 22.sp)
                        }
                    }
                }
            }
            Surface(color = panel, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            voiceController.listen()
                        } else {
                            micPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }) {
                        Icon(Icons.Default.Mic, contentDescription = "تحدث مع Alexander", tint = accent)
                    }
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
                        decorationBox = { inner ->
                            if (input.isEmpty()) Text("اكتب أمر لألكسندر...", color = Color(0xFF8390A8))
                            inner()
                        }
                    )
                    IconButton(onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty()) {
                            messages = messages + ChatMessage(text, true) + ChatMessage(
                                "وصلني أمرك يا سالم. محرك الذكاء الاصطناعي وتنفيذ الأوامر لسه هيتوصلوا في المرحلة القادمة.", false
                            )
                            voiceController.speak("وصلني أمرك يا سالم. محرك الذكاء الاصطناعي وتنفيذ الأوامر لسه هيتوصلوا في المرحلة القادمة.")
                            input = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "إرسال", tint = accent)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(voiceStatus, color = Color(0xFF9BA8BF), fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(8.dp))
            Text("نسخة تجريبية • 0.2.0", color = Color(0xFF66738A), fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
