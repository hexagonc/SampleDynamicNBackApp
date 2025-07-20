package com.evolved.automata.app.dynamicnback.ui.screens

import android.media.RingtoneManager
import android.net.Uri
import android.text.Spanned
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeType
import com.evolved.automata.app.dynamicnback.ui.NBackViewModel
import com.evolved.automata.app.dynamicnback.ui.UIController
import kotlinx.serialization.Serializable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned

import com.evolved.automata.app.dynamicnback.R

import com.evolved.automata.app.dynamicnback.getRingtonePickerIntent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Serializable
data class ConfigureNewGame(val initialProfileToConfig:String)
@Composable
fun GameConfigScreen(uiController:UIController, modifier: Modifier, initialProfile:Profile, viewModel: NBackViewModel = hiltViewModel()) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("New Game Config", style = typography.titleLarge)

        val scope = rememberCoroutineScope()

        val allProfileNames:List<String> by viewModel.profileNames.collectAsState()
        var profileConfigured:Profile by remember{mutableStateOf(initialProfile)}

        var profileName by remember { mutableStateOf(initialProfile.name)}

        var N by remember {mutableStateOf(initialProfile.lastConfig.N)}
        var presentationMS by remember { mutableStateOf(initialProfile.lastConfig.impressionPeriodMS) }

        var reviewPredioMS by remember { mutableStateOf(initialProfile.lastConfig.reviewPeriodMS) }


        var expanded by remember { mutableStateOf(false) }


        Box {
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorScheme.primary, unfocusedTextColor = colorScheme.primary),
                modifier = Modifier.fillMaxWidth(), value = profileName,  label = {Text("Profile name:", color = colorScheme.primary)}, onValueChange = { it:String -> profileName = it; profileConfigured = profileConfigured.copy(name = it)},
                trailingIcon = {
                    IconButton(onClick = { expanded = true}) {
                        Icon(Icons.Filled.ArrowDropDown, tint = colorScheme.primary, contentDescription = "profile search")
                    }
                })

            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.heightIn(max = 200.dp),
                onDismissRequest = { expanded = false }) {
                for (pName:String in allProfileNames) {
                    val name:String = pName
                    DropdownMenuItem(onClick = {
                        scope.launch(Dispatchers.IO) {
                            val loadedProfile:Profile = viewModel.fetchProfileByName(name).await()
                            profileConfigured = loadedProfile
                            profileName = name;
                            N = loadedProfile.lastConfig.N
                            presentationMS = loadedProfile.lastConfig.impressionPeriodMS
                            reviewPredioMS = loadedProfile.lastConfig.reviewPeriodMS
                        }
                        expanded = false }, text = {
                        Text(pName)
                    })
                }
            }
        }

        ConfigPanel(profileConfigured, modifier = Modifier.fillMaxWidth(),
            onChangeN = {it:Int ->
                profileConfigured = profileConfigured.copy(lastConfig = profileConfigured.lastConfig.copy(N = it));
                N = profileConfigured.lastConfig.N},
            onChangedImpressionPeriod = { it:Long ->
                profileConfigured = profileConfigured.copy(lastConfig = profileConfigured.lastConfig.copy(impressionPeriodMS = it));
                presentationMS = profileConfigured.lastConfig.impressionPeriodMS},
            onChangedReviewPeriod = { it:Long ->
                profileConfigured = profileConfigured.copy(lastConfig = profileConfigured.lastConfig.copy(reviewPeriodMS = it));
                reviewPredioMS = profileConfigured.lastConfig.reviewPeriodMS
            })
        val onUpdatedBadgeConfig:(BadgeConfig)->Unit = { bConfig:BadgeConfig ->
            profileConfigured = profileConfigured.copy(experimentConfig = profileConfigured.experimentConfig.copy(badgeConfig = bConfig));
        }
        ConfigBadgePane(profile = profileConfigured, onBadgeUpdated = onUpdatedBadgeConfig)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            TextButton(onClick = {
                scope.launch(Dispatchers.IO) {
                    viewModel.saveProfile(profileConfigured).await();
                    uiController.showSnackbar("Saved profile: $profileName")
                }

            }) {
                Text("Save changes")
            }
            TextButton(onClick = {
                uiController.startGame(profileConfigured)
            }) {Text("Start Game", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        }

    }
}

