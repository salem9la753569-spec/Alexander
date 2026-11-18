package com.salem.alexander

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Arabic-Egyptian speech input and Android TTS output for Alexander. */
class VoiceController(
    context: Context,
    private val onTranscript: (String) -> String,
    private val onStatus: (String) -> Unit
) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    init {
        if (SpeechRecognizer.isRecognitionAvailable(appContext)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).also {
                it.setRecognitionListener(listener)
            }
        }
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        ttsReady = status == TextToSpeech.SUCCESS
        if (ttsReady) {
            val result = tts?.setLanguage(Locale("ar", "EG"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("ar"))
            }
        }
    }

    fun listen() {
        val speech = recognizer
        if (speech == null) {
            onStatus("خدمة التعرف على الكلام مش متاحة على الجهاز")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-EG")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "اتكلم يا سالم...")
        }
        onStatus("سامعك...")
        speech.startListening(intent)
    }

    fun speak(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "alexander_reply")
        else onStatus("الصوت لسه بيجهز")
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { onStatus("اتكلم دلوقتي") }
        override fun onBeginningOfSpeech() { onStatus("بسمعك...") }
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() { onStatus("بفهم كلامك...") }
        override fun onError(error: Int) {
            onStatus(when (error) {
                SpeechRecognizer.ERROR_NO_MATCH -> "مش قادر أفهم الكلام، جرب تاني"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ماسمعتش كلام، اضغط وحاول تاني"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "محتاج صلاحية الميكروفون"
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "في مشكلة اتصال"
                else -> "حصل خطأ في التعرف على الصوت ($error)"
            })
        }
        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            if (text.isNotBlank()) {
                onStatus("تم التعرف على كلامك")
                val reply = onTranscript(text)
                if (reply.isNotBlank()) speak(reply)
            } else onStatus("ماوصلنيش كلام واضح")
        }
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }
}
