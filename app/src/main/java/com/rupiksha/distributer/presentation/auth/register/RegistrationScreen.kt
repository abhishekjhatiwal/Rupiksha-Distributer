package com.rupiksha.distributer.presentation.auth.register

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rupiksha.distributer.di.AppContainer
import com.rupiksha.distributer.presentation.auth.login.CustomOutlinedTextField
import com.rupiksha.distributer.ui.theme.*
import com.rupiksha.distributer.util.ImageUtils
import com.rupiksha.distributer.util.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

private val stepTitles = listOf("Personal", "Business", "KYC & Finance", "Documents", "Security")
private val stepSubtitles = listOf(
    "Let's start with your basic information",
    "Tell us about your shop",
    "Your identity & banking details",
    "Upload the required photos",
    "Secure your account"
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RegistrationScreen(
    appContainer: AppContainer,
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    val viewModel: RegistrationViewModel =
        viewModel(factory = RegistrationViewModelFactory(appContainer))
    val uiState by viewModel.uiState.collectAsState()

    var showSuccessOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            showSuccessOverlay = true
            delay(1300)
            onRegistrationSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMeshBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            AnimatedContent(
                                targetState = uiState.currentStep,
                                transitionSpec = {
                                    (fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 }) togetherWith
                                            (fadeOut(tween(150)))
                                },
                                label = "titleFade"
                            ) { step ->
                                Text(
                                    stepTitles[step - 1],
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            AnimatedContent(
                                targetState = uiState.currentStep,
                                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                                label = "stepCountFade"
                            ) { step ->
                                Text(
                                    "Step $step of 5",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (uiState.currentStep > 1) viewModel.previousStep() else onNavigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = BrandPrimary
                    )
                )
            }
        ) { padding ->
            // BoxWithConstraints lets us measure available width so the card can size itself
            // sensibly on phones, foldables and tablets instead of always stretching edge-to-edge.
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Wide screens (tablets/foldables) get generous side margins and a capped card
                // width so the form doesn't stretch into an unreadable single line of text.
                val isWideScreen = maxWidth > 600.dp
                val horizontalMargin = if (isWideScreen) 32.dp else 20.dp
                val contentMaxWidth = 520.dp

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = contentMaxWidth)
                        .align(Alignment.TopCenter)
                        .padding(horizontal = horizontalMargin)
                ) {
                    StepIndicator(
                        currentStep = uiState.currentStep,
                        totalSteps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp)
                    ) {
                        val cardInfiniteTransition =
                            rememberInfiniteTransition(label = "cardBorderAnim")
                        val cardShimmer by cardInfiniteTransition.animateFloat(
                            initialValue = -1f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
                            label = "cardShimmer"
                        )
                        val cardTertiary = MaterialTheme.colorScheme.tertiary
                        val cardBorderBrush = Brush.linearGradient(
                            colors = listOf(
                                BrandPrimary.copy(alpha = 0.55f),
                                cardTertiary.copy(alpha = 0.45f),
                                BrandPrimaryDark.copy(alpha = 0.55f),
                                BrandPrimary.copy(alpha = 0.55f)
                            ),
                            start = Offset(cardShimmer * 500f, 0f),
                            end = Offset(cardShimmer * 500f + 500f, 500f)
                        )

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(28.dp),
                            color = Color.White.copy(alpha = 0.92f),
                            shadowElevation = 8.dp,
                            border = BorderStroke(1.6.dp, cardBorderBrush)
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentStep,
                                transitionSpec = {
                                    val forward = targetState > initialState
                                    val enterDir = if (forward) 1 else -1
                                    val exitDir = if (forward) -1 else 1
                                    (slideInHorizontally(tween(350)) { w -> enterDir * w } + fadeIn(
                                        tween(350)
                                    ) + scaleIn(
                                        tween(350),
                                        initialScale = 0.97f
                                    )) togetherWith
                                            (slideOutHorizontally(tween(300)) { w -> exitDir * w } + fadeOut(
                                                tween(200)
                                            ))
                                },
                                label = "stepContent"
                            ) { step ->
                                when (step) {
                                    1 -> StepPersonal(uiState, viewModel)
                                    2 -> StepBusiness(uiState, viewModel)
                                    3 -> StepKycFinance(uiState, viewModel)
                                    4 -> StepMedia(uiState, viewModel)
                                    5 -> StepSecurity(uiState, viewModel)
                                }
                            }
                        }
                    }

                    // Bottom Actions — kept inside the same max-width column as the card so the
                    // buttons always line up flush with the card's edges, at every screen size.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = uiState.currentStep > 1,
                            enter = fadeIn(tween(250)) + expandHorizontally(tween(250)),
                            exit = fadeOut(tween(150)) + shrinkHorizontally(tween(150))
                        ) {
                            Row {
                                val prevInfiniteTransition =
                                    rememberInfiniteTransition(label = "prevBtnBorderAnim")
                                val prevShimmer by prevInfiniteTransition.animateFloat(
                                    initialValue = -1f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        tween(
                                            2800,
                                            easing = LinearEasing
                                        )
                                    ),
                                    label = "prevBtnShimmer"
                                )
                                val prevTertiary = MaterialTheme.colorScheme.tertiary
                                val prevBorderBrush = Brush.linearGradient(
                                    colors = listOf(
                                        BrandPrimary.copy(alpha = 0.55f),
                                        prevTertiary.copy(alpha = 0.45f),
                                        BrandPrimaryDark.copy(alpha = 0.55f),
                                        BrandPrimary.copy(alpha = 0.55f)
                                    ),
                                    start = Offset(prevShimmer * 180f, 0f),
                                    end = Offset(prevShimmer * 180f + 180f, 180f)
                                )

                                OutlinedButton(
                                    onClick = { viewModel.previousStep() },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.8.dp, prevBorderBrush)
                                ) {
                                    Text("Previous", color = TextSecondary)
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                        }

                        AnimatedPrimaryButton(
                            text = if (uiState.currentStep < 5) "Next" else "Register",
                            isLoading = uiState.isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = {
                                if (uiState.currentStep < 5) viewModel.nextStep() else viewModel.register()
                            }
                        )
                    }
                }
            }
        }

        SuccessOverlay(visible = showSuccessOverlay)
    }
}