@Composable
fun ConfigBadgePane(modifier: Modifier = Modifier, profile: Profile, onBadgeUpdated:(BadgeConfig)->Unit = {}, viewModel: NBackViewModel = hiltViewModel()) {
    var selectedConfig: BadgeConfig by remember{ mutableStateOf(profile.experimentConfig.badgeConfig)}

    LaunchedEffect(profile.experimentConfig.badgeConfig.name) {
        selectedConfig = profile.experimentConfig.badgeConfig
    }

    var onAudioBadgeSelected: (name:String, uri:String ) -> Unit = {name:String, uri:String ->
        selectedConfig = selectedConfig.copy(badgeType = BadgeType.AUDIO, contentKey = uri, name = name)
    }

    val context = LocalContext.current.applicationContext

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        if (uri != null) {
            val ringtone = RingtoneManager.getRingtone(context, uri)
            val title = ringtone?.getTitle(context) ?: "Unknown Ringtone"
            onAudioBadgeSelected(title, uri.toString())
        }
    }

    var showConfigToolTip by remember { mutableStateOf(false) }

    val helpToolText = "Specify how you get notified when you correctly indicate that the current stimulus is the same as the one N-steps back.\n You can set the ringtone to play a specify that a graphical badge is shown."
    val spannedToolText = remember {
        HtmlCompat.fromHtml(helpToolText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    Column(modifier = modifier.padding(top = 10.dp).border(2.dp, color = Color.Black, shape = RoundedCornerShape(5.dp)).padding(5.dp), horizontalAlignment = Alignment.Start) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Text("Configure reward notification", style = typography.headlineLarge)
            Box(){
                DropdownMenu(
                    expanded = showConfigToolTip,
                    onDismissRequest = { showConfigToolTip = false },
                    modifier = Modifier
                        .widthIn(min = 200.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = spannedToolText.toAnnotatedString(),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = {showConfigToolTip = true}) {
                    Icon(painter = painterResource(R.drawable.icon_small_help), tint = colorScheme.primary, contentDescription = "Help button")
                }
            }
        }
        Text("Set tone to play on correct action")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            if (selectedConfig.badgeType == BadgeType.VISUAL) {
                Text("N/A")
            }
            else
                Text(selectedConfig.name)

            Row(){
                IconButton(onClick = { ringtonePickerLauncher.launch(getRingtonePickerIntent(context)) }) {
                    Icon(Icons.Filled.Edit, tint = colorScheme.primary, contentDescription = "Edit ringtone")
                }


                IconButton(onClick = { viewModel.playBadgeAlert(selectedConfig.contentKey)}, enabled = selectedConfig.badgeType == BadgeType.AUDIO) {
                    Icon(Icons.Filled.PlayArrow, tint = if (selectedConfig.badgeType == BadgeType.VISUAL) Color.LightGray else colorScheme.primary, contentDescription = "Preview sound")
                }
            }
        }

        TextButton(onClick = { onBadgeUpdated(selectedConfig)}) {Text("Update ringtone") }
    }

}

@Composable
fun ConfigPanel(profileSource: Profile, modifier:Modifier = Modifier.fillMaxWidth(), onChangeN:(Int) -> Unit = {}, onChangedImpressionPeriod:(Long) -> Unit, onChangedReviewPeriod:(Long) -> Unit){

    var preferredNBack by remember { mutableStateOf(profileSource.lastConfig.N)}
    var preferredImprintPeriod by remember { mutableStateOf(profileSource.lastConfig.impressionPeriodMS)}
    var preferredReviewPeriod by remember {mutableStateOf(profileSource.lastConfig.reviewPeriodMS)}

    LaunchedEffect(profileSource.name) {
        preferredNBack = profileSource.lastConfig.N
        preferredImprintPeriod = profileSource.lastConfig.impressionPeriodMS
        preferredReviewPeriod = profileSource.lastConfig.reviewPeriodMS
    }

    var showConfigToolTip by remember { mutableStateOf(false) }

    val helpToolText = "<b>N</b>: sets the number of items you must memorize<br/><b>Impression duration</b>: the duration in milliseconds that the stimulus is shown.<br/><b>review duration</b>: Number of milliseconds between stimulus where nothing is shown."
    val spannedToolText = remember {
        HtmlCompat.fromHtml(helpToolText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Text("Game testing parameters", style = typography.headlineLarge)
            Box(){
                DropdownMenu(
                    expanded = showConfigToolTip,
                    onDismissRequest = { showConfigToolTip = false },
                    modifier = Modifier
                        .widthIn(min = 200.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = spannedToolText.toAnnotatedString(),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = {showConfigToolTip = true}) {
                    Icon(painter = painterResource(R.drawable.icon_small_help), tint = colorScheme.primary, contentDescription = "Help button")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth(0.75F), horizontalAlignment = Alignment.Start){
            Text("N: ${preferredNBack}")
            Slider(value = preferredNBack.toFloat(), steps = 7, onValueChange = { it:Float -> onChangeN(it.toInt()); preferredNBack = it.toInt() },  modifier = Modifier.fillMaxWidth(), valueRange = 2.0F .. 8F)
            Text("Impression duration: ${preferredImprintPeriod}")
            Slider(value = preferredImprintPeriod.toFloat(), steps = 5, onValueChange = { it:Float -> onChangedImpressionPeriod(it.toLong()); preferredImprintPeriod = it.toLong() },  modifier = Modifier.fillMaxWidth(), valueRange = 1000F .. 4000F)
            Text("Review duration: ${preferredReviewPeriod}")
            Slider(value = preferredReviewPeriod.toFloat(), steps = 6, onValueChange = { it:Float -> onChangedReviewPeriod(it.toLong()); preferredReviewPeriod = it.toLong() },  modifier = Modifier.fillMaxWidth(), valueRange = 0F .. 4000F)
        }
    }
}


fun Spanned.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {

        append(toSpanned().toString()) // Basic fallback
        // Optional: parse spans and apply styles here if needed
    }
}

