package io.boxo.sample.hostapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import io.boxo.sdk.Boxo
import io.boxo.sdk.MiniappConfig

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentLanguage by remember {
                        mutableStateOf(Boxo.config.language)
                    }
                    MainScreen(
                        currentLanguage = currentLanguage,
                        onOpenMiniapp = { openDemoMiniapp() },
                        onChangeLanguage = { language ->
                            changeLanguage(language)
                            currentLanguage = language
                        }
                    )
                }
            }
        }
    }

    private fun openDemoMiniapp() {
        Boxo.getMiniapp("APP_ID")
            .setConfig(MiniappConfig.Builder()
                .saveState(false)
                .build())
            .setAuthListener { _, miniapp -> miniapp.setAuthCode("") }
            .open()
    }

    private fun changeLanguage(language: String) {
        Boxo.setConfig(Boxo.config
            .toBuilder()
            .setLanguage(language)
            .build())
    }
}

@Composable
fun MainScreen(
    currentLanguage: String?,
    onOpenMiniapp: () -> Unit,
    onChangeLanguage: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = onOpenMiniapp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text(text = "Open miniapp")
        }

        Text(
            text = "Language: ${currentLanguage ?: ""}",
            modifier = Modifier.padding(top = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onChangeLanguage("kk") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Qazaq")
            }
            OutlinedButton(
                onClick = { onChangeLanguage("en") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "English")
            }
        }
    }
}