/**
 * Soft, slowly drifting gradient blobs behind the whole screen for a modern, airy
 * fintech feel. Cheap to render (no blur modifier / API 31 dependency) since the
 * softness comes purely from radial gradient falloff.
 */
@Composable
private fun AnimatedMeshBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "meshDrift")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing)),
        label = "driftAngle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BackgroundLight,
                        Color.White,
                        BackgroundLight
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.45f)
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    translationX = 60f * sin(drift)
                    translationY = 40f * sin(drift * 0.7f)
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(BrandPrimary.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    translationX = -50f * sin(drift * 0.8f)
                    translationY = -30f * sin(drift)
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(BrandPrimaryDark.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )
    }
}

/** Gradient button with a spring-based press scale and animated loading state. */
@Composable
fun AnimatedPrimaryButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "buttonShimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2200, delayMillis = 600, easing = LinearEasing)),
        label = "shimmerOffset"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        enabled = !isLoading,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, BrandPrimaryDark.copy(alpha = 0.35f)),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BrandPrimary, BrandPrimaryDark)
                    )
                )
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithShimmer(shimmerOffset, enabled = !isLoading),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = isLoading, label = "buttonContent") { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = if (text == "Register") Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Draws a soft diagonal light sweep across a button to signal it's ready to tap. */
private fun Modifier.drawWithShimmer(offset: Float, enabled: Boolean): Modifier = this.then(
    Modifier.background(
        brush = if (enabled) {
            Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.16f),
                    Color.Transparent
                ),
                start = Offset(x = (offset - 0.3f) * 600f, y = 0f),
                end = Offset(x = (offset + 0.3f) * 600f, y = 200f)
            )
        } else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    )
)

/**
 * Free-floating row of outline-only step markers connected by a progress track.
 * No card/rectangle wrapper — the dots sit directly on the background.
 * The active step gets an orbiting gradient ring + soft pulse; completed steps
 * morph their border color and swap the number for a checkmark.
 */
@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 10.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep

            StepDot(step = step, isCompleted = isCompleted, isCurrent = isCurrent)

            if (step != totalSteps) {
                val lineFill by animateFloatAsState(
                    targetValue = if (step < currentStep) 1f else 0f,
                    animationSpec = tween(450, easing = FastOutSlowInEasing),
                    label = "lineFill"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(BorderLight.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(lineFill)
                            .background(
                                Brush.horizontalGradient(listOf(BrandPrimary, BrandPrimaryDark)),
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
    }
}

/** A single outline-style step marker: no filled background, just an animated border. */
@Composable
private fun StepDot(step: Int, isCompleted: Boolean, isCurrent: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrent) 1.14f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dotScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCompleted || isCurrent) BrandPrimary else BorderLight,
        animationSpec = tween(400),
        label = "borderColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isCurrent) BrandPrimary else if (isCompleted) BrandPrimary else TextSecondary,
        animationSpec = tween(400),
        label = "textColor"
    )
    val fillTint by animateColorAsState(
        targetValue = if (isCurrent) BrandPrimary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(400),
        label = "fillTint"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "stepDotOrbit")
    // Slow, continuous rotation that only visually matters while this step is active.
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "orbitRotation"
    )
    // Gentle breathing glow behind the active step.
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.32f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        if (isCurrent) {
            // Soft halo — no rectangle, just a translucent glow that breathes.
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = pulseAlpha))
            )
            // Orbiting gradient arc that spins around the border, like a tiny loader.
            Canvas(modifier = Modifier.size(32.dp)) {
                rotate(orbitRotation) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                BrandPrimary.copy(alpha = 0f),
                                BrandPrimaryDark,
                                BrandPrimary,
                                BrandPrimary.copy(alpha = 0f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 300f,
                        useCenter = false,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(fillTint)
                .border(BorderStroke(2.dp, borderColor), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isCompleted,
                transitionSpec = {
                    (fadeIn(tween(250)) + scaleIn(
                        tween(250),
                        initialScale = 0.4f
                    )) togetherWith
                            (fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.4f))
                },
                label = "stepMarker"
            ) { completed ->
                if (completed) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        "$step",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/** Wraps a field so it fades/slides in shortly after the step appears, for a staggered feel. */
@Composable
fun AnimatedField(delayMillis: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong().milliseconds)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 3 }
    ) {
        content()
    }
}

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = BrandPrimary)
    Spacer(Modifier.height(6.dp))
    Text(subtitle, color = TextSecondary, fontSize = 14.sp)
    Spacer(Modifier.height(24.dp))
}

@Composable
fun StepPersonal(uiState: RegistrationUiState, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StepHeader(stepTitles[0] + " Details", stepSubtitles[0])

        AnimatedField(0) {
            CustomOutlinedTextField(
                value = uiState.data.name,
                onValueChange = { name -> viewModel.updateData("name") { it.copy(name = name) } },
                placeholder = "Full Name",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.fieldErrors["name"]
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(60) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomOutlinedTextField(
                            value = uiState.data.mobile,
                            onValueChange = { mobile ->
                                if (mobile.length <= 10 && mobile.all { it.isDigit() }) {
                                    viewModel.updateData("mobile") { it.copy(mobile = mobile) }
                                }
                            },
                            placeholder = "Mobile Number",
                            leadingIcon = Icons.Default.Phone,
                            errorMessage = uiState.fieldErrors["mobile"],
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    if (uiState.data.isMobileVerified) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = BrandPrimary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(28.dp)
                        )
                    } else if (uiState.data.mobile.length == 10) {
                        TextButton(
                            onClick = { viewModel.sendOtp() },
                            enabled = !uiState.isOtpSending && uiState.otpTimer == 0,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            if (uiState.isOtpSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    if (uiState.otpTimer > 0) "Resend in ${uiState.otpTimer}s" else "Send OTP",
                                    fontSize = 13.sp,
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = uiState.isOtpSent && !uiState.data.isMobileVerified,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        // IntrinsicSize.Min lets the button stretch to exactly match the text
                        // field's real rendered height, instead of guessing a fixed dp value
                        // that drifts out of alignment whenever the field's own height changes.
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                CustomOutlinedTextField(
                                    value = uiState.otpInput,
                                    onValueChange = {
                                        if (it.length <= 6) viewModel.updateOtpInput(
                                            it
                                        )
                                    },
                                    placeholder = "Enter 6-digit OTP",
                                    leadingIcon = Icons.Default.LockClock,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Button(
                                onClick = { viewModel.verifyOtp() },
                                enabled = uiState.otpInput.length >= 4 && !uiState.isOtpVerifying,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(96.dp),
                                border = BorderStroke(1.dp, BrandPrimaryDark.copy(alpha = 0.35f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandPrimary,
                                    disabledContainerColor = BrandPrimary.copy(alpha = 0.4f)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                if (uiState.isOtpVerifying) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Verify",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        if (uiState.error != null) {
                            Text(
                                uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(120) {
            CustomOutlinedTextField(
                value = uiState.data.email,
                onValueChange = { email -> viewModel.updateData("email") { it.copy(email = email) } },
                placeholder = "Email ID",
                leadingIcon = Icons.Default.Email,
                errorMessage = uiState.fieldErrors["email"],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
    }
}

@Composable
fun StepBusiness(uiState: RegistrationUiState, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StepHeader(stepTitles[1] + " Details", stepSubtitles[1])

        AnimatedField(0) {
            CustomOutlinedTextField(
                value = uiState.data.shopName,
                onValueChange = { shopName -> viewModel.updateData("shopName") { it.copy(shopName = shopName) } },
                placeholder = "Shop Name",
                leadingIcon = Icons.Default.Store,
                errorMessage = uiState.fieldErrors["shopName"]
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(60) {
            CustomOutlinedTextField(
                value = uiState.data.shopAddress,
                onValueChange = { shopAddress ->
                    viewModel.updateData("shopAddress") {
                        it.copy(
                            shopAddress = shopAddress
                        )
                    }
                },
                placeholder = "Shop Address",
                leadingIcon = Icons.Default.LocationOn,
                errorMessage = uiState.fieldErrors["shopAddress"]
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(90) {
            CustomOutlinedTextField(
                value = uiState.data.permanentAddress,
                onValueChange = { addr ->
                    viewModel.updateData("permanentAddress") {
                        it.copy(
                            permanentAddress = addr
                        )
                    }
                },
                placeholder = "Permanent Address",
                leadingIcon = Icons.Default.Home,
                errorMessage = uiState.fieldErrors["permanentAddress"]
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedField(120) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomOutlinedTextField(
                    value = uiState.data.pincode,
                    onValueChange = { pincode ->
                        if (pincode.length <= 6 && pincode.all { it.isDigit() }) {
                            viewModel.updateData("pincode") { it.copy(pincode = pincode) }
                            if (pincode.length == 6) {
                                viewModel.fetchLocationDetails(pincode)
                            }
                        }
                    },
                    placeholder = "Pincode",
                    leadingIcon = Icons.Default.Numbers,
                    errorMessage = uiState.fieldErrors["pincode"],
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (uiState.isPincodeLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                AnimatedField(180) {
                    CustomOutlinedTextField(
                        value = uiState.data.district,
                        onValueChange = { district ->
                            viewModel.updateData("district") {
                                it.copy(
                                    district = district
                                )
                            }
                        },
                        placeholder = "District",
                        leadingIcon = Icons.Default.LocationCity,
                        errorMessage = uiState.fieldErrors["district"]
                    )
                }

//                Spacer(Modifier.height(16.dp))

//                CustomOutlinedTextField(
//                    value = uiState.data.district,
//                    onValueChange = { district -> viewModel.updateData("district") { it.copy(district = district) } },
//                    placeholder = "District",
//                    leadingIcon = Icons.Default.LocationCity,
//                    errorMessage = uiState.fieldErrors["district"]
//                )

                Spacer(Modifier.height(16.dp))

                CustomOutlinedTextField(
                    value = uiState.data.state,
                    onValueChange = { state -> viewModel.updateData("state") { it.copy(state = state) } },
                    placeholder = "State",
                    leadingIcon = Icons.Default.Map,
                    errorMessage = uiState.fieldErrors["state"]
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun StepKycFinance(uiState: RegistrationUiState, viewModel: RegistrationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StepHeader(stepTitles[2], stepSubtitles[2])

        AnimatedField(0) {
            CustomOutlinedTextField(
                value = uiState.data.adharNumber,
                onValueChange = { adhar ->
                    if (adhar.length <= 12 && adhar.all { it.isDigit() }) {
                        viewModel.updateData("adharNumber") { it.copy(adharNumber = adhar) }
                    }
                },
                placeholder = "Adhaar Number",
                leadingIcon = Icons.Default.Badge,
                errorMessage = uiState.fieldErrors["adharNumber"],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(60) {
            CustomOutlinedTextField(
                value = uiState.data.panNumber,
                onValueChange = { pan -> viewModel.updateData("panNumber") { it.copy(panNumber = pan.uppercase()) } },
                placeholder = "PAN Number",
                leadingIcon = Icons.Default.Badge,
                errorMessage = uiState.fieldErrors["panNumber"]
            )
        }
        Spacer(Modifier.height(28.dp))

        Text(
            "Account Details",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = BrandPrimary
        )
        Spacer(Modifier.height(16.dp))

        AnimatedField(160) {
            CustomOutlinedTextField(
                value = uiState.data.accountHolderName,
                onValueChange = { name ->
                    viewModel.updateData("accountHolderName") {
                        it.copy(
                            accountHolderName = name
                        )
                    }
                },
                placeholder = "Account Holder Name",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.fieldErrors["accountHolderName"]
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(200) {
            CustomOutlinedTextField(
                value = uiState.data.accountNumber,
                onValueChange = { num ->
                    viewModel.updateData("accountNumber") {
                        it.copy(
                            accountNumber = num
                        )
                    }
                },
                placeholder = "Account Number",
                leadingIcon = Icons.Default.AccountBalance,
                errorMessage = uiState.fieldErrors["accountNumber"],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(240) {
            CustomOutlinedTextField(
                value = uiState.data.bankName,
                onValueChange = { bank -> viewModel.updateData("bankName") { it.copy(bankName = bank) } },
                placeholder = "Bank Name",
                leadingIcon = Icons.Default.AccountBalance,
                errorMessage = uiState.fieldErrors["bankName"]
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(280) {
            CustomOutlinedTextField(
                value = uiState.data.ifscCode,
                onValueChange = { ifsc -> viewModel.updateData("ifscCode") { it.copy(ifscCode = ifsc.uppercase()) } },
                placeholder = "IFSC Code",
                leadingIcon = Icons.Default.Code,
                errorMessage = uiState.fieldErrors["ifscCode"]
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedField(60) {
            CustomOutlinedTextField(
                value = uiState.data.upiId,
                onValueChange = { upi -> viewModel.updateData("upiId") { it.copy(upiId = upi) } },
                placeholder = "UPI ID",
                leadingIcon = Icons.Default.Badge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StepMedia(uiState: RegistrationUiState, viewModel: RegistrationViewModel) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    var activeDocType by remember { mutableStateOf<DocumentType?>(null) }
    var showSourcePicker by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleImageSelection(context, it, activeDocType, viewModel, scope)
            activeDocType = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let {
                handleImageSelection(context, it, activeDocType, viewModel, scope)
                activeDocType = null
            }
        }
    }

    fun openSourcePicker(type: DocumentType) {
        activeDocType = type
        showSourcePicker = true
    }

    if (showSourcePicker) {
        AlertDialog(
            onDismissRequest = { showSourcePicker = false },
            title = { Text("Choose Image Source") },
            text = { Text("Select where you want to pick the photo from.") },
            confirmButton = {
                TextButton(onClick = {
                    showSourcePicker = false
                    if (cameraPermissionState.status.isGranted) {
                        val uri = createTmpFileUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSourcePicker = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Gallery")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StepHeader(stepTitles[3] + " & Photos", stepSubtitles[3])

        // Aadhaar Section
        Text(
            "Aadhaar Card",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            MediaItem(
                label = "Front Photo",
                uri = uiState.data.adharFrontUri,
                isError = uiState.fieldErrors["adharFront"] != null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                openSourcePicker(DocumentType.ADHAR_FRONT)
            }
            Spacer(Modifier.width(16.dp))
            MediaItem(
                label = "Back Photo",
                uri = uiState.data.adharBackUri,
                isError = uiState.fieldErrors["adharBack"] != null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                openSourcePicker(DocumentType.ADHAR_BACK)
            }
        }
        Spacer(Modifier.height(16.dp))

        // PAN Section
        Text("PAN Card", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            MediaItem(
                label = "Front Photo",
                uri = uiState.data.panFrontUri,
                isError = uiState.fieldErrors["panFront"] != null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                openSourcePicker(DocumentType.PAN_FRONT)
            }
            Spacer(Modifier.width(16.dp))
            MediaItem(
                label = "Back Photo",
                uri = uiState.data.panBackUri,
                isError = uiState.fieldErrors["panBack"] != null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                openSourcePicker(DocumentType.PAN_BACK)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Additional Documents Section
        Text(
            "Additional Documents",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        val additionalDocTypes = listOf("Electricity Bill", "Voter ID Card", "Passport")

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                additionalDocTypes.forEach { type ->
                    val isSelected = uiState.data.additionalDocType == type
                    Surface(
                        onClick = { viewModel.updateData { it.copy(additionalDocType = type) } },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BrandPrimary else Color.Transparent,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (uiState.data.additionalDocType != null) {
                Spacer(Modifier.height(12.dp))
                MediaItem(
                    "Upload ${uiState.data.additionalDocType}",
                    uri = uiState.data.additionalDocUri
                ) {
                    openSourcePicker(DocumentType.ADDITIONAL_DOC)
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        AnimatedField(80) {
            Column {
                MediaItem(
                    label = "Photo with Employee (with GPS)",
                    uri = uiState.data.photoWithEmployeeUri,
                    isError = uiState.fieldErrors["photoEmployee"] != null
                ) {
                    if (locationPermissionState.status.isGranted) {
                        LocationUtils.getCurrentLocation(context) { location ->
                            viewModel.updateData { it.copy(employeeGpsLocation = location) }
                            openSourcePicker(DocumentType.PHOTO_EMPLOYEE)
                        }
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }
                AnimatedVisibility(
                    visible = uiState.data.employeeGpsLocation.isNotEmpty(),
                    enter = fadeIn(tween(300)) + expandVertically(tween(300))
                ) {
                    Text(
                        "GPS: ${uiState.data.employeeGpsLocation}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(160) {
            Column {
                MediaItem(
                    label = "Shop Photo (with GPS)",
                    uri = uiState.data.shopPhotoUri,
                    isError = uiState.fieldErrors["shopPhoto"] != null
                ) {
                    if (locationPermissionState.status.isGranted) {
                        LocationUtils.getCurrentLocation(context) { location ->
                            viewModel.updateData { it.copy(shopGpsLocation = location) }
                            openSourcePicker(DocumentType.SHOP_PHOTO)
                        }
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }
                AnimatedVisibility(
                    visible = uiState.data.shopGpsLocation.isNotEmpty(),
                    enter = fadeIn(tween(300)) + expandVertically(tween(300))
                ) {
                    Text(
                        "GPS: ${uiState.data.shopGpsLocation}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }
            }
        }
    }
}

private enum class DocumentType {
    ADHAR_FRONT, ADHAR_BACK, PAN_FRONT, PAN_BACK, ADDITIONAL_DOC, PHOTO_EMPLOYEE, SHOP_PHOTO
}

private fun handleImageSelection(
    context: Context,
    uri: Uri,
    type: DocumentType?,
    viewModel: RegistrationViewModel,
    scope: CoroutineScope
) {
    scope.launch {
        val compressedUri = ImageUtils.compressImage(context, uri)
        when (type) {
            DocumentType.ADHAR_FRONT -> viewModel.updateData { it.copy(adharFrontUri = compressedUri) }
            DocumentType.ADHAR_BACK -> viewModel.updateData { it.copy(adharBackUri = compressedUri) }
            DocumentType.PAN_FRONT -> viewModel.updateData { it.copy(panFrontUri = compressedUri) }
            DocumentType.PAN_BACK -> viewModel.updateData { it.copy(panBackUri = compressedUri) }
            DocumentType.ADDITIONAL_DOC -> viewModel.updateData { it.copy(additionalDocUri = compressedUri) }
            DocumentType.PHOTO_EMPLOYEE -> viewModel.updateData { it.copy(photoWithEmployeeUri = compressedUri) }
            DocumentType.SHOP_PHOTO -> viewModel.updateData { it.copy(shopPhotoUri = compressedUri) }
            null -> {}
        }
    }
}

private fun createTmpFileUri(context: Context): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tmpFile)
}

@Composable
fun MediaItem(
    label: String,
    uri: Uri?,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "mediaPressScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (uri != null) BrandPrimary else if (isError) MaterialTheme.colorScheme.error.copy(
            alpha = 0.6f
        ) else BorderLight,
        animationSpec = tween(300),
        label = "mediaBorder"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp)
            .scale(pressScale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = if (uri != null) 4.dp else 0.dp,
        shadowElevation = if (uri != null) 6.dp else 0.dp,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Slight dark overlay to make icons/checkmark pop
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
                )
            }

            if (uri == null) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isError) MaterialTheme.colorScheme.error else Color.DarkGray,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 16.dp)
                )
            }

            // Icons overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Left status icon (Check circle if uploaded)
                if (uri != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .align(Alignment.CenterStart)
                            .clip(CircleShape)
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Right action icon (Camera/Add)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StepSecurity(uiState: RegistrationUiState, viewModel: RegistrationViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }
    var pinVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StepHeader(stepTitles[4] + " Setup", stepSubtitles[4])

        AnimatedField(0) {
            CustomOutlinedTextField(
                value = uiState.data.password,
                onValueChange = { p -> viewModel.updateData("password") { it.copy(password = p) } },
                placeholder = "Create Password",
                leadingIcon = Icons.Default.Lock,
                errorMessage = uiState.fieldErrors["password"],
                isPassword = true,
                isPasswordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible }
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(60) {
            CustomOutlinedTextField(
                value = uiState.data.confirmPassword,
                onValueChange = { cp ->
                    viewModel.updateData("confirmPassword") {
                        it.copy(
                            confirmPassword = cp
                        )
                    }
                },
                placeholder = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                errorMessage = uiState.fieldErrors["confirmPassword"],
                isPassword = true,
                isPasswordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible }
            )
        }
        Spacer(Modifier.height(24.dp))

        AnimatedField(120) {
            CustomOutlinedTextField(
                value = uiState.data.pin,
                onValueChange = { pin ->
                    if (pin.length <= 4 && pin.all { it.isDigit() }) {
                        viewModel.updateData("pin") { it.copy(pin = pin) }
                    }
                },
                placeholder = "Create PIN",
                leadingIcon = Icons.Default.Dialpad,
                errorMessage = uiState.fieldErrors["pin"],
                isPassword = true,
                isPasswordVisible = pinVisible,
                onPasswordToggle = { pinVisible = !pinVisible },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
        }
        Spacer(Modifier.height(16.dp))

        AnimatedField(180) {
            CustomOutlinedTextField(
                value = uiState.data.confirmPin,
                onValueChange = { cp ->
                    if (cp.length <= 4 && cp.all { it.isDigit() }) {
                        viewModel.updateData("confirmPin") { it.copy(confirmPin = cp) }
                    }
                },
                placeholder = "Confirm PIN",
                leadingIcon = Icons.Default.Dialpad,
                errorMessage = uiState.fieldErrors["confirmPin"],
                isPassword = true,
                isPasswordVisible = pinVisible,
                onPasswordToggle = { pinVisible = !pinVisible },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
        }

        if (uiState.error != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/** Full-screen scrim with a spring-scaled checkmark shown right after registration succeeds. */
@Composable
fun SuccessOverlay(visible: Boolean) {
    val checkScale = remember { Animatable(0f) }
    val ringProgress = remember { Animatable(0f) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            checkScale.snapTo(0f)
            ringProgress.snapTo(0f)
            titleVisible = false
            subtitleVisible = false

            checkScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            launch {
                ringProgress.animateTo(
                    1f,
                    animationSpec = infiniteRepeatable(tween(1400, easing = LinearOutSlowInEasing))
                )
            }
            delay(150)
            titleVisible = true
            delay(120)
            subtitleVisible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(150))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(96.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Two radiating rings that expand and fade, staggered half a cycle apart
                        for (i in 0..1) {
                            val phase = (ringProgress.value + i * 0.5f) % 1f
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .scale(1f + phase * 0.8f)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = (1f - phase) * 0.35f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(checkScale.value)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            BrandPrimary,
                                            BrandPrimaryDark
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    AnimatedVisibility(
                        visible = titleVisible,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                    ) {
                        Text(
                            "Registration Successful!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BrandPrimary
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    AnimatedVisibility(
                        visible = subtitleVisible,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                    ) {
                        Text(
                            "Setting things up for you...",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}